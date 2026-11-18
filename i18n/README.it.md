# b4n1-db

**Strato dati sovrano incorporato per l'ecosistema B4N1.** Un unico handle
(`SovereignDB`) su otto motori di archiviazione locali — nessun database
client/server, nessun motore di rete, nessun demone, nessuna dipendenza esterna.

> **Stato: libreria + binario autonomo.** Fornisce una libreria nativa E un CLI
> autonomo `b4n1-db` (KV, SQL, grafi, documenti, code, vettori, audit, migrazioni,
> trasferimento, più un `demo` offline e `docs`). Nessun server MCP. SDK
> pubblicati: PyPI, npm, NuGet, RubyGems, Packagist e Go modules (vedi `sdks/`).

**Lingue:** [العربية](README.ar.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Português (BR)](README.pt-BR.md) · [Русский](README.ru.md) · [中文](README.zh-CN.md)

---

## Indice

- [Che cos'è](#che-cosè)
- [Installazione](#installazione)
- [Avvio rapido](#avvio-rapido)
- [Sottosistemi](#sottosistemi)
- [SDK](#sdk)
- [Licenza](#licenza)

---

## Che cos'è

`b4n1-db` è uno strato dati incorporato multi-motore. Si apre una directory e si
ottiene un unico handle che espone otto motori di archiviazione, tutti locali,
deterministici e verificabili.

| Motore | Funzione |
|--------|----------|
| KV | Chiave/valore con TTL, contatori atomici, CAS |
| SQL | SQL analitico su archiviazione a colonne |
| Graph | Grafo di conoscenza con ricerca ibrida |
| Doc | Collezioni di documenti con query per campo |
| Queue | Code FIFO con ack/nack |
| Vector | Indice vettoriale con similarità coseno |
| Audit | Log di audit Merkle-DAG con viaggio nel tempo |
| Migration | Registro delle migrazioni di schema |

Nessun demone. Nessuna rete. Nessun database esterno.

## Installazione

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

## Avvio rapido

```bash
b4n1-db kv put mydb greeting "hello"
b4n1-db kv get mydb greeting
b4n1-db sql query mydb "SELECT * FROM users WHERE age > 25"
b4n1-db demo
b4n1-db docs
b4n1-db --licenses
```

## Sottosistemi

| Verbo | Descrizione |
|-------|-------------|
| `kv` | Chiave/valore: put, get, delete, scan, TTL |
| `sql` | Motore SQL analitico |
| `graph` | Grafo di conoscenza e ricerca ibrida |
| `doc` | Collezioni di documenti |
| `queue` | Code FIFO con ack/nack |
| `vector` | Indice vettoriale con similarità coseno |
| `audit` | Log di audit Merkle-DAG |
| `migration` | Registro delle migrazioni di schema |
| `transfer` | Backup, restore e trasferimento JSON |
| `demo` | Dimostrazione offline in 1 clic |
| `docs` | Documentazione integrata |

## SDK

La cartella `sdks/` contiene wrapper leggeri per 7 linguaggi. Ogni SDK incorpora
il binario nativo e lo risolve in questo ordine: `B4N1_DB_BIN` → `bin/`
incorporato → `PATH`.

| SDK | Cartella | Pacchetto |
|-----|----------|-----------|
| Python | `sdks/python/` | PyPI: `b4n1-db` |
| Node.js | `sdks/node/` | npm: `b4n1-db` |
| .NET | `sdks/cs/` | NuGet: `B4n1.Db` |
| Ruby | `sdks/ruby/` | RubyGems: `b4n1-db` |
| PHP | `sdks/php/` | Packagist: `b4n1/b4n1-db` |
| Go | `sdks/go/` | Go modules |
| Java | `sdks/java/` | (Maven in sospeso) |

## Licenza

Business Source License 1.1 (BUSL-1.1). Vedi [LICENSE](../LICENSE) e [EULA.txt](../EULA.txt).

---

*Parte dell'[ecosistema B4N1](https://b4n1.com).*
