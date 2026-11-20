# b4n1-db — Java SDK

Thin Java wrapper over the `b4n1-db` standalone binary. All storage, SQL, graph,
vector and audit logic lives in the binary; this package only invokes it. JSON
comes back as text, so there is no runtime dependency to conflict with yours.

## Install

Build the JAR that ships with the engine binary for your platform:

```bash
mvn -f polyglot/java package
```

The binary is resolved in this order: `B4N1_DB_BIN`, then `b4n1-db` on `PATH`.

## Usage

```java
import com.b4n1.db.B4n1Db;

B4n1Db db = new B4n1Db("./data");

db.kv().put("greeting", "hello");
System.out.println(db.kv().get("greeting"));   // {"key":"greeting","value":"hello"}

db.sql().execute("CREATE TABLE u (id BIGINT, name VARCHAR)");
db.sql().execute("INSERT INTO u VALUES (1, 'ada')");
System.out.println(db.sql().query("SELECT name FROM u"));

System.out.println(db.graph().addNode("pattern", "ada"));  // {"ok":true,"id":"…"}
db.doc().insert("notes", "{\"text\":\"first\"}");
db.queue().push("jobs", "build");
db.audit().verify();
```

## Engines

| Group | Methods |
|---|---|
| `kv()` | put, get, delete, scan, putTtl, ttl, purgeExpired |
| `sql()` | execute, query, tables, registerCsv, registerJson, registerParquet, drop |
| `graph()` | addNode, addRelation, node, search, forget |
| `doc()` | insert, findOne, find, findByField, update, delete |
| `queue()` | push, pop, peek, ack, nack, clear, len |
| `vector()` | insert, search, delete, list |
| `audit()` | verify, history, latest, timeTravel, rootHash |
| `migration()` | list, add, apply, exec, rollback |
| `transfer()` | backup, restore, export, import |

Vectors use the database dimension (1536 by default). Graph node types are
`instruction`, `intention`, `contract`, `pattern`, `scaffold`, `qapair` and
`cyclerecord`. `transfer().importFrom(...)` is named that way because `import`
is a Java keyword.

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db javac -d out $(find src/main/java -name '*.java') \
  && javac -cp out -d out tests/SurfaceSmoke.java \
  && java -cp out SurfaceSmoke
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
