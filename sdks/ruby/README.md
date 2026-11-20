# b4n1-db — Ruby SDK

Thin Ruby wrapper over the `b4n1-db` standalone binary. All storage, SQL, graph,
vector and audit logic lives in the binary; this gem only invokes it.

## Install

```bash
gem install b4n1-db
```

The binary is resolved in this order: `B4N1_DB_BIN`, the copy bundled in the
gem, then `b4n1-db` on `PATH`.

## Usage

```ruby
require "b4n1-db"

db = B4n1Db::DB.new("./data")

db.kv.put("greeting", "hello")
puts db.kv.get("greeting")          # {"key" => "greeting", "value" => "hello"}

db.sql.execute("CREATE TABLE u (id BIGINT, name VARCHAR)")
db.sql.execute("INSERT INTO u VALUES (1, 'ada')")
puts db.sql.query("SELECT name FROM u").inspect

node = db.graph.add_node("pattern", "ada")   # {"ok" => true, "id" => "…"}
db.doc.insert("notes", { "text" => "first" })
db.queue.push("jobs", "build")
db.audit.verify
```

## Engines

| Group | Methods |
|---|---|
| `kv` | put, get, delete, scan, put_ttl, ttl, purge_expired |
| `sql` | execute, query, tables, register_csv, register_json, register_parquet, drop |
| `graph` | add_node, add_relation, node, search, forget |
| `doc` | insert, find_one, find, find_by_field, update, delete |
| `queue` | push, pop, peek, ack, nack, clear, len |
| `vector` | insert, search, delete, list |
| `audit` | verify, history, latest, time_travel, root_hash |
| `migration` | list, add, apply, exec, rollback |
| `transfer` | backup, restore, export, import |

Vectors use the database dimension (1536 by default). Graph node types are
`instruction`, `intention`, `contract`, `pattern`, `scaffold`, `qapair` and
`cyclerecord`.

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db ruby test/surface_test.rb
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
