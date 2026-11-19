# Quick Start

## Install

```bash
pip install b4n1-db          # Python
npm install b4n1-db          # Node.js
```

Packages are published for Linux (x86_64, arm64), macOS (Intel and Apple
silicon) and Windows x86_64. The engine ships precompiled inside the package:
no compiler, no build step, no separate service.

## Try it in one command

```bash
python -m b4n1db demo
```

The demo runs against a throwaway database, prints each capability with live
numbers, and never touches your own data.

## First database from Python

```python
from b4n1db import DB

db = DB("./data")                  # creates the directory it needs

db.kv_put("greeting", "hello")
print(db.kv_get("greeting"))       # b"hello"
print(db.stats())                  # entries, audit nodes, schema version
```

The Python object is deliberately thin: `kv_put`, `kv_get`, `stats` and `demo`.
For everything else it hands the work to the engine it ships with, which you
drive as a command (below) — same store, same file, no server in between.

## First database from the shell

```bash
b4n1-db kv put greeting hello
b4n1-db kv get greeting

b4n1-db sql execute "CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT)"
b4n1-db sql execute "INSERT INTO users (name) VALUES ('ada')"
b4n1-db sql query "SELECT name FROM users"

b4n1-db stats
```

Values cross the CLI as text; a value that is not valid UTF-8 is printed
base64-encoded. `--db <path>` picks the database directory (default `./data`,
or `B4N1_DB_PATH`).

## Key-value with expiry

```bash
b4n1-db kv put-ttl session token 60     # expires in 60 s
b4n1-db kv ttl session                  # seconds left
b4n1-db kv purge-expired                # reclaim eagerly; reads expire anyway
```

## A queue you can restart behind

```bash
b4n1-db queue push jobs '{"kind":"resize"}'
b4n1-db queue pop jobs                  # delivered, not yet acknowledged
b4n1-db queue ack jobs <id>             # or nack to hand it back
```

A popped message stays visible to another consumer (or to the same process
after a crash) until it is acknowledged.

## A tamper-evident history

```bash
b4n1-db audit verify          # recompute every hash, fail at the first mismatch
b4n1-db audit history greeting
b4n1-db audit root-hash
```

## Where to go next

- [The Eight Engines](engines.md) — the guarantees behind each capability.
- [CLI Reference](cli.md) — every verb, exit code and variable.
- [SDKs](sdks.md) — the language packages.
