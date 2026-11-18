"""b4n1-db Python SDK — thin wrapper over the `b4n1-db` binary (§9/§10m).

The heavy lifting lives in the Rust binary; this package only shells out to it
so the consumer never needs a Rust toolchain. The binary is looked up in this
order: ``B4N1_DB_BIN``, the copy bundled in this wheel (``bin/``), then
``PATH`` — the bundled one is what makes ``pip install b4n1-db`` work with no
compiler and no extra download.
"""

__version__ = "0.1.1"

import os
import shutil
import subprocess
import json


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
    import platform
    import sys

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
    def __init__(self, path: str = "./data"):
        self.path = path

    def _run(self, *args, json_out: bool = True):
        cmd = [_binary(), "--db", self.path]
        if json_out:
            cmd.append("--json")
        cmd.extend(args)
        proc = subprocess.run(cmd, capture_output=True, text=True)
        if proc.returncode != 0:
            raise RuntimeError(proc.stderr.strip() or proc.stdout.strip())
        if json_out and proc.stdout.strip():
            return json.loads(proc.stdout)
        return proc.stdout

    def stats(self):
        return self._run("stats")

    def kv_put(self, key, value):
        # `kv put` answers with a human line even under --json ("OK put k (1
        # bytes)"), so asking for JSON here raised JSONDecodeError on every
        # write. Writes return the raw text; reads and stats stay JSON.
        return self._run("kv", "put", key, value, json_out=False)

    def kv_get(self, key):
        return self._run("kv", "get", key)

    def demo(self):
        return self._run("demo", json_out=False)
