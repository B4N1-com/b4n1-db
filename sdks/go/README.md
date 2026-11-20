# b4n1-db — Go SDK

Thin Go wrapper over the `b4n1-db` standalone binary. All storage, SQL, graph,
vector and audit logic lives in the binary; this module only invokes it.

## Install

```bash
go get github.com/B4N1-com/b4n1-db/polyglot/go
```

The binary is resolved in this order: `B4N1_DB_BIN`, then `b4n1-db` on `PATH`.

## Usage

```go
db := b4n1db.New("./data")

res, err := db.KV.Put("greeting", "hello")     // {"ok": true, "key": "greeting"}
entry, err := db.KV.Get("greeting")            // {"key": "greeting", "value": "hello"}

_, err = db.SQL.Execute("CREATE TABLE u (id BIGINT, name VARCHAR)")
_, err = db.SQL.Execute("INSERT INTO u VALUES (1, 'ada')")
rows, err := db.SQL.Query("SELECT name FROM u")

node, err := db.Graph.AddNode("pattern", "ada")
_, err = db.Doc.Insert("notes", map[string]any{"text": "first"})
_, err = db.Queue.Push("jobs", "build")
_, err = db.Audit.Verify()
```

Every call returns `(Result, error)`, where `Result` is the decoded JSON object:
the binary decides the fields and the wrapper never invents them.

## Engines

| Group | Methods |
|---|---|
| `KV` | Put, Get, Delete, Scan, PutTTL, TTL, PurgeExpired |
| `SQL` | Execute, Query, Tables, RegisterCSV, RegisterJSON, RegisterParquet, Drop |
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
B4N1_DB_BIN=/path/to/b4n1-db go test ./...
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
