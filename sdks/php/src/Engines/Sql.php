<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Analytical SQL engine: `b4n1-db sql <verb>`. */
final class Sql extends Engine
{
    /** @return array<string,mixed> */
    public function execute(string $statement): array
    {
        return $this->call(['sql', 'execute', $statement]);
    }

    /** @return array<string,mixed> */
    public function query(string $statement): array
    {
        return $this->call(['sql', 'query', $statement]);
    }

    /** @return array<string,mixed> */
    public function tables(): array
    {
        return $this->call(['sql', 'tables']);
    }

    /** @return array<string,mixed> */
    public function registerCsv(string $table, string $file): array
    {
        return $this->call(['sql', 'register-csv', $table, $file]);
    }

    /** @return array<string,mixed> */
    public function registerJson(string $table, string $file): array
    {
        return $this->call(['sql', 'register-json', $table, $file]);
    }

    /** @return array<string,mixed> */
    public function registerParquet(string $table, string $file): array
    {
        return $this->call(['sql', 'register-parquet', $table, $file]);
    }

    /** @return array<string,mixed> */
    public function drop(string $table): array
    {
        return $this->call(['sql', 'drop', $table]);
    }
}
