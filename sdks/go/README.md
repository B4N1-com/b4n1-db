# b4n1-db — Go SDK

Thin Go wrapper over the `b4n1-db` standalone binary. All storage, SQL, graph,
vector and audit logic lives in the binary; this package only invokes it.

## Install

```bash
go get github.com/B4N1-com/b4n1-db/polyglot/go
```

The binary is resolved in this order: `B4N1_DB_BIN`, then `b4n1-db` on `PATH`.

## Usage

```go
package main

import (
	"fmt"

	b4n1db "github.com/B4N1-com/b4n1-db/polyglot/go"
)

func main() {
	db := b4n1db.New("./data")
	if err := db.Put("greeting", "hello"); err != nil {
		panic(err)
	}
	entry, err := db.Get("greeting")
	if err != nil {
		panic(err)
	}
	fmt.Println(entry.Key, entry.Value)

	stats, _ := db.Stats()
	fmt.Println(stats.KvEntries)
}
```

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db go test ./...
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
