# SDKs

Every package is a **thin wrapper**: the operations are executed by the
`b4n1-db` engine that ships precompiled inside the package for your platform.
No compiler, no build step, no service to install.

| Language | Install | Package |
|---|---|---|
| Python | `pip install b4n1-db` | `b4n1-db` (module `b4n1db`) |
| Node.js | `npm install b4n1-db` | `b4n1-db` |
| .NET | `dotnet add package B4n1.Db` | `B4n1.Db` |
| Ruby | `gem install b4n1-db` | `b4n1-db` |
| PHP | `composer require b4n1/b4n1-db` | `b4n1/b4n1-db` |
| Go | `go get github.com/B4N1-com/b4n1-db/polyglot/go` | module `b4n1db` |
| Java | the JAR that ships with the engine binary | `com.b4n1.db` |

Platforms: Linux x86_64 and arm64, macOS Intel and Apple silicon, Windows
x86_64. If no build matches your platform the install fails rather than handing
you a wrapper that cannot run.

## The surface is the same in all seven

One contract, fifty methods, nine engines. The name differs by convention
(`db.kv.put` in Python, `db.Kv.Put` in .NET, `db.KV.Put` in Go) and the
behaviour never does — each method is one verb of the binary.

| Engine | Methods |
|---|---|
| `kv` | `put` `get` `delete` `scan` `put_ttl` `ttl` `purge_expired` |
| `sql` | `execute` `query` `tables` `register_csv` `register_json` `register_parquet` `drop` |
| `graph` | `add_node` `add_relation` `node` `search` `forget` |
| `doc` | `insert` `find_one` `find` `find_by_field` `update` `delete` |
| `queue` | `push` `pop` `peek` `ack` `nack` `clear` `len` |
| `vector` | `insert` `search` `delete` `list` |
| `audit` | `verify` `history` `latest` `time_travel` `root_hash` |
| `migration` | `list` `add` `apply` `exec` `rollback` |
| `transfer` | `backup` `restore` `export` `import` |

Mutations return the facts of what changed (the new `id`, the rows affected),
never a sentence to parse.

## Python

```python
from b4n1db import DB

db = DB("./data")

db.kv.put("greeting", "hello")
print(db.kv.get("greeting"))          # {'key': 'greeting', 'value': 'hello'}

db.sql.execute("CREATE TABLE users (id BIGINT, name VARCHAR)")
db.sql.execute("INSERT INTO users VALUES (1, 'ada')")
print(db.sql.query("SELECT name FROM users"))   # {'columns': [...], 'rows': [...]}

node = db.graph.add_node("pattern", "ada")      # {'ok': True, 'id': '…'}
db.doc.insert("notes", {"text": "first"})
db.queue.push("jobs", "build")
db.vector.insert("docs", [1.0, 0.0, …])          # 1536 floats
db.audit.root_hash()
db.migration.add(1, "init", "CREATE TABLE m (id BIGINT)")
db.transfer.export("/tmp/dump.jsonl")
```

## Node.js

```javascript
const { DB } = require("b4n1-db");

const db = new DB("./data");
db.kv.put("greeting", "hello");
console.log(db.kv.get("greeting"));        // { key: 'greeting', value: 'hello' }
db.sql.execute("CREATE TABLE u (id BIGINT)");
db.queue.push("jobs", "build");
db.audit.verify();
```

## .NET

```csharp
using B4n1.Db;

var db = new Database("./data");
db.Kv.Put("greeting", "hello");
Console.WriteLine(db.Kv.Get("greeting"));  // JSON text, dependency-free
db.Sql.Execute("CREATE TABLE u (id BIGINT)");
db.Queue.Push("jobs", "build");
```

## Ruby

```ruby
require "b4n1-db"

db = B4n1Db::DB.new("./data")
db.kv.put("greeting", "hello")
puts db.kv.get("greeting")        # {"key" => "greeting", "value" => "hello"}
db.sql.execute("CREATE TABLE u (id BIGINT)")
db.graph.add_node("pattern", "ada")
```

## PHP

```php
$db = new B4n1\Db\Db('./data');
$db->kv()->put('greeting', 'hello');
var_dump($db->kv()->get('greeting'));
$db->sql()->execute('CREATE TABLE u (id BIGINT)');
$db->queue()->push('jobs', 'build');
```

## Go

```go
db := b4n1db.New("./data")
res, err := db.KV.Put("greeting", "hello")
entry, err := db.KV.Get("greeting")
_, err = db.SQL.Execute("CREATE TABLE u (id BIGINT)")
```

## Java

```java
B4n1Db db = new B4n1Db("./data");
db.kv().put("greeting", "hello");
System.out.println(db.kv().get("greeting"));
db.sql().execute("CREATE TABLE u (id BIGINT)");
```

## What lives where

The wrappers carry no logic: no SQL parser, no queue bookkeeping, no storage
format. A fix in the engine reaches all seven packages at once, and the suite
fails when any wrapper drifts from the contract. `b4n1-db docs` prints the full
CLI reference offline on the machine you are on.

## License

Packages are distributed under the Business Source License 1.1, and the engine
binary inside them carries its EULA. See [License](license.md).
