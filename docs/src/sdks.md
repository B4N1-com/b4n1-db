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

Java, PHP and Go wrappers ship **in the package archive** (a JAR with its native
library, a Composer package and a Go module) rather than through their central
registries, which are tracked separately. Take them from the archive you were
given, not from a registry that does not carry them yet.

Platforms: Linux x86_64 and arm64, macOS Intel and Apple silicon, Windows
x86_64. If no build matches your platform the install fails rather than handing
you a wrapper that cannot run.

## Python

```python
from b4n1db import DB

db = DB("./data")
db.kv_put("greeting", "hello")
print(db.kv_get("greeting"))     # b"hello"
print(db.stats())
```

## Node.js

```javascript
const { DB } = require("b4n1-db");

const db = new DB("./data");
db.kvPut("greeting", "hello");
console.log(db.kvGet("greeting"));
```

## What the wrapper covers

The key-value surface and `stats` are wrapped directly; every other engine is
reached through the same engine binary the package carries. The [CLI
Reference](cli.md) documents that surface in full, and `b4n1-db docs` prints it
offline on the machine you are on.

## License

Packages are distributed under the Business Source License 1.1, and the engine
binary inside them carries its EULA. See [License](license.md).
