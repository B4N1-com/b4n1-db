"""Shared plumbing for the per-engine groups.

Each engine of the `b4n1-db` binary is exposed as one group on the `DB` handle
(`db.kv`, `db.sql`, `db.graph`, …). A group holds no state of its own: it
forwards to the owning handle, so `B4N1_DB_BIN` and the database path are
resolved once, at `DB(...)`.
"""


class EngineGroup:
    """Base for the nine engine groups: argument marshalling only."""

    def __init__(self, db):
        self._db = db

    def _call(self, *args, **kwargs):
        return self._db._run(*args, **kwargs)


def json_value(value):
    """Encode one scalar/object as a JSON *value*.

    `find-by-field` parses its argument as JSON, so a bare `ada` is rejected:
    the text for a string value must be `"ada"`. Encoding here keeps that
    detail out of every caller.
    """
    import json

    return json.dumps(value, separators=(",", ":"), ensure_ascii=False)


def json_arg(value, empty=""):
    """Serialise a dict/list argument, or return `empty` when it is missing.

    The binary takes JSON on the command line for metadata, filters, document
    payloads and vectors, so the wrapper is the only place that turns Python
    objects into that text.
    """
    import json

    if value is None or value == "":
        return empty
    if isinstance(value, str):
        return value
    return json.dumps(value, separators=(",", ":"), ensure_ascii=False)
