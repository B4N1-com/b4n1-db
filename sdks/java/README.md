# b4n1-db — Java SDK

Thin Java wrapper over the `b4n1-db` standalone binary. All storage, SQL,
graph, vector and audit logic lives in the binary; this library only invokes it.

> **Distribution status:** the source ships in the repository and builds locally
> with Maven; **no artifact is published to Maven Central** — that channel is
> blocked by the organization quota (AGENTS.md §11.29, task `90b3ad6b`). The jar
> travels inside the product bundle delivered to evaluated clients.

## Build

```bash
mvn -q package          # target/b4n1-db-0.1.0.jar
```

## Usage

```java
import com.b4n1.db.B4n1Db;

B4n1Db db = new B4n1Db("./data");
db.put("greeting", "hello");
System.out.println(db.get("greeting")); // {"key":"greeting","value":"hello"}
System.out.println(db.stats());         // {"current_version":..., ...}
```

`stats()`, `get()` return JSON text so the wrapper has zero dependencies — parse
them with the JSON library your application already uses.

The binary is resolved in this order: `B4N1_DB_BIN`, then `b4n1-db` on `PATH`.

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
