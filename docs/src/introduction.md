# Introduction

b4n1-db is an **embedded data layer**: one handle over eight local storage
engines, running inside your process. There is no server, no daemon, no port to
open and no network round trip between your code and its data.

```text
your program ──► SovereignDB handle ──► key-value · SQL · graph · documents
                                        queues · vectors · audit · migrations
                                                    │
                                             one local store
```

## What it is

- **One handle, eight engines.** `DB("./data")` (or a `SovereignDB` in Rust)
  gives you key-value, analytical SQL, a knowledge graph, document collections,
  durable queues, a vector index, a tamper-evident audit log and versioned
  migrations. They share one store; you never pick between them at setup.
- **Local by construction.** Data never leaves the machine. That is not a
  configuration option you can accidentally disable — there is no code path
  that opens a socket.
- **Ship it in the artifact.** The engine is a native binary that travels
  inside each SDK package, so a consumer installs one thing and gets the whole
  data layer with no compiler and no service to keep alive.

## What it is not

- **Not a distributed database.** No replication protocol, no consensus, no
  client/server mode.
- **Not ACID across operations.** Each key write is durable and atomic on its
  own; there is no multi-operation transaction and no two-phase commit. Do not
  design around an assumption it does not make.
- **Not an authorization system.** Tenant isolation is a key prefix (see
  [Security and Isolation](security.md)); it separates by accident, not by
  policy.

## Why it exists

A service that needs a cache, a queue, a table, a graph and a search index
usually ends up running five daemons and a network between them. Most of that
workload fits in one process on one machine. b4n1-db is the case where it does:
you get the five capabilities with the latency of a function call and the
deployment cost of a single file.

## Next

- [Quick Start](quickstart.md) — five minutes, no configuration.
- [The Eight Engines](engines.md) — what each one actually guarantees.
