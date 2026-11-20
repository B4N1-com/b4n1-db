<?php

declare(strict_types=1);

/**
 * Every engine of the binary, exercised through the PHP SDK.
 *
 * Plain PHP, no PHPUnit: the suite runs this file directly with
 * `php tests/surface_test.php` and B4N1_DB_BIN pointing at the build.
 */

require_once __DIR__ . '/../src/Db.php';
foreach (['Engine', 'Kv', 'Sql', 'Graph', 'Doc', 'Queue', 'Vector', 'Audit', 'Migration', 'Transfer'] as $class) {
    require_once __DIR__ . '/../src/Engines/' . $class . '.php';
}

use B4n1\Db\Db;

$failures = 0;
$checks = 0;

function check(string $what, bool $condition): void
{
    global $failures, $checks;
    $checks++;
    if (!$condition) {
        $failures++;
        fwrite(STDERR, "  FAIL {$what}\n");
    }
}

function tmpdir(): string
{
    $dir = sys_get_temp_dir() . '/b4n1-db-php-' . bin2hex(random_bytes(6));
    mkdir($dir, 0777, true);

    return $dir;
}

$dir = tmpdir();
$db = new Db($dir . '/data');

// kv
check('kv.put', $db->kv()->put('k', 'v')['ok'] === true);
check('kv.get', $db->kv()->get('k') === ['key' => 'k', 'value' => 'v']);
check('kv.scan', $db->kv()->scan('')['count'] === 1);
check('kv.put_ttl', $db->kv()->putTtl('t', 'x', 60)['ttl_secs'] === 60);
check('kv.ttl', $db->kv()->ttl('t')['ttl_secs'] > 0);
check('kv.delete', $db->kv()->delete('k')['ok'] === true);
check('kv.purge_expired', $db->kv()->purgeExpired()['purged'] === 0);
try {
    $db->kv()->get('k');
    check('kv.get missing must throw', false);
} catch (RuntimeException) {
    check('kv.get missing throws', true);
}

// sql
check('sql.execute', $db->sql()->execute('CREATE TABLE u (id BIGINT, name VARCHAR)')['rows_affected'] === 0);
check('sql.execute insert', $db->sql()->execute("INSERT INTO u VALUES (1, 'ada')")['rows_affected'] === 1);
check('sql.query', count($db->sql()->query('SELECT * FROM u')['rows']) === 1);
check('sql.tables', $db->sql()->tables()['tables'] === ['u']);

$second = new Db($dir . '/data');
check('sql survives a second handle', $second->sql()->query('SELECT name FROM u')['rows'] === [['name' => 'ada']]);

$csv = $dir . '/rows.csv';
file_put_contents($csv, "id\n7\n");
check('sql.register_csv', $db->sql()->registerCsv('c1', $csv)['ok'] === true);
check('sql.query csv', $db->sql()->query('SELECT id FROM c1')['rows'] === [['id' => 7]]);
check('sql.drop', $second->sql()->drop('u')['ok'] === true);
check('sql.tables after drop', !in_array('u', $second->sql()->tables()['tables'], true));

// graph
$node = $db->graph()->addNode('pattern', 'ada');
$other = $db->graph()->addNode('pattern', 'grace');
check('graph.add_node', strlen($node['id']) === 36);
check('graph.node', is_array($db->graph()->node($node['id'])['node']));
check('graph.search', $db->graph()->search('ada', 5)['count'] >= 1);
check('graph.add_relation', $db->graph()->addRelation($node['id'], $other['id'], 'follows', 0.5)['relation'] === 'follows');
check('graph.forget', $db->graph()->forget($node['id'])['ok'] === true);

