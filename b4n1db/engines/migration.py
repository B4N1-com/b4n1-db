"""Schema migration registry: `b4n1-db migration <verb>`."""

from .._base import EngineGroup


class Migration(EngineGroup):
    """Versioned migration steps, recorded once applied."""

    def list(self):
        """Current version, applied records and pending migrations."""
        return self._call("migration", "list")

    def add(self, version, name, up_sql):
        """Register a migration without applying it."""
        return self._call("migration", "add", str(int(version)), name, up_sql)

    def apply(self):
        """Mark pending migrations applied WITHOUT running their DDL."""
        return self._call("migration", "apply")

    def exec(self):
        """Run each pending migration's up_sql, rolling back the batch on failure."""
        return self._call("migration", "exec")

    def rollback(self, target):
        """Roll the registry back to `target`."""
        return self._call("migration", "rollback", str(int(target)))
