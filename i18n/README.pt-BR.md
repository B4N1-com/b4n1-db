# b4n1-db

**Camada de dados soberana embarcada para o ecossistema B4N1.** Um único handle
(`SovereignDB`) sobre oito motores de armazenamento locais — sem banco
cliente/servidor, sem motor de rede, sem daemon, sem dependências externas.

> **Status: biblioteca + binário autônomo.** Fornece uma biblioteca nativa E um CLI
> autônomo `b4n1-db` (KV, SQL, grafo, documentos, filas, vetores, auditoria,
> migrações, transferência, além de um `demo` offline e `docs`). Sem servidor MCP.
> SDKs publicados: PyPI, npm, NuGet, RubyGems, Packagist e Go modules (ver `sdks/`).

**Idiomas:** [العربية](README.ar.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Português (BR)](README.pt-BR.md) · [Русский](README.ru.md) · [中文](README.zh-CN.md)

---

## Índice

- [O que é](#o-que-é)
- [Instalação](#instalação)
- [Início rápido](#início-rápido)
- [Subsistemas](#subsistemas)
- [SDKs](#sdks)
- [Licença](#licença)

---

## O que é

`b4n1-db` é uma camada de dados embarcada multi-motor. Você abre um diretório e
obtém um único handle que expõe oito motores de armazenamento, todos locais,
determinísticos e auditáveis.

| Motor | Função |
|-------|--------|
| KV | Chave/valor com TTL, contadores atômicos, CAS |
| SQL | SQL analítico sobre armazenamento colunar |
| Graph | Grafo de conhecimento com busca híbrida |
| Doc | Coleções de documentos com consultas por campo |
| Queue | Filas FIFO com ack/nack |
| Vector | Índice vetorial com similaridade de cosseno |
| Audit | Log de auditoria Merkle-DAG com viagem no tempo |
| Migration | Registro de migrações de esquema |

Sem daemon. Sem rede. Sem banco externo.

## Instalação

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

## Início rápido

```bash
b4n1-db kv put mydb greeting "hello"
b4n1-db kv get mydb greeting
b4n1-db sql query mydb "SELECT * FROM users WHERE age > 25"
b4n1-db demo
b4n1-db docs
b4n1-db --licenses
```

## Subsistemas

| Verbo | Descrição |
|-------|-----------|
| `kv` | Chave/valor: put, get, delete, scan, TTL |
| `sql` | Motor SQL analítico |
| `graph` | Grafo de conhecimento e busca híbrida |
| `doc` | Coleções de documentos |
| `queue` | Filas FIFO com ack/nack |
| `vector` | Índice vetorial com similaridade de cosseno |
| `audit` | Log de auditoria Merkle-DAG |
| `migration` | Registro de migrações de esquema |
| `transfer` | Backup, restore e transferência JSON |
| `demo` | Demonstração offline de 1 clique |
| `docs` | Documentação integrada |

## SDKs

O diretório `sdks/` contém wrappers finos para 7 linguagens. Cada SDK embarca o
binário nativo e o resolve nesta ordem: `B4N1_DB_BIN` → `bin/` embarcado → `PATH`.

| SDK | Diretório | Pacote |
|-----|-----------|--------|
| Python | `sdks/python/` | PyPI: `b4n1-db` |
| Node.js | `sdks/node/` | npm: `b4n1-db` |
| .NET | `sdks/cs/` | NuGet: `B4n1.Db` |
| Ruby | `sdks/ruby/` | RubyGems: `b4n1-db` |
| PHP | `sdks/php/` | Packagist: `b4n1/b4n1-db` |
| Go | `sdks/go/` | Go modules |
| Java | `sdks/java/` | (Maven pendente) |

## Licença

Business Source License 1.1 (BUSL-1.1). Ver [LICENSE](../LICENSE) e [EULA.txt](../EULA.txt).

---

*Parte do [ecossistema B4N1](https://b4n1.com).*
