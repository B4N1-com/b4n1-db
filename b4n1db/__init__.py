"""b4n1-db Python SDK — thin wrapper over the `b4n1-db` binary (§9/§10m).

The heavy lifting lives in the Rust binary; this package only shells out to it
so the consumer never needs a Rust toolchain. The binary is looked up in this
order: ``B4N1_DB_BIN``, the copy bundled in this wheel (``bin/``), then
``PATH`` — the bundled one is what makes ``pip install b4n1-db`` work with no
compiler and no extra download.

Every engine of the binary is a group on the handle, one module each:

    db.kv.put("greeting", "hello")
    db.sql.execute("CREATE TABLE users (id BIGINT)")
    db.graph.add_node("person", "ada")
    db.queue.push("jobs", "build")
    db.audit.root_hash()

The full contract (9 engines, 50 methods) is `sdk-surface.tsv` in the crate
root, and the suite fails when a wrapper drifts from it.
"""

__version__ = "0.1.3"

import json
import os
import platform
import shutil
import subprocess
import sys

from .engines.audit import Audit
from .engines.doc import Doc
from .engines.graph import Graph
from .engines.kv import Kv
from .engines.migration import Migration
from .engines.queue import Queue
from .engines.sql import Sql
from .engines.transfer import Transfer
from .engines.vector import Vector


# Platform → file name inside the wheel's bin/ dir. Must match the names
# scripts/bundle-sdk-binaries.sh writes (one list, four readers).
_BUNDLED = {
    ("linux", "x86_64"): "b4n1-db-linux-amd64",
    ("linux", "arm64"): "b4n1-db-linux-arm64",
    ("darwin", "x86_64"): "b4n1-db-macos-x64",
    ("darwin", "arm64"): "b4n1-db-macos-arm64",
    ("win32", "x86_64"): "b4n1-db-windows-amd64.exe",
}

_ARCH = {"amd64": "x86_64", "x86_64": "x86_64", "arm64": "arm64", "aarch64": "arm64"}


def _bundled_binary() -> str:
    """Path to the binary shipped inside this package, or "" if absent."""
    system = "win32" if sys.platform == "win32" else platform.system().lower()
    name = _BUNDLED.get((system, _ARCH.get(platform.machine().lower(), "")))
    if not name:
        return ""
    candidate = os.path.join(os.path.dirname(os.path.abspath(__file__)), "bin", name)
    return candidate if os.path.isfile(candidate) else ""


def _binary() -> str:
    path = os.environ.get("B4N1_DB_BIN")
    if path and os.path.isfile(path):
        return path
    bundled = _bundled_binary()
    if bundled:
        return bundled
    found = shutil.which("b4n1-db")
    if found:
        return found
    raise FileNotFoundError(
        "b4n1-db binary not found; set B4N1_DB_BIN or install the binary"
    )


class DB:
    """One database directory, nine engines."""

    def __init__(self, path: str = "./data"):
        self.path = path
        self.kv = Kv(self)
        self.sql = Sql(self)
        self.graph = Graph(self)
        self.doc = Doc(self)
        self.queue = Queue(self)
        self.vector = Vector(self)
        self.audit = Audit(self)
        self.migration = Migration(self)
        self.transfer = Transfer(self)

    def _run(self, *args, json_out: bool = True):
        """Invoke the binary and return its answer.

        With `--json` the binary emits JSON for reads and `stats`, but plain
        text for mutations (`OK put k (1 bytes)`, `OK executed: 1 row(s)`).
        Parsing is therefore attempted and the raw line is returned when the
        answer is not JSON — the alternative is guessing per verb which of the
        fifty mutations answers in text, and being wrong on one.
        """
        cmd = [_binary(), "--db", self.path]
        if json_out:
            cmd.append("--json")
        cmd.extend(args)
        proc = subprocess.run(cmd, capture_output=True)
        out = proc.stdout.decode("utf-8", "replace")
        if proc.returncode != 0:
            raise RuntimeError(
                proc.stderr.decode("utf-8", "replace").strip() or out.strip()
            )
        if not json_out or not out.strip():
            return out
        try:
            return json.loads(out)
        except json.JSONDecodeError:
            return out

    # ── Flat KV aliases kept from 0.1: `db.kv_put(...)` still works ──────────
    def stats(self):
        """Current version, entry count and Merkle node count."""
        return self._run("stats")

    def kv_put(self, key, value):
        """Alias of `db.kv.put(...)`."""
        return self.kv.put(key, value)

    def kv_get(self, key):
        """Alias of `db.kv.get(...)`."""
        return self.kv.get(key)

    def demo(self):
        """One-click offline tour over a throwaway database."""
        return self._run("demo", json_out=False)
