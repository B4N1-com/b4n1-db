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
db.kvPut("greeting", "hello");
console.log(db.kvGet("greeting")); // { key: "greeting", value: "hello" }
console.log(db.stats());           // { current_version, kv_entries, merkle_nodes }
console.log(db.demo());            // guided offline tour
```

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db npm test
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
