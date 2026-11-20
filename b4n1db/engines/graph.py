"""Knowledge graph engine: `b4n1-db graph <verb>`."""

from .._base import EngineGroup


class Graph(EngineGroup):
    """Typed nodes, weighted relations and hybrid search."""

    #: Node types the engine accepts; anything else is refused with
    #: `urn:b4n1:error:b4n1-db:runtime`.
    NODE_TYPES = (
        "instruction",
        "intention",
        "contract",
        "pattern",
        "scaffold",
        "qapair",
        "cyclerecord",
    )

    def add_node(self, node_type, content):
        """Add a node of `node_type` (see `NODE_TYPES`) and return its id."""
        return self._call("graph", "add-node", node_type, content)

    def add_relation(self, source, target, relation, weight=None):
        """Relate two existing nodes; `weight` defaults to 1.0 in the engine."""
        args = ["graph", "add-relation", source, target, relation]
        if weight is not None:
            args.append(str(float(weight)))
        return self._call(*args)

    def node(self, node_id):
        """Fetch one node together with its relations."""
        return self._call("graph", "node", node_id)

    def search(self, query, limit=None):
        """Hybrid search over content and graph adjacency."""
        args = ["graph", "search", query]
        if limit is not None:
            args.append(str(int(limit)))
        return self._call(*args)

    def forget(self, node_id):
        """Remove a node and cascade across its relations."""
        return self._call("graph", "forget", node_id)
