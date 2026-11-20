<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Vector engine: `b4n1-db vector <verb>`. */
final class Vector extends Engine
{
    /**
     * @param list<float> $vector
     * @param array<string,mixed>|null $metadata
     * @return array<string,mixed>
     */
    public function insert(string $namespace, array $vector, ?array $metadata = null): array
    {
        return $this->call(['vector', 'insert', $namespace, $this->json($vector), $this->json($metadata)]);
    }

    /**
     * @param list<float> $query
     * @param array<string,mixed>|null $filter
     * @return array<string,mixed>
     */
    public function search(string $namespace, array $query, ?int $topK = null, ?array $filter = null): array
    {
        $args = ['vector', 'search', $namespace, $this->json($query)];
        if ($topK !== null || $filter !== null) {
            $args[] = (string) ($topK ?? 5);
        }
        if ($filter !== null) {
            $args[] = $this->json($filter);
        }

        return $this->call($args);
    }

    /** @return array<string,mixed> */
    public function delete(string $namespace, string $id): array
    {
        return $this->call(['vector', 'delete', $namespace, $id]);
    }

    /** @return array<string,mixed> */
    public function list(string $namespace): array
    {
        return $this->call(['vector', 'list', $namespace]);
    }
}
