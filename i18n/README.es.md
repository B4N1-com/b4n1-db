# b4n1-db

**Capa de datos embebida soberana para el ecosistema B4N1.** Un solo handle (`SovereignDB`)
sobre ocho motores de almacenamiento local — sin base de datos cliente/servidor, sin motor de red,
sin daemon, sin dependencias externas.

> **Estado: librería + binario standalone.** Incluye una librería nativa Y un CLI standalone
> `b4n1-db` (KV, SQL, grafos, documentos, colas, vectores, auditoría, migraciones,
> transferencia, más un `demo` offline y `docs`). Sin servidor MCP. SDKs publicados:
> PyPI, npm, NuGet, RubyGems, Packagist y Go modules (ver `sdks/`).

---

## Tabla de contenidos

- [Qué es](#qué-es)
- [Instalación](#instalación)
- [Inicio rápido](#inicio-rápido)
- [Subsistemas](#subsistemas)
- [SDKs](#sdks)
- [Licencia](#licencia)

---

## Qué es

`b4n1-db` es una capa de datos embebida multi-motor. Abres un directorio y obtienes
un solo handle que expone ocho motores de almacenamiento, todos locales, todos deterministas,
todos auditables:

| Motor | Qué hace |
|-------|----------|
| KV | Clave/valor con TTL, contadores atómicos, CAS |
| SQL | SQL analítico sobre almacenamiento columnar |
| Graph | Grafo de conocimiento con búsqueda híbrida |
| Doc | Colecciones de documentos con consultas por campo |
| Queue | Colas FIFO con ack/nack |
| Vector | Índice vectorial con similitud coseno |
| Audit | Log de auditoría Merkle-DAG con viaje en el tiempo |
| Migration | Registro de migraciones de esquema |

Sin daemon. Sin red. Sin base de datos externa. Solo abre una ruta y úsala.

## Instalación

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

### Binario (CLI)
Descarga desde la [página de releases](https://github.com/B4N1-com/b4n1-db/releases)
o compila desde el código fuente.

## Inicio rápido

```bash
# Abrir una base de datos y guardar un valor
b4n1-db kv put mydb greeting "hello"

# Leerlo de vuelta
b4n1-db kv get mydb greeting

# Ejecutar SQL
b4n1-db sql query mydb "SELECT * FROM users WHERE age > 25"

# Demo offline (sin red)
b4n1-db demo

# Documentación integrada
b4n1-db docs

# Texto de licencia
b4n1-db --licenses
```

## Subsistemas

Cada subsistema es un subcomando del binario `b4n1-db`:

| Verbo | Descripción |
|-------|-------------|
| `kv` | Almacén clave/valor: put, get, delete, scan, TTL |
| `sql` | Motor SQL analítico |
| `graph` | Grafo de conocimiento y búsqueda híbrida |
| `doc` | Colecciones de documentos |
| `queue` | Colas FIFO con ack/nack |
| `vector` | Índice vectorial con similitud coseno |
| `audit` | Log de auditoría Merkle-DAG |
| `migration` | Registro de migraciones de esquema |
| `transfer` | Backup, restore y transferencia JSON |
| `stats` | Estadísticas de la base de datos |
| `demo` | Demostración offline de 1 clic |
| `docs` | Documentación integrada |

## SDKs

El directorio `sdks/` contiene wrappers delgados para 7 lenguajes. Cada SDK embebe
el binario nativo y lo resuelve en este orden:

1. Variable de entorno `B4N1_DB_BIN` (override explícito)
2. Binario embebido en `bin/` (incluido en el paquete)
3. `PATH` (instalación global del sistema)

| SDK | Directorio | Paquete |
|-----|-----------|---------|
| Python | `sdks/python/` | PyPI: `b4n1-db` |
| Node.js | `sdks/node/` | npm: `b4n1-db` |
| .NET | `sdks/cs/` | NuGet: `B4n1.Db` |
| Ruby | `sdks/ruby/` | RubyGems: `b4n1-db` |
| PHP | `sdks/php/` | Packagist: `b4n1/b4n1-db` |
| Go | `sdks/go/` | Go modules |
| Java | `sdks/java/` | (Maven pendiente) |

## Licencia

Business Source License 1.1 (BUSL-1.1). Ver [LICENSE](../LICENSE) y [EULA.txt](../EULA.txt).

---

*Parte del [ecosistema B4N1](https://b4n1.com).*
