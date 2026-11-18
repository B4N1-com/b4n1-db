# b4n1-db — Ruby SDK

Thin Ruby wrapper over the `b4n1-db` standalone binary. All storage, SQL,
graph, vector and audit logic lives in the binary; this gem only invokes it.

## Install

```bash
gem install b4n1-db
```

The binary is resolved in this order: `B4N1_DB_BIN`, then `b4n1-db` on `PATH`.

## Usage

```ruby
require "b4n1-db"

db = B4n1Db::DB.new("./data")
db.put("greeting", "hello")
puts db.get("greeting")      # {"key"=>"greeting", "value"=>"hello"}
puts db.stats["kv_entries"]  # 1
puts db.demo                 # guided offline tour
```

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db ruby test/test_basic.rb
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
