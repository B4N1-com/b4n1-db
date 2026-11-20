"""Document collection engine: `b4n1-db doc <verb>`."""

from .._base import EngineGroup, json_arg, json_value


class Doc(EngineGroup):
    """Structured records with indexed field queries."""

    def insert(self, collection, payload):
        """Insert `payload` (a dict or a JSON string) and return its id."""
        return self._call("doc", "insert", collection, json_arg(payload))

    def find_one(self, collection, doc_id):
        """Fetch one document by id."""
        return self._call("doc", "find-one", collection, doc_id)

    def find(self, collection):
        """List a collection."""
        return self._call("doc", "find", collection)

    def find_by_field(self, collection, field, value):
        """Query by an indexed field, falling back to a full scan.

        `value` may be any JSON-serialisable scalar (`"ada"`, `36`, `True`).
        """
        return self._call("doc", "find-by-field", collection, field, json_value(value))

    def update(self, collection, doc_id, payload):
        """Apply a partial update from a dict or JSON string."""
        return self._call("doc", "update", collection, doc_id, json_arg(payload))

    def delete(self, collection, doc_id):
        """Delete a document."""
        return self._call("doc", "delete", collection, doc_id)
