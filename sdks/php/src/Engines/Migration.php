<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Schema migration registry: `b4n1-db migration <verb>`. */
final class Migration extends Engine
{
    /** @return array<string,mixed> */
    public function list(): array
    {
        return $this->call(['migration', 'list']);
    }

    /** @return array<string,mixed> */
    public function add(int $version, string $name, string $upSql): array
    {
        return $this->call(['migration', 'add', (string) $version, $name, $upSql]);
    }

    /** @return array<string,mixed> */
    public function apply(): array
    {
        return $this->call(['migration', 'apply']);
    }

    /** @return array<string,mixed> */
    public function exec(): array
    {
        return $this->call(['migration', 'exec']);
    }

    /** @return array<string,mixed> */
    public function rollback(int $target): array
    {
        return $this->call(['migration', 'rollback', (string) $target]);
    }
}
