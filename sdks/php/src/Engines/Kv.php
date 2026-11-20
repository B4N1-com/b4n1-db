<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Key-value engine: `b4n1-db kv <verb>`. */
final class Kv extends Engine
{
    /** @return array<string,mixed> */
    public function put(string $key, string $value): array
    {
        return $this->call(['kv', 'put', $key, $value]);
    }

    /** @return array<string,mixed> */
    public function get(string $key): array
    {
        return $this->call(['kv', 'get', $key]);
    }

    /** @return array<string,mixed> */
    public function delete(string $key): array
    {
        return $this->call(['kv', 'delete', $key]);
    }

    /** @return array<string,mixed> */
    public function scan(string $prefix): array
    {
        return $this->call(['kv', 'scan', $prefix]);
    }

    /** @return array<string,mixed> */
    public function putTtl(string $key, string $value, int $seconds): array
    {
        return $this->call(['kv', 'put-ttl', $key, $value, (string) $seconds]);
    }

    /** @return array<string,mixed> */
    public function ttl(string $key): array
    {
        return $this->call(['kv', 'ttl', $key]);
    }

    /** @return array<string,mixed> */
    public function purgeExpired(): array
    {
        return $this->call(['kv', 'purge-expired']);
    }
}
