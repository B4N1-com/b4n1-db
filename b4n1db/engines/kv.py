"""Key-value engine: `b4n1-db kv <verb>`."""

from .._base import EngineGroup


class Kv(EngineGroup):
    """Ordered keys, TTL and prefix scans."""

    def put(self, key, value):
        """Store `value` under `key`. Returns the binary's confirmation line."""
        return self._call("kv", "put", key, value)

    def get(self, key):
        """Read `value` back as `{"key", "value"}`; raises when it is missing."""
        return self._call("kv", "get", key)

    def delete(self, key):
        """Remove `key`. Succeeds even when the key was already gone."""
        return self._call("kv", "delete", key)

    def scan(self, prefix):
        """List the pairs whose key starts with `prefix`."""
        return self._call("kv", "scan", prefix)

    def put_ttl(self, key, value, seconds):
        """Store `value` under `key` and expire it after `seconds`."""
        return self._call("kv", "put-ttl", key, value, str(int(seconds)))

    def ttl(self, key):
        """Seconds left before `key` expires."""
        return self._call("kv", "ttl", key)

    def purge_expired(self):
        """Drop every key whose TTL already elapsed."""
        return self._call("kv", "purge-expired")
