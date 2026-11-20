<?php

declare(strict_types=1);

namespace B4n1\Db\Engines;

use B4n1\Db\Db;

/**
 * Base for the nine engine groups: argument marshalling only.
 *
 * Each engine of the `b4n1-db` binary is one group on the handle
 * (`$db->kv()`, `$db->sql()`, …). The contract is `sdk-surface.tsv` in the
 * crate root and the suite fails when a wrapper drifts from it.
 */
abstract class Engine
{
    public function __construct(protected Db $db)
    {
    }

    /**
     * Run a verb and return its `--json` answer.
     *
     * @param list<string> $args
     * @return array<string,mixed>
     */
    protected function call(array $args): array
    {
        return $this->db->invoke($args);
    }

    /** Serialise an object/array for a JSON flag; null stays empty. */
    protected function json(mixed $value): string
    {
        if ($value === null) {
            return '';
        }

        return (string) json_encode($value, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
    }

    /** Encode one scalar as a JSON value (`find-by-field` parses its argument). */
    protected function jsonValue(mixed $value): string
    {
        return (string) json_encode($value, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
    }
}
