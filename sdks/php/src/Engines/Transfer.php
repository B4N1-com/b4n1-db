<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Backup and transfer engine: `b4n1-db transfer <verb>`. */
final class Transfer extends Engine
{
    /** @return array<string,mixed> */
    public function backup(string $destination): array
    {
        return $this->call(['transfer', 'backup', $destination]);
    }

    /** @return array<string,mixed> */
    public function restore(string $source): array
    {
        return $this->call(['transfer', 'restore', $source]);
    }

    /** @return array<string,mixed> */
    public function export(string $destination): array
    {
        return $this->call(['transfer', 'export', $destination]);
    }

    /** @return array<string,mixed> */
    public function import(string $source): array
    {
        return $this->call(['transfer', 'import', $source]);
    }
}
