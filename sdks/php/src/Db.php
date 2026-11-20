<?php

declare(strict_types=1);

namespace B4n1\Db;

use B4n1\Db\Engines\Audit;
use B4n1\Db\Engines\Doc;
use B4n1\Db\Engines\Graph;
use B4n1\Db\Engines\Kv;
use B4n1\Db\Engines\Migration;
use B4n1\Db\Engines\Queue;
use B4n1\Db\Engines\Sql;
use B4n1\Db\Engines\Transfer;
use B4n1\Db\Engines\Vector;

/**
 * Thin PHP wrapper over the `b4n1-db` standalone binary.
 *
 * All storage, SQL, graph, vector and audit logic lives in the binary; this
 * class only invokes it. Each engine is a group: `$db->kv()`, `$db->sql()`,
 * `$db->graph()`, `$db->doc()`, `$db->queue()`, `$db->vector()`, `$db->audit()`,
 * `$db->migration()`, `$db->transfer()`.
 */
final class Db
{
    public const VERSION = '0.1.2';

    private string $path;

    private ?Kv $kv = null;
    private ?Sql $sql = null;
    private ?Graph $graph = null;
    private ?Doc $doc = null;
    private ?Queue $queue = null;
    private ?Vector $vector = null;
    private ?Audit $audit = null;
    private ?Migration $migration = null;
    private ?Transfer $transfer = null;

    public function __construct(string $path = './data')
    {
        $this->path = $path;
    }

    public function kv(): Kv
    {
        return $this->kv ??= new Kv($this);
    }

    public function sql(): Sql
    {
        return $this->sql ??= new Sql($this);
    }

    public function graph(): Graph
    {
        return $this->graph ??= new Graph($this);
    }

    public function doc(): Doc
    {
        return $this->doc ??= new Doc($this);
    }

    public function queue(): Queue
    {
        return $this->queue ??= new Queue($this);
    }

    public function vector(): Vector
    {
        return $this->vector ??= new Vector($this);
    }

    public function audit(): Audit
    {
        return $this->audit ??= new Audit($this);
    }

    public function migration(): Migration
    {
        return $this->migration ??= new Migration($this);
    }

    public function transfer(): Transfer
    {
        return $this->transfer ??= new Transfer($this);
    }

    /**
     * Run a verb and return its `--json` answer (the engine groups use this).
     *
     * @param list<string> $args
     * @return array<string,mixed>
     */
    public function invoke(array $args): array
    {
        return $this->decode($this->run(true, $args));
    }

    /** Resolves the binary: B4N1_DB_BIN, then PATH. */
    public static function binary(): string
    {
        $env = getenv('B4N1_DB_BIN');
        if (is_string($env) && is_file($env)) {
            return $env;
        }
        foreach (explode(PATH_SEPARATOR, (string) getenv('PATH')) as $dir) {
            $candidate = $dir . DIRECTORY_SEPARATOR . 'b4n1-db';
            if (is_file($candidate)) {
                return $candidate;
            }
        }
        throw new \RuntimeException(
            'b4n1-db binary not found; set B4N1_DB_BIN or install the b4n1-db binary'
        );
    }

    /** @return array<string,mixed> */
    public function stats(): array
    {
        return $this->decode($this->run(true, ['stats']));
    }

    /** Alias of `$db->kv()->put(...)`, kept from 0.1. */
    public function put(string $key, string $value): bool
    {
        $this->kv()->put($key, $value);

        return true;
    }

    /** Alias of `$db->kv()->get(...)`, kept from 0.1. @return array<string,mixed> */
    public function get(string $key): array
    {
        return $this->kv()->get($key);
    }

    public function demo(): string
    {
        return $this->run(false, ['demo']);
    }

    /** @param list<string> $args */
    private function run(bool $jsonOut, array $args): string
    {
        $cmd = array_merge([self::binary(), '--db', $this->path]);
        if ($jsonOut) {
            $cmd[] = '--json';
        }
        $cmd = array_merge($cmd, $args);

        $descriptors = [1 => ['pipe', 'w'], 2 => ['pipe', 'w']];
        $proc = proc_open($cmd, $descriptors, $pipes);
        if (!is_resource($proc)) {
            throw new \RuntimeException('failed to start ' . $cmd[0]);
        }
        $out = stream_get_contents($pipes[1]);
        $err = stream_get_contents($pipes[2]);
        fclose($pipes[1]);
        fclose($pipes[2]);
        $code = proc_close($proc);
        if ($code !== 0) {
            throw new \RuntimeException(trim((string) ($err !== '' ? $err : $out)));
        }

        return (string) $out;
    }

    /** @return array<string,mixed> */
    private function decode(string $json): array
    {
        $data = json_decode($json, true, 512, JSON_THROW_ON_ERROR);
        if (!is_array($data)) {
            throw new \RuntimeException('unexpected non-object JSON from b4n1-db');
        }

        return $data;
    }
}
