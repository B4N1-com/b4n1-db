"""Analytical SQL engine: `b4n1-db sql <verb>`."""

from .._base import EngineGroup


class Sql(EngineGroup):
    """DataFusion-backed SQL. Tables persist as Arrow IPC across processes."""

    def execute(self, statement):
        """Run one statement (CREATE, INSERT, DROP, …) and report rows affected."""
        return self._call("sql", "execute", statement)

    def query(self, statement):
        """Run a SELECT and return `{"columns", "rows"}`."""
        return self._call("sql", "query", statement)

    def tables(self):
        """List the tables registered in the engine."""
        return self._call("sql", "tables")

    def register_csv(self, table, path):
        """Query a CSV file as `table` without copying it."""
        return self._call("sql", "register-csv", table, str(path))

    def register_json(self, table, path):
        """Query a JSON file as `table` without copying it."""
        return self._call("sql", "register-json", table, str(path))

    def register_parquet(self, table, path):
        """Query a Parquet file as `table` without copying it."""
        return self._call("sql", "register-parquet", table, str(path))

    def drop(self, table):
        """Remove a table and its persisted copy."""
        return self._call("sql", "drop", table)
