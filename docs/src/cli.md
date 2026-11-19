# CLI Reference

```bash
b4n1-db <verb> [arguments] [--db <path>] [--json]
```

Two rules hold everywhere: `--json` gives pure JSON on stdout (nothing else is
written there), and `--db <path>` picks the database directory (default
`./data`, or `B4N1_DB_PATH`).

`b4n1-db docs` prints this reference from **inside the binary**, so it works on
a machine with no network. `b4n1-db docs --section <slug>` prints one chapter.

## Verbs

| Group | Verbs |
|---|---|
| `kv` | `put`, `get`, `delete`, `scan`, `put-ttl`, `ttl`, `purge-expired` |
| `sql` | `execute`, `query`, `tables`, `drop` |
| `graph` | `add-node`, `add-relation`, `node`, `search`, `forget` |
| `doc` | `insert`, `find-one`, `find`, `find-by-field`, `update`, `delete` |
| `queue` | `push`, `pop`, `peek`, `ack`, `nack`, `clear`, `len` |
| `vec` | `insert`, `search`, `delete`, `list` |
| `audit` | `verify`, `history`, `latest`, `time-travel`, `root-hash` |
| `migration` | `list`, `add`, `apply`, `exec`, `rollback` |
| `transfer` | `backup`, `restore`, `export`, `import` |
| — | `stats`, `demo`, `docs` |

`b4n1-db --help` lists every verb with its arguments; `b4n1-db demo` walks the
whole surface over a throwaway directory.

## Exit codes

| Code | Meaning |
|---|---|
| `0` | Success |
| `1` | Runtime error (IO, the store, a failed operation) |
| `2` | Usage error raised by the argument parser |
| `3` | Configuration invalid or missing |
| `4` | Invalid use of the tool (unknown verb, bad argument) |
| `130` | Interrupted by the user |
| `141` | Closed pipe — never an application error |

Every error prints one actionable line plus a `code:` and a `hint:`, so a script
can branch on the code and a person can act on the hint.

## Environment

| Variable | Effect |
|---|---|
| `B4N1_DB_PATH` | Default database directory |
| `B4N1_HESP_KEY` | 64 hex characters that seal values at rest; a malformed value fails loudly |
| `NO_COLOR` | When set, output contains no ANSI bytes |

## Piping

The binary restores the default `SIGPIPE` behaviour, so `b4n1-db stats | head`
exits quietly instead of printing a broken-pipe error. Exit code `141` from a
pipeline is the pipe closing, not a failure of the database.
