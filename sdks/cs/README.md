# b4n1-db — .NET SDK

Thin C# wrapper over the `b4n1-db` standalone binary. All storage, SQL, graph,
vector and audit logic lives in the binary; this package only invokes it. JSON
comes back as text, so the package has no dependencies: parse it with the JSON
library your application already uses.

## Install

```bash
dotnet add package B4n1.Db
```

The binary is resolved in this order: `B4N1_DB_BIN`, the copy bundled under
`native/` in the package, then `b4n1-db` on `PATH`.

## Usage

```csharp
using B4n1.Db;

var db = new Database("./data");

db.Kv.Put("greeting", "hello");
Console.WriteLine(db.Kv.Get("greeting"));   // {"key":"greeting","value":"hello"}

db.Sql.Execute("CREATE TABLE u (id BIGINT, name VARCHAR)");
db.Sql.Execute("INSERT INTO u VALUES (1, 'ada')");
Console.WriteLine(db.Sql.Query("SELECT name FROM u"));

Console.WriteLine(db.Graph.AddNode("pattern", "ada"));  // {"ok":true,"id":"…"}
db.Doc.Insert("notes", new Dictionary<string, object> { ["text"] = "first" });
db.Queue.Push("jobs", "build");
db.Audit.Verify();
```

## Engines

| Group | Methods |
|---|---|
| `Kv` | Put, Get, Delete, Scan, PutTtl, Ttl, PurgeExpired |
| `Sql` | Execute, Query, Tables, RegisterCsv, RegisterJson, RegisterParquet, Drop |
| `Graph` | AddNode, AddRelation, Node, Search, Forget |
| `Doc` | Insert, FindOne, Find, FindByField, Update, Delete |
| `Queue` | Push, Pop, Peek, Ack, Nack, Clear, Len |
| `Vector` | Insert, Search, Delete, List |
| `Audit` | Verify, History, Latest, TimeTravel, RootHash |
| `Migration` | List, Add, Apply, Exec, Rollback |
| `Transfer` | Backup, Restore, Export, Import |

Vectors use the database dimension (1536 by default). Graph node types are
`instruction`, `intention`, `contract`, `pattern`, `scaffold`, `qapair` and
`cyclerecord`.

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db dotnet run --project tests/SurfaceSmoke
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
