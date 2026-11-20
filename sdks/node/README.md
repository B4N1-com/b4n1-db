# b4n1-db — Node.js SDK

Thin Node.js wrapper over the `b4n1-db` standalone binary. All storage, SQL,
graph, vector and audit logic lives in the binary; this package only invokes it.

## Install

```bash
npm install b4n1-db
```

The binary is resolved in this order:

1. `B4N1_DB_BIN` (absolute path to the binary)
2. `bin/<platform binary>` bundled in the package
3. `b4n1-db` on `PATH`

## Usage

```js
const { DB } = require("b4n1-db");

const db = new DB("./data");

db.kv.put("greeting", "hello");
console.log(db.kv.get("greeting"));        // { key: "greeting", value: "hello" }

db.sql.execute("CREATE TABLE u (id BIGINT, name VARCHAR)");
db.sql.execute("INSERT INTO u VALUES (1, 'ada')");
console.log(db.sql.query("SELECT name FROM u"));   // { columns, rows }

const node = db.graph.addNode("pattern", "ada");   // { ok: true, id: "…" }
db.doc.insert("notes", { text: "first" });
db.queue.push("jobs", "build");
db.audit.verify();

console.log(db.stats());           // { current_version, kv_entries, merkle_nodes }
console.log(db.demo());            // guided offline tour
```

## Engines

Nine groups on the handle, one per engine of the binary:

| Group | Methods |
|---|---|
| `kv` | put, get, delete, scan, putTtl, ttl, purgeExpired |
| `sql` | execute, query, tables, registerCsv, registerJson, registerParquet, drop |
| `graph` | addNode, addRelation, node, search, forget |
| `doc` | insert, findOne, find, findByField, update, delete |
| `queue` | push, pop, peek, ack, nack, clear, len |
| `vector` | insert, search, delete, list |
| `audit` | verify, history, latest, timeTravel, rootHash |
| `migration` | list, add, apply, exec, rollback |
| `transfer` | backup, restore, export, import |

Vectors use the database dimension (1536 by default). Graph node types are
`instruction`, `intention`, `contract`, `pattern`, `scaffold`, `qapair` and
`cyclerecord`. Mutations answer with the facts of what changed (the new `id`,
the rows affected), never a sentence.

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db npm test
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
