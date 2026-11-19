# The Eight Engines

One store, eight surfaces, all reachable from the same handle and the same
command.

| Engine | What it gives you | What it does not |
|---|---|---|
| **Key-value** | ordered keys, TTL, prefix scans, atomic batch writes | no cross-key transaction |
| **SQL** | `CREATE` / `INSERT` / `SELECT`, injection guard, row-level tenant predicate | not a full SQL dialect |
| **Graph** | typed nodes, weighted relations, traversal, BM25 + adjacency hybrid search | not a distributed graph |
| **Documents** | structured records, per-field indexes, queries | no joins with SQL tables |
| **Queues** | durable FIFO with `ack` / `nack` / `peek` / `clear` | at-least-once, not exactly-once |
| **Vectors** | cosine similarity with metadata filtering | no hosted embedding service |
| **Audit** | append-only Merkle DAG, root hash, per-key history, time travel | it detects tampering; it does not prevent it |
| **Migrations** | versioned registry with checksum, batch rollback | no automatic schema diffing |

## Key-value

The base every other engine sits on. A write is durable and atomic **per key**.
TTL is enforced lazily on read and can also be reclaimed eagerly.

```bash
b4n1-db kv put k v
b4n1-db kv put-ttl session token 60
b4n1-db kv ttl session
b4n1-db kv scan user:
b4n1-db kv purge-expired
```

From Python: `db.kv_put("k", "v")` and `db.kv_get("k")`. A value that is not
valid UTF-8 comes back base64-encoded.

## SQL

An analytical engine on top of the same store. Tables are persisted, so they
survive a reopen. Statements pass through an injection guard, and a `SELECT` is
wrapped in the current tenant's row predicate automatically.

```bash
b4n1-db sql execute "CREATE TABLE events (id INTEGER PRIMARY KEY, kind TEXT)"
b4n1-db sql execute "INSERT INTO events (kind) VALUES ('open')"
b4n1-db sql query "SELECT kind, count(*) FROM events GROUP BY kind"
b4n1-db sql tables
```

## Graph

Typed nodes and weighted relations over the same structure, with two search
modes: **BM25** over node text and **adjacency** over the relations. Hybrid
search blends both, which is what makes "related to" queries behave sensibly
instead of returning keyword noise.

## Documents

Records with per-field indexes. Collections are independent, queries stream, and
`_`-prefixed keys in a payload are dropped — they are the document's own
identity fields, never user data.

```bash
b4n1-db doc insert orders '{"total": 42}'
b4n1-db doc find-by-field orders total 42
b4n1-db doc update orders <id> '{"total": 43}'
```

## Queues

FIFO with at-least-once delivery. `pop` parks the payload in an ack slot, so an
unacknowledged message is redelivered instead of lost.

```bash
b4n1-db queue push jobs '{"kind":"resize"}'
b4n1-db queue pop jobs          # returns an id with the payload
b4n1-db queue ack jobs <id>     # done
b4n1-db queue nack jobs <id>    # put it back now
b4n1-db queue len jobs
```

## Vectors

Cosine similarity with metadata filtering. Vectors are JSON arrays of numbers; a
filter is a JSON object of required pairs.

```bash
b4n1-db vec insert docs '[0.1, 0.2, 0.3]' '{"lang":"en"}'
b4n1-db vec search docs '[0.1, 0.2, 0.3]' 5 '{"lang":"en"}'
b4n1-db vec delete docs <id>
```

## Audit

Every write appends a node to a Merkle DAG. `verify` recomputes each hash and
fails at the first mismatch; `root-hash` folds all of them, so any addition,
removal or modification changes it. Per-key history and point-in-time
reconstruction come from the same DAG.

```bash
b4n1-db audit verify
b4n1-db audit history greeting
b4n1-db audit time-travel greeting 1700000000000
b4n1-db audit root-hash
```

## Migrations

A versioned registry with a checksum per step. `apply` registers versions
**without** running DDL; `exec` runs them for real and rolls the whole batch
back on failure. The separation exists so a deploy can record intent and a
separate step can act on it.

```bash
b4n1-db migration add 1 create-users "CREATE TABLE users (id INTEGER PRIMARY KEY)"
b4n1-db migration apply
b4n1-db migration exec
b4n1-db migration rollback 0
```

## Moving data

```bash
b4n1-db transfer backup ./backup
b4n1-db transfer restore ./backup        # forward-only merge, never overwrites
b4n1-db transfer export ./dump.jsonl     # JSON Lines, base64 values
b4n1-db transfer import ./dump.jsonl
```

## Integrity boundary

Durability and atomicity are **per key**. There is no multi-operation
transaction and no two-phase commit. When a group of writes must land together,
write them as one atomic batch instead of a sequence of separate puts.
