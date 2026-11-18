# b4n1-db — PHP SDK

Thin PHP wrapper over the `b4n1-db` standalone binary. All storage, SQL, graph,
vector and audit logic lives in the binary; this package only invokes it.

## Install

```bash
composer require b4n1/b4n1-db
```

The binary is resolved in this order: `B4N1_DB_BIN`, then `b4n1-db` on `PATH`.

## Usage

```php
<?php
require 'vendor/autoload.php';

use B4n1\Db\Db;

$db = new Db('./data');
$db->put('greeting', 'hello');
var_dump($db->get('greeting'));   // ['key' => 'greeting', 'value' => 'hello']
var_dump($db->stats()['kv_entries']); // 1
echo $db->demo();
```

## Test

```bash
B4N1_DB_BIN=/path/to/b4n1-db php -r 'require "src/Db.php"; $d = new B4n1\Db\Db(sys_get_temp_dir()."/b4n1-php"); $d->put("a","1"); var_dump($d->get("a"));'
```

## License

BUSL-1.1 (see `LICENSE`). Binaries are governed by `EULA.txt`.