// documents
$inserted = $db->doc()->insert('users', ['name' => 'ada', 'age' => 36]);
check('doc.insert', $inserted['collection'] === 'users');
check('doc.find_one', $db->doc()->findOne('users', $inserted['id'])['document']['_id'] === $inserted['id']);
check('doc.find', $db->doc()->find('users')['count'] === 1);
check('doc.update', $db->doc()->update('users', $inserted['id'], ['age' => 37])['ok'] === true);
check('doc.find_by_field', $db->doc()->findByField('users', 'name', 'ada')['count'] === 1);
check('doc.delete', $db->doc()->delete('users', $inserted['id'])['ok'] === true);
check('doc.find after delete', $db->doc()->find('users')['count'] === 0);

// queue
$id = $db->queue()->push('jobs', 'build')['id'];
check('queue.len', $db->queue()->len('jobs')['pending'] === 1);
check('queue.peek', $db->queue()->peek('jobs')['body'] === 'build');
check('queue.pop', $db->queue()->pop('jobs')['id'] === $id);
check('queue.nack', $db->queue()->nack('jobs', $id)['requeued'] === true);
$db->queue()->pop('jobs');
check('queue.ack', $db->queue()->ack('jobs', $id)['ok'] === true);
check('queue.clear', $db->queue()->clear('jobs')['ok'] === true);

// vectors
$embedding = array_merge([1.0], array_fill(0, 1535, 0.0));
$record = $db->vector()->insert('ns', $embedding, ['tag' => 'a']);
check('vector.insert', strlen($record['id']) === 36);
check('vector.search', $db->vector()->search('ns', $embedding, 3, ['tag' => 'a'])['hits'][0]['id'] === $record['id']);
check('vector.list', $db->vector()->list('ns')['count'] === 1);
check('vector.delete', $db->vector()->delete('ns', $record['id'])['ok'] === true);

// audit
$db->kv()->put('a', '1');
check('audit.verify', $db->audit()->verify()['intact'] === true);
check('audit.history', count($db->audit()->history('a')['nodes']) === 1);
$latest = $db->audit()->latest('a')['node'];
check('audit.latest', $latest['key'] === 'a');
check('audit.time_travel', $db->audit()->timeTravel('a', $latest['timestamp'])['node']['hash'] === $latest['hash']);
check('audit.root_hash', strlen($db->audit()->rootHash()['root_hash']) === 64);

// migrations
check('migration.add', $db->migration()->add(1, 'init', 'CREATE TABLE m (id BIGINT)')['registered'] === true);
$third = new Db($dir . '/data');
check('migration.list', $third->migration()->list()['registered'] === 1);
check('migration.exec', $third->migration()->exec()['executed'] === [1]);
check('migration.apply', $third->migration()->apply()['ddl_executed'] === false);
check('migration.rollback', $third->migration()->rollback(0)['rolled_back'] === [1]);
try {
    $third->migration()->add(1, 'again', 'SELECT 1');
    check('migration.add duplicate must throw', false);
} catch (RuntimeException) {
    check('migration.add duplicate throws', true);
}

// transfer
$db->kv()->put('keep', 'me');
check('transfer.backup', $db->transfer()->backup($dir . '/bak')['files'] > 0);
check('transfer.export', $db->transfer()->export($dir . '/dump.jsonl')['bytes'] > 0);
check('transfer.import', $db->transfer()->import($dir . '/dump.jsonl')['records'] >= 1);
check('transfer.restore', $db->transfer()->restore($dir . '/bak')['ok'] === true);

// stats and the 0.1 aliases
check('stats', $db->stats()['kv_entries'] >= 1);
check('put alias', $db->put('pk', 'pv') === true);
check('get alias', $db->get('pk') === ['key' => 'pk', 'value' => 'pv']);
check('demo', str_contains($db->demo(), 'database'));

$it = new RecursiveIteratorIterator(
    new RecursiveDirectoryIterator($dir, FilesystemIterator::SKIP_DOTS),
    RecursiveIteratorIterator::CHILD_FIRST
);
foreach ($it as $entry) {
    $entry->isDir() ? rmdir($entry->getPathname()) : unlink($entry->getPathname());
}
rmdir($dir);

printf("%d checks, %d failure(s)\n", $checks, $failures);
exit($failures === 0 ? 0 : 1);
