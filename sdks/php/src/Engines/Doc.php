<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Document collection engine: `b4n1-db doc <verb>`. */
final class Doc extends Engine
{
    /** @param array<string,mixed>|string $payload @return array<string,mixed> */
    public function insert(string $collection, array|string $payload): array
    {
        return $this->call(['doc', 'insert', $collection, $this->json($payload)]);
    }

    /** @return array<string,mixed> */
    public function findOne(string $collection, string $id): array
    {
        return $this->call(['doc', 'find-one', $collection, $id]);
    }

    /** @return array<string,mixed> */
    public function find(string $collection): array
    {
        return $this->call(['doc', 'find', $collection]);
    }

    /** @return array<string,mixed> */
    public function findByField(string $collection, string $field, mixed $value): array
    {
        return $this->call(['doc', 'find-by-field', $collection, $field, $this->jsonValue($value)]);
    }

    /** @param array<string,mixed>|string $payload @return array<string,mixed> */
    public function update(string $collection, string $id, array|string $payload): array
    {
        return $this->call(['doc', 'update', $collection, $id, $this->json($payload)]);
    }

    /** @return array<string,mixed> */
    public function delete(string $collection, string $id): array
    {
        return $this->call(['doc', 'delete', $collection, $id]);
    }
}
