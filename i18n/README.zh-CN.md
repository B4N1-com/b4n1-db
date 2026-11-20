# b4n1-db

**B4N1 生态系统的自主嵌入式数据层。** 一个句柄（`SovereignDB`）即可驾驭八个本地
存储引擎——无需客户端/服务器数据库，无需网络引擎，无需守护进程，无外部依赖。

> **状态：库 + 独立二进制文件。** 提供原生库和独立的 `b4n1-db` 命令行工具（KV、
> SQL、图、文档、队列、向量、审计、迁移、传输，另有离线 `demo` 和 `docs`）。
> 无 MCP 服务器。已发布 SDK：PyPI、npm、NuGet、RubyGems、Packagist 和 Go modules
> （见 `sdks/`）。

**语言：** [العربية](README.ar.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Português (BR)](README.pt-BR.md) · [Русский](README.ru.md) · [中文](README.zh-CN.md)

---

## 目录

- [简介](#简介)
- [安装](#安装)
- [快速开始](#快速开始)
- [子系统](#子系统)
- [SDK](#sdk)
- [许可证](#许可证)

---

## 简介

`b4n1-db` 是一个嵌入式多引擎数据层。打开一个目录，即可获得一个句柄，暴露八个存储
引擎，全部本地、确定且可审计。

| 引擎 | 功能 |
|------|------|
| KV | 带 TTL 的键/值、原子计数器、CAS |
| SQL | 基于列式存储的分析型 SQL |
| Graph | 带混合搜索的知识图谱 |
| Doc | 支持字段查询的文档集合 |
| Queue | 带 ack/nack 的 FIFO 队列 |
| Vector | 余弦相似度向量索引 |
| Audit | 可时间旅行的 Merkle-DAG 审计日志 |
| Migration | 模式迁移注册表 |

无守护进程。无网络。无外部数据库。

## 安装

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

## 快速开始

```bash
b4n1-db kv put mydb greeting "hello"
b4n1-db kv get mydb greeting
b4n1-db sql query mydb "SELECT * FROM users WHERE age > 25"
b4n1-db demo
b4n1-db docs
b4n1-db --licenses
```

## 子系统

| 命令 | 说明 |
|------|------|
| `kv` | 键/值存储：put、get、delete、scan、TTL |
| `sql` | 分析型 SQL 引擎 |
| `graph` | 知识图谱与混合搜索 |
| `doc` | 文档集合 |
| `queue` | 带 ack/nack 的 FIFO 队列 |
| `vector` | 余弦相似度向量索引 |
| `audit` | Merkle-DAG 审计日志 |
| `migration` | 模式迁移注册表 |
| `transfer` | 备份、恢复和 JSON 传输 |
| `demo` | 一键离线演示 |
| `docs` | 内置文档 |

## SDK

`sdks/` 目录包含 7 种语言的轻量包装。每个 SDK 都嵌入原生二进制文件并按以下顺序查找：
`B4N1_DB_BIN` → 嵌入式 `bin/` → `PATH`。

| SDK | 目录 | 包 |
|-----|------|-----|
| Python | `sdks/python/` | PyPI：`b4n1-db` |
| Node.js | `sdks/node/` | npm：`b4n1-db` |
| .NET | `sdks/cs/` | NuGet：`B4n1.Db` |
| Ruby | `sdks/ruby/` | RubyGems：`b4n1-db` |
| PHP | `sdks/php/` | Packagist：`b4n1/b4n1-db` |
| Go | `sdks/go/` | Go modules |
| Java | `sdks/java/` | （Maven 待定） |

## 许可证

Business Source License 1.1（BUSL-1.1）。见 [LICENSE](../LICENSE) 和 [EULA.txt](../EULA.txt)。

---

*[B4N1 生态系统](https://b4n1.com) 的一部分。*
