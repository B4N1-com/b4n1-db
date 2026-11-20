"""Vector engine: `b4n1-db vector <verb>`."""

from .._base import EngineGroup, json_arg


class Vector(EngineGroup):
    """Nearest neighbours by cosine similarity."""

    def insert(self, namespace, vector, metadata=None):
        """Insert `vector` (a list of floats) with optional metadata."""
        return self._call(
            "vector", "insert", namespace, json_arg(vector), json_arg(metadata)
        )

    def search(self, namespace, query, top_k=None, filter_by=None):
        """Return the `top_k` nearest neighbours of `query`."""
        args = ["vector", "search", namespace, json_arg(query)]
        if top_k is not None or filter_by is not None:
            args.append(str(int(top_k)) if top_k is not None else "5")
        if filter_by is not None:
            args.append(json_arg(filter_by))
        return self._call(*args)

    def delete(self, namespace, record_id):
        """Remove a record."""
        return self._call("vector", "delete", namespace, record_id)

    def list(self, namespace):
        """List the record ids in a namespace."""
        return self._call("vector", "list", namespace)
