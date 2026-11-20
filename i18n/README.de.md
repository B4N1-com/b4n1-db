# b4n1-db

**Soberane eingebettete Datenschicht für das B4N1-Ökosystem.** Ein einziges Handle
(`SovereignDB`) über acht lokale Speicher-Engines — keine Client/Server-Datenbank,
keine Netzwerk-Engine, kein Daemon, keine externen Abhängigkeiten.

> **Status: Bibliothek + eigenständiges Binary.** Liefert eine native Bibliothek UND
> ein eigenständiges `b4n1-db`-CLI (KV, SQL, Graph, Dokumente, Queues, Vektoren,
> Audit, Migrationen, Transfer, plus ein Offline-`demo` und `docs`). Kein
> MCP-Server. Veröffentlichte SDKs: PyPI, npm, NuGet, RubyGems, Packagist und
> Go-Module (siehe `sdks/`).

**Sprachen:** [العربية](README.ar.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Português (BR)](README.pt-BR.md) · [Русский](README.ru.md) · [中文](README.zh-CN.md)

---

## Inhaltsverzeichnis

- [Was es ist](#was-es-ist)
- [Installation](#installation)
- [Schnellstart](#schnellstart)
- [Subsysteme](#subsysteme)
- [SDKs](#sdks)
- [Lizenz](#lizenz)

---

## Was es ist

`b4n1-db` ist eine eingebettete Multi-Engine-Datenschicht. Sie öffnen ein
Verzeichnis und erhalten ein einziges Handle, das acht Speicher-Engines
bereitstellt — alle lokal, alle deterministisch, alle auditierbar.

| Engine | Funktion |
|--------|----------|
| KV | Schlüssel/Wert mit TTL, atomare Zähler, CAS |
| SQL | Analytisches SQL über spaltenorientierter Speicherung |
| Graph | Wissensgraph mit Hybrid-Suche |
| Doc | Dokumentensammlungen mit Feldsuchen |
| Queue | FIFO-Queues mit Ack/Nack |
| Vector | Vektorindex mit Kosinus-Ähnlichkeit |
| Audit | Merkle-DAG-Audit-Log mit Zeitreise |
| Migration | Schema-Migrationsregister |

Kein Daemon. Kein Netzwerk. Keine externe Datenbank.

## Installation

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

## Schnellstart

```bash
b4n1-db kv put mydb greeting "hello"
b4n1-db kv get mydb greeting
b4n1-db sql query mydb "SELECT * FROM users WHERE age > 25"
b4n1-db demo
b4n1-db docs
b4n1-db --licenses
```

## Subsysteme

| Verb | Beschreibung |
|------|-------------|
| `kv` | Schlüssel/Wert-Speicher: put, get, delete, scan, TTL |
| `sql` | Analytische SQL-Engine |
| `graph` | Wissensgraph und Hybrid-Suche |
| `doc` | Dokumentensammlungen |
| `queue` | FIFO-Queues mit Ack/Nack |
| `vector` | Vektorindex mit Kosinus-Ähnlichkeit |
| `audit` | Merkle-DAG-Audit-Log |
| `migration` | Schema-Migrationsregister |
| `transfer` | Backup, Restore und JSON-Transfer |
| `demo` | Offline-1-Klick-Demo |
| `docs` | Integrierte Dokumentation |

## SDKs

Das Verzeichnis `sdks/` enthält dünne Wrapper für 7 Sprachen. Jedes SDK bettet
das native Binary ein und löst es in dieser Reihenfolge auf: `B4N1_DB_BIN` →
eingebettetes `bin/` → `PATH`.

| SDK | Verzeichnis | Paket |
|-----|-------------|-------|
| Python | `sdks/python/` | PyPI: `b4n1-db` |
| Node.js | `sdks/node/` | npm: `b4n1-db` |
| .NET | `sdks/cs/` | NuGet: `B4n1.Db` |
| Ruby | `sdks/ruby/` | RubyGems: `b4n1-db` |
| PHP | `sdks/php/` | Packagist: `b4n1/b4n1-db` |
| Go | `sdks/go/` | Go-Module |
| Java | `sdks/java/` | (Maven ausstehend) |

## Lizenz

Business Source License 1.1 (BUSL-1.1). Siehe [LICENSE](../LICENSE) und [EULA.txt](../EULA.txt).

---

*Teil des [B4N1-Ökosystems](https://b4n1.com).*
