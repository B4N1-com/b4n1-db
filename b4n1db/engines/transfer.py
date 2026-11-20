"""Backup and transfer engine: `b4n1-db transfer <verb>`."""

from .._base import EngineGroup


class Transfer(EngineGroup):
    """Copy the database, merge another one, or move it as JSON Lines."""

    def backup(self, destination):
        """Copy the whole database directory to `destination`."""
        return self._call("transfer", "backup", str(destination))

    def restore(self, source):
        """Merge `source` into this database, forward only."""
        return self._call("transfer", "restore", str(source))

    def export(self, destination):
        """Write a portable JSON Lines dump to `destination`."""
        return self._call("transfer", "export", str(destination))

    def import_(self, source):
        """Load a JSON Lines dump produced by `export`.

        Trailing underscore because `import` is a Python keyword.
        """
        return self._call("transfer", "import", str(source))
