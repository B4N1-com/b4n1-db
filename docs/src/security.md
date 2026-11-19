# Security and Isolation

## Encryption at rest

Blind mode seals values with **AES-256-GCM** before they reach the store. It is
a property of the data, not of the handle: the mode is recorded on disk, so a
database reopened tomorrow still knows its values are ciphertext.

The key is resolved once, at open time, in this order and no other:

1. **`B4N1_HESP_KEY`** — 64 hex characters (32 bytes). A malformed value fails
   loudly; it never falls through to a weaker source.
2. **A keyfile next to the data** — so the database can be moved to another
   machine and still be read.
3. **Hardware fingerprint** — a hash of stable machine identity. Last resort
   only: it ties the data to this machine, and changing the hostname makes the
   data unreadable. Do not rely on it in production; set the variable or ship
   the keyfile.

```bash
export B4N1_HESP_KEY=$(openssl rand -hex 32)
```

Keep that value out of the database directory and out of version control — the
whole point of sealing at rest is that the two never travel together.

## Multi-tenancy

A tenant prefixes every key with `{tenant}:`. A handle bound to `acme` writes
`key` as `acme:key`, and a handle without a tenant cannot see it.

**This is a key prefix, not an authorization policy.** A handle bound to a
tenant sees only its prefix; a handle without one sees everything. It prevents
accidents between tenants that share a database — it is not access control, and
it must not be the only thing between two customers.

## What never leaves the machine

There is no code path that opens a network connection. Data does not leave the
process, no port is opened, and no telemetry is sent. That makes the threat
model small enough to state in one line: whoever can read the data directory can
read the database unless blind mode is on.

## Audit as evidence

The Merkle DAG is the record of what happened, not just a log. `verify` walks
every node and fails at the first mismatch, and `root-hash` is stable across
restarts — so a value you recorded yesterday can be proven unchanged today, and
a modification cannot be hidden without breaking the chain.
