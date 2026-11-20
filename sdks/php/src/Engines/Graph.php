<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Knowledge graph engine: `b4n1-db graph <verb>`. */
final class Graph extends Engine
{
    /** Node types the engine accepts; anything else is refused. */
    public const NODE_TYPES = [
        'instruction', 'intention', 'contract', 'pattern', 'scaffold', 'qapair', 'cyclerecord',
    ];

    /** @return array<string,mixed> */
    public function addNode(string $type, string $content): array
    {
        return $this->call(['graph', 'add-node', $type, $content]);
    }

    /** @return array<string,mixed> */
    public function addRelation(string $from, string $to, string $relation, ?float $weight = null): array
    {
        $args = ['graph', 'add-relation', $from, $to, $relation];
        if ($weight !== null) {
            $args[] = (string) $weight;
        }

        return $this->call($args);
    }

    /** @return array<string,mixed> */
    public function node(string $id): array
    {
        return $this->call(['graph', 'node', $id]);
    }

    /** @return array<string,mixed> */
    public function search(string $query, ?int $limit = null): array
    {
        $args = ['graph', 'search', $query];
        if ($limit !== null) {
            $args[] = (string) $limit;
        }

        return $this->call($args);
    }

    /** @return array<string,mixed> */
    public function forget(string $id): array
    {
        return $this->call(['graph', 'forget', $id]);
    }
}
