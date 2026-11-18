# b4n1-db

**Sovereign embedded data layer for the B4N1 ecosystem.** One handle (`SovereignDB`)
over eight local storage engines — no client/server database, no network engine,
no daemon, no C dependencies.

> **Status: library only + standalone binary.** This crate ships a Rust library AND a
> standalone `b4n1-db` CLI (KV, SQL, graph, documents, queues, vectors, audit,
> migrations, transfer, plus an offline `demo` and `docs`). It has **no MCP
> server** and **no published SDK packages yet** (SDK scaffolding is in
> progress). Anything a README of this crate claims must be verifiable in this
> repository — if you do not find it here, it does not exist.

---

## Table of contents

- [Identity](#identity)
- [Install](#install)
- [Quick start](#quick-start)
- [Subsystems](#subsystems)
- [Encryption at rest (HESP)](#encryption-at-rest-hesp)
- [Multi-tenancy](#multi-tenancy)
- [Feature flags](#feature-flags)
- [Integrity and replication](#integrity-and-replication)
- [Consumers](#consumers)
- [Testing](#testing)
- [License](#license)

---

## Identity

| | |
|---|---|
| Crate | `b4n1-db` (`private-repos/b4n1-db`) |
| Tier | 0 — no dependency on any other B4N1 crate |
| Kind | **Rust library** (`lib`) + **standalone binary** (`b4n1-db`, `[[bin]]`, requires `sql`+`documents`) |
| Packaging | Private monorepo only. `publish = false`. |
| Daemon | None. It is embedded into the process that opens it. |
| External services | None. Sled, the SQL engine, the audit DAG and the crypto are all in-process. |

**What it is not:** it is not a distributed database, it does not open a network
port, and it does not talk to a remote server. There is no client/server mode.

---

## Install

b4n1-db is consumed as a compile-time dependency from inside the monorepo
(§10f: local `path`, never a git tag):

```toml
[dependencies]
# KV + Merkle audit + queues + vectors + knowledge graph, no Arrow, no SQL engine.
b4n1-db = { path = "../b4n1-db", default-features = false }

# Full default profile: adds the analytical SQL engine, documents and audit.
b4n1-db = { path = "../b4n1-db" }
```

`default-features = false` is the cheap profile: it drops the SQL engine and the
Arrow columnar stack entirely, which matters for consumers that only need KV,
queues or the audit DAG.

---

## Quick start

`open` creates the directory layout it needs, so any writable path works.

```rust
use b4n1_db::SovereignDB;

fn main() -> Result<(), String> {
    let dir = std::env::temp_dir().join("b4n1-db-readme-kv");
    let mut db = SovereignDB::open(&dir)?;

    // `put` needs a mutable handle: it seals, audits and then writes.
    db.put("greeting", b"hello")?;

    let value = db.get("greeting")?;
    assert_eq!(value.as_deref(), Some(&b"hello"[..]));

    Ok(())
}
```

Time to live, which is enforced lazily on read and can be purged eagerly:

```rust
use b4n1_db::SovereignDB;

fn main() -> Result<(), String> {
    let dir = std::env::temp_dir().join("b4n1-db-readme-ttl");
    let mut db = SovereignDB::open(&dir)?;

    db.rocks.put_with_ttl("session", b"token", 60)?;
    let remaining = db.rocks.ttl("session")?;
    assert!(remaining.is_some());

    Ok(())
}
```

Analytical SQL. Tables are persisted as Arrow IPC, so they survive a reopen.
Note the two hops: `execute`/`query` live on the analytical engine reached
through the `sql` field, not on `SovereignDB` itself.

```rust
use b4n1_db::SovereignDB;

fn main() -> Result<(), String> {
    let dir = std::env::temp_dir().join("b4n1-db-readme-sql");
    let db = SovereignDB::open(&dir)?;

    db.sql.execute("CREATE TABLE users (id BIGINT, name VARCHAR)")?;
    db.sql.execute("INSERT INTO users VALUES (1, 'ada')")?;

    let result = db.sql.query("SELECT name FROM users")?;
    assert_eq!(result.columns, vec!["name".to_string()]);
    assert_eq!(result.rows.len(), 1);

    Ok(())
}
```

---

## Subsystems

Every subsystem is a public field of `SovereignDB`, so the handle is the whole
API. All of them share one embedded store; nothing needs a server.

| Field | What it gives you |
|---|---|
| `rocks` | Key/value with tenant prefixes, TTL, atomic `batch_write`, streaming scans |
| `sql` | Analytical SQL (`CREATE` / `INSERT` / `SELECT`), tables persisted as Arrow IPC, injection guard, row-level tenant isolation |
| `merkle` | Append-only chained audit log: `verify_integrity()`, `root_hash()`, per-key history |
| `migrations` | Versioned schema registry with checksum, batch rollback, and a real executor |
| `documents` | Documents with per-field indexes, queries and streaming |
| `knowledge` | Graph plus hybrid search (BM25 and graph adjacency), cascading forget, knowledge import |
| `queues` | FIFO queues with `ack` / `nack` / `peek` / `clear` |
| `vectors` | Embedded vector index, cosine similarity, metadata filtering |

On top of the fields: `backup`, `restore_from`, `export_json`, `import_json`,
`replicate_from`, `time_travel`, `stats` and the BLang type system that bridges
records to Arrow and SQL.

**Integrity boundary.** Sled gives durability and atomicity **per key**. This
crate does not provide ACID across multiple operations, and there is no two-phase
commit. Do not design around that assumption.

---

## Encryption at rest (HESP)

`enable_blind_mode()` seals values with AES-256-GCM before they touch the store.
The mode is a property of the data, not of the handle: it is recorded on disk, so
a database reopened later still knows its values are ciphertext.

The key is resolved once, at `open`, in this order and no other:

1. **`B4N1_HESP_KEY`** — 64 hex characters (32 bytes). A malformed value panics
   loudly; it never falls through to a weaker source.
2. **`<data_dir>/hesp.key`** — a co-located keyfile, so the database can be moved
   to another machine and still be read.
3. **Hardware fingerprint** — a hash of stable machine identity. Last resort.
   It ties the data to the machine: change the hostname and the data becomes
   unreadable. **Do not rely on it in production**; set the env var or ship the
   keyfile.

---

## Multi-tenancy

`with_tenant` prefixes every key with `{tenant}:`.

```rust
use b4n1_db::SovereignDB;

fn main() -> Result<(), String> {
    let dir = std::env::temp_dir().join("b4n1-db-readme-tenant");
    let mut db = SovereignDB::open(&dir)?.with_tenant("acme");

    db.put("key", b"value")?;
    // Stored as "acme:key"; a handle without the tenant cannot see it.
    assert!(db.get("key")?.is_some());

    Ok(())
}
```

**This is a key prefix, not an authorization policy.** A handle with a tenant
sees only its prefix; a handle without one sees everything. It is isolation of
accident, not access control.

---

## Feature flags

| Feature | Default | Adds |
|---|---|---|
| `sql` | ✅ | The analytical engine and the Arrow columnar stack |
| `documents` | ✅ | The document collection |
| `audit` | ✅ | Reserved; the audit DAG is always compiled |
| `arrow` | via `sql` | The Arrow/RecordBatch bridge (`BlangType::to_arrow`) |
| `embed` | ❌ | Local ONNX embeddings. Heavy: pulls an inference runtime. |
| `python` | ❌ | The pyo3 bindings |

`sql` and `arrow` are the expensive ones. Consumers that only need KV, queues or
the audit DAG should use `default-features = false` and build with no Arrow and
no SQL engine at all.

---

## Integrity and replication

`verify_integrity()` recomputes the hash of every node in the audit DAG and fails
loudly at the first mismatch. `root_hash()` folds the hashes of all nodes, so any
addition, removal or modification changes it; it is stable across restarts and is
`None` on an empty DAG.

`replicate_from` merges another database **forward only**: it imports keys that
are absent and never overwrites keys that are present. It unions Merkle nodes
rather than choosing a winner, which is what makes the convergence eventual
rather than immediate.

---

## Consumers

Compiled in via a local path dependency by the crates that need it, including
the production data path: `b4n1-projects` (the project API's store),
`b4n1-orchestrator` (its ledger) and `b4n1-bio` (its findings store, with a
Merkle node per finding).

A change to the public API is **breaking** for every one of them and must be
verified against all of them before it lands.

---

## Testing

```bash
bash cli.sh test b4n1-db      # this crate: unit + integration + feature-cut guards
bash cli.sh test all          # everything registered in the ecosystem
```

The suite pins, among other things, that `--no-default-features` really does drop
Arrow and the SQL engine, and that the README above stays honest: no license
claim, no version claim and no published-package claim that the repository does
not back.

---

## License

**Proprietary.** All rights reserved. The source in this repository is commercial
confidential IP and is not published to any registry. See `LICENSE`.

---

_b4n1-db is a core component of the B4N1 sovereign computing stack: embedded,
local, auditable._