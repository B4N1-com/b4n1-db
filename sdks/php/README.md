# b4n1-db — PHP SDK

Thin PHP wrapper over the `b4n1-db` standalone binary. All storage, SQL, graph,
vector and audit logic lives in the binary; this package only invokes it.

## Install

```bash
composer require b4n1/b4n1-db
```

The binary is resolved in this order: `B4N1_DB_BIN`, then `b4n1-db` on `PATH`.

## Usage

```php
<?php
require 'vendor/autoload.php';

use B4n1\Db\Db;

$db = new Db('./data');

$db->kv()->put('greeting', 'hello');
var_dump($db->kv()->get('greeting'));      // ['key' => 'greeting', 'value' => 'hello']

$db->sql()->execute('CREATE TABLE u (id BIGINT, name VARCHAR)');
$db->sql()->execute("INSERT INTO u VALUES (1, 'ada')");
var_dump($db->sql()->query('SELECT name FROM u'));

var_dump($db->graph()->addNode('pattern', 'ada'));   // ['ok' => true, 'id' => '…']
$db->doc()->insert('notes', ['text' => 'first']);
$db->queue()->push('jobs', 'build');
$db->audit()->verify();
```

## Engines

| Group | Methods |
|---|---|
| `kv()` | put, get, delete, scan, putTtl, ttl, purgeExpired |
| `sql()` | execute, query, tables, registerCsv, registerJson, registerParquet, drop |
| `graph()` | addNode, addRelation, node, search, forget |
| `doc()` | insert, findOne, find, findByField, update, delete |
| `queue()` | push, pop, peek, ack, nack, clear, len |
| `vector()` | insert, search, delete, list |
| `audit()` | verify, history, latest, timeTravel, rootHash |
| `migration()` | list, add, apply, exec, rollback |
| `transfer()` | backup, restore, export, import |

Vectors use the database dimension (1536 by default). Graph node types are
`instruction`, `intention`, `contract`, `pattern`, `scaffold`, `qapair` and
`cyclerecord`.

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db php tests/surface_test.php
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
