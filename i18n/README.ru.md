# b4n1-db

**Суверенный встроенный уровень данных для экосистемы B4N1.** Один дескриптор
(`SovereignDB`) над восемью локальными движками хранения — без клиент-серверной
базы, без сетевого движка, без демона, без внешних зависимостей.

> **Статус: библиотека + автономный бинарник.** Поставляет нативную библиотеку И
> автономный CLI `b4n1-db` (KV, SQL, графы, документы, очереди, векторы, аудит,
> миграции, передача, плюс офлайн-`demo` и `docs`). Без MCP-сервера. Опубликованные
> SDK: PyPI, npm, NuGet, RubyGems, Packagist и Go modules (см. `sdks/`).

**Языки:** [العربية](README.ar.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Português (BR)](README.pt-BR.md) · [Русский](README.ru.md) · [中文](README.zh-CN.md)

---

## Содержание

- [Что это](#что-это)
- [Установка](#установка)
- [Быстрый старт](#быстрый-старт)
- [Подсистемы](#подсистемы)
- [SDK](#sdk)
- [Лицензия](#лицензия)

---

## Что это

`b4n1-db` — встроенный много-движковый уровень данных. Вы открываете каталог и
получаете один дескриптор, дающий доступ к восьми движкам хранения, все локальные,
детерминированные и поддающиеся аудиту.

| Движок | Назначение |
|--------|-----------|
| KV | Ключ/значение с TTL, атомарные счётчики, CAS |
| SQL | Аналитический SQL по колоночному хранилищу |
| Graph | Граф знаний с гибридным поиском |
| Doc | Коллекции документов с запросами по полям |
| Queue | Очереди FIFO с ack/nack |
| Vector | Векторный индекс с косинусной близостью |
| Audit | Журнал аудита Merkle-DAG с путешествием во времени |
| Migration | Реестр миграций схемы |

Без демона. Без сети. Без внешней базы.

## Установка

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

## Быстрый старт

```bash
b4n1-db kv put mydb greeting "hello"
b4n1-db kv get mydb greeting
b4n1-db sql query mydb "SELECT * FROM users WHERE age > 25"
b4n1-db demo
b4n1-db docs
b4n1-db --licenses
```

## Подсистемы

| Команда | Описание |
|---------|----------|
| `kv` | Ключ/значение: put, get, delete, scan, TTL |
| `sql` | Аналитический движок SQL |
| `graph` | Граф знаний и гибридный поиск |
| `doc` | Коллекции документов |
| `queue` | Очереди FIFO с ack/nack |
| `vector` | Векторный индекс с косинусной близостью |
| `audit` | Журнал аудита Merkle-DAG |
| `migration` | Реестр миграций схемы |
| `transfer` | Резервное копирование, восстановление и JSON-передача |
| `demo` | Офлайн-демонстрация в один клик |
| `docs` | Встроенная документация |

## SDK

Каталог `sdks/` содержит тонкие обёртки для 7 языков. Каждый SDK встраивает
нативный бинарник и находит его в таком порядке: `B4N1_DB_BIN` → встроенный
`bin/` → `PATH`.

| SDK | Каталог | Пакет |
|-----|---------|-------|
| Python | `sdks/python/` | PyPI: `b4n1-db` |
| Node.js | `sdks/node/` | npm: `b4n1-db` |
| .NET | `sdks/cs/` | NuGet: `B4n1.Db` |
| Ruby | `sdks/ruby/` | RubyGems: `b4n1-db` |
| PHP | `sdks/php/` | Packagist: `b4n1/b4n1-db` |
| Go | `sdks/go/` | Go modules |
| Java | `sdks/java/` | (Maven ожидается) |

## Лицензия

Business Source License 1.1 (BUSL-1.1). См. [LICENSE](../LICENSE) и [EULA.txt](../EULA.txt).

---

*Часть [экосистемы B4N1](https://b4n1.com).*
