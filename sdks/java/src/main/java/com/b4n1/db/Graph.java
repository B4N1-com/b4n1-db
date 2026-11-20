package com.b4n1.db;

import java.io.IOException;

/** Knowledge graph engine: {@code b4n1-db graph <verb>}. */
public final class Graph extends Engine {
    Graph(B4n1Db db) {
        super(db);
    }

    /** Node types the engine accepts; anything else is refused. */
    public static final String[] NODE_TYPES = {
        "instruction", "intention", "contract", "pattern", "scaffold", "qapair", "cyclerecord",
    };

    /** Adds a node and returns its id. */
    public String addNode(String type, String content) throws IOException {
        return call("graph", "add-node", type, content);
    }

    /** Relates two existing nodes; weight defaults to 1.0 in the engine. */
    public String addRelation(String from, String to, String relation) throws IOException {
        return call("graph", "add-relation", from, to, relation);
    }

    /** Relates two existing nodes with an explicit weight. */
    public String addRelation(String from, String to, String relation, double weight) throws IOException {
        return call("graph", "add-relation", from, to, relation,
                java.math.BigDecimal.valueOf(weight).stripTrailingZeros().toPlainString());
    }

    /** Fetches one node with its relations. */
    public String node(String id) throws IOException {
        return call("graph", "node", id);
    }

    /** Hybrid search over content and graph adjacency. */
    public String search(String query) throws IOException {
        return call("graph", "search", query);
    }

    /** Hybrid search with an explicit limit. */
    public String search(String query, long limit) throws IOException {
        return call("graph", "search", query, num(limit));
    }

    /** Removes a node and cascades across its relations. */
    public String forget(String id) throws IOException {
        return call("graph", "forget", id);
    }
}
