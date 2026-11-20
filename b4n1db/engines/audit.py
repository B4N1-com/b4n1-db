"""Merkle-DAG audit engine: `b4n1-db audit <verb>`."""

from .._base import EngineGroup


class Audit(EngineGroup):
    """Tamper-evident history: verify, history, time travel, root hash."""

    def verify(self):
        """Recompute every node hash; raises at the first mismatch."""
        return self._call("audit", "verify")

    def history(self, key):
        """Full history of a key, oldest first."""
        return self._call("audit", "history", key)

    def latest(self, key):
        """Newest node for a key."""
        return self._call("audit", "latest", key)

    def time_travel(self, key, timestamp_ms):
        """Read the state of a key as of a past instant (epoch milliseconds)."""
        return self._call("audit", "time-travel", key, str(int(timestamp_ms)))

    def root_hash(self):
        """Fold every node hash into one root hash."""
        return self._call("audit", "root-hash")
