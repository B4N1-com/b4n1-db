<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Merkle-DAG audit engine: `b4n1-db audit <verb>`. */
final class Audit extends Engine
{
    /** @return array<string,mixed> */
    public function verify(): array
    {
        return $this->call(['audit', 'verify']);
    }

    /** @return array<string,mixed> */
    public function history(string $key): array
    {
        return $this->call(['audit', 'history', $key]);
    }

    /** @return array<string,mixed> */
    public function latest(string $key): array
    {
        return $this->call(['audit', 'latest', $key]);
    }

    /** @return array<string,mixed> */
    public function timeTravel(string $key, int $timestampMs): array
    {
        return $this->call(['audit', 'time-travel', $key, (string) $timestampMs]);
    }

    /** @return array<string,mixed> */
    public function rootHash(): array
    {
        return $this->call(['audit', 'root-hash']);
    }
}
