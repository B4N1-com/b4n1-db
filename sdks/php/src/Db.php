<?php

declare(strict_types=1);

namespace B4n1\Db;

/**
 * Thin PHP wrapper over the `b4n1-db` standalone binary.
 *
 * All storage, SQL, graph, vector and audit logic lives in the binary; this
 * class only invokes it.
 */
final class Db
{
    public const VERSION = '0.1.0';

    private string $path;

    public function __construct(string $path = './data')
    {
        $this->path = $path;
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

    public function put(string $key, string $value): bool
    {
        $this->run(false, ['kv', 'put', $key, $value]);

        return true;
    }

    /** @return array<string,mixed> */
    public function get(string $key): array
    {
        return $this->decode($this->run(true, ['kv', 'get', $key]));
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
