# b4n1-db — .NET SDK

Thin .NET wrapper over the `b4n1-db` standalone binary. All storage, SQL,
graph, vector and audit logic lives in the binary; this library only invokes it.

## Install

```bash
dotnet add package B4n1.Db
```

The binary is resolved in this order: `B4N1_DB_BIN`, then `b4n1-db` on `PATH`.

## Usage

```csharp
using B4n1.Db;

var db = new Database("./data");
db.Put("greeting", "hello");
Console.WriteLine(db.Get("greeting"));   // {"key":"greeting","value":"hello"}
Console.WriteLine(db.Stats());           // {"current_version":..., ...}
```

`Stats()` and `Get()` return JSON text so the wrapper has zero dependencies —
parse them with the JSON library your application already uses.

## Build & test

```bash
dotnet build
B4N1_DB_BIN=/path/to/b4n1-db dotnet pack -c Release
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
