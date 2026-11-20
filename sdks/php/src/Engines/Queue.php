<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

/** Queue engine: `b4n1-db queue <verb>`. */
final class Queue extends Engine
{
    /** @return array<string,mixed> */
    public function push(string $queue, string $body): array
    {
        return $this->call(['queue', 'push', $queue, $body]);
    }

    /** @return array<string,mixed> */
    public function pop(string $queue): array
    {
        return $this->call(['queue', 'pop', $queue]);
    }

    /** @return array<string,mixed> */
    public function peek(string $queue): array
    {
        return $this->call(['queue', 'peek', $queue]);
    }

    /** @return array<string,mixed> */
    public function ack(string $queue, string $id): array
    {
        return $this->call(['queue', 'ack', $queue, $id]);
    }

    /** @return array<string,mixed> */
    public function nack(string $queue, string $id): array
    {
        return $this->call(['queue', 'nack', $queue, $id]);
    }

    /** @return array<string,mixed> */
    public function clear(string $queue): array
    {
        return $this->call(['queue', 'clear', $queue]);
    }

    /** @return array<string,mixed> */
    public function len(string $queue): array
    {
        return $this->call(['queue', 'len', $queue]);
    }
}
