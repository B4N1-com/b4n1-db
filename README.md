# b4n1-db

**Sovereign embedded data layer for the B4N1 ecosystem.** One handle (`SovereignDB`)
over eight local storage engines — no client/server database, no network engine,
no daemon, no external dependencies.

> **Status: library + standalone binary.** Ships a native library AND a standalone
> `b4n1-db` CLI (KV, SQL, graph, documents, queues, vectors, audit, migrations,
> transfer, plus an offline `demo` and `docs`). No MCP server. Published SDKs:
> PyPI, npm, NuGet, RubyGems, Packagist and Go modules (see `sdks/`).

**Languages:** [العربية](i18n/README.ar.md) · [Deutsch](i18n/README.de.md) · [Español](i18n/README.es.md) · [Français](i18n/README.fr.md) · [हिन्दी](i18n/README.hi.md) · [Italiano](i18n/README.it.md) · [日本語](i18n/README.ja.md) · [한국어](i18n/README.ko.md) · [Português (BR)](i18n/README.pt-BR.md) · [Русский](i18n/README.ru.md) · [中文](i18n/README.zh-CN.md)

---

## Table of contents

- [What it is](#what-it-is)
- [Install](#install)
- [Quick start](#quick-start)
- [Subsystems](#subsystems)
- [SDKs](#sdks)
- [Testing](#testing)
- [License](#license)

---

## What it is

`b4n1-db` is an embedded, multi-engine data layer. You open a directory and get
a single handle that exposes eight storage engines, all local, all deterministic,
all auditable:

| Engine | What it does |
|--------|-------------|
| KV | Key/value with TTL, atomic counters, CAS |
| SQL | Analytical SQL over columnar storage |
| Graph | Knowledge graph with hybrid search |
| Doc | Document collections with field queries |
| Queue | FIFO queues with ack/nack |
| Vector | Cosine-similarity vector index |
| Audit | Merkle-DAG audit log with time travel |
| Migration | Schema migration registry |

No daemon. No network. No external database. Just open a path and use it.

## Install

### Python
```bash
pip install b4n1-db
```

### Node.js
```bash
npm install b4n1-db
```

### .NET
```bash
dotnet add package B4n1.Db
```

### Ruby
```bash
gem install b4n1-db
```

### PHP (Composer)
```bash
composer require b4n1/b4n1-db
```

### Go
```bash
go get github.com/B4N1-com/b4n1-db/sdks/go
```

### Binary (CLI)
Download from the [releases page](https://github.com/B4N1-com/b4n1-db/releases)
or build from source.

## Quick start

```bash
# Open a database and put a value
b4n1-db kv put mydb greeting "hello"

# Read it back
b4n1-db kv get mydb greeting

# Run SQL
b4n1-db sql query mydb "SELECT * FROM users WHERE age > 25"

# Offline demo (no network needed)
b4n1-db demo

# Built-in documentation
b4n1-db docs

# License text
b4n1-db --licenses
```

## Subsystems

Each subsystem is a subcommand of the `b4n1-db` binary:

| Verb | Description |
|------|-------------|
| `kv` | Key/value store: put, get, delete, scan, TTL |
| `sql` | Analytical SQL engine |
| `graph` | Knowledge graph and hybrid search |
| `doc` | Document collections |
| `queue` | FIFO queues with ack/nack |
| `vector` | Vector index with cosine similarity |
| `audit` | Merkle-DAG audit log |
| `migration` | Schema migration registry |
| `transfer` | Backup, restore and JSON transfer |
| `stats` | Database statistics |
| `demo` | Offline 1-click demonstration |
| `docs` | Built-in documentation |

## SDKs

The `sdks/` directory contains thin wrappers for 7 languages. Each SDK embeds
the native binary and resolves it in this order:

1. `B4N1_DB_BIN` env var (explicit override)
2. Embedded binary in `bin/` (shipped with the package)
3. `PATH` (system-wide install)

| SDK | Directory | Package |
|-----|-----------|---------|
| Python | `sdks/python/` | PyPI: `b4n1-db` |
| Node.js | `sdks/node/` | npm: `b4n1-db` |
| .NET | `sdks/cs/` | NuGet: `B4n1.Db` |
| Ruby | `sdks/ruby/` | RubyGems: `b4n1-db` |
| PHP | `sdks/php/` | Packagist: `b4n1/b4n1-db` |
| Go | `sdks/go/` | Go modules |
| Java | `sdks/java/` | (Maven pending) |

## Testing

The project ships a full test suite: unit, integration, feature-cut guards and
per-SDK contract tests. Every published SDK is contract-tested against the
real binary before release.

```bash
# Run the embedded SDK contract suite
bash cli.sh test b4n1-db-embedded
```

## License

Business Source License 1.1 (BUSL-1.1). See [LICENSE](LICENSE) and [EULA.txt](EULA.txt).

---

*Part of the [B4N1 ecosystem](https://b4n1.com).*
