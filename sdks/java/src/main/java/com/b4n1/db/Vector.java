package com.b4n1.db;

import java.io.IOException;

/** Vector engine: {@code b4n1-db vector <verb>}. */
public final class Vector extends Engine {
    Vector(B4n1Db db) {
        super(db);
    }

    /** Renders a vector as a JSON array. */
    public static String json(double[] vector) {
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) {
                out.append(',');
            }
            out.append(vector[i]);
        }
        return out.append(']').toString();
    }

    /** Inserts a vector (dimension fixed by the engine) with metadata JSON. */
    public String insert(String namespace, double[] vector, String metadataJson) throws IOException {
        return call("vector", "insert", namespace, json(vector), metadataJson == null ? "" : metadataJson);
    }

    /** Inserts a vector without metadata. */
    public String insert(String namespace, double[] vector) throws IOException {
        return insert(namespace, vector, null);
    }

    /** Returns the nearest neighbours by cosine similarity. */
    public String search(String namespace, double[] query, long topK, String filterJson) throws IOException {
        return call("vector", "search", namespace, json(query), num(topK),
                filterJson == null ? "" : filterJson);
    }

    /** Returns the nearest neighbours without a metadata filter. */
    public String search(String namespace, double[] query, long topK) throws IOException {
        return call("vector", "search", namespace, json(query), num(topK));
    }

    /** Removes a record. */
    public String delete(String namespace, String id) throws IOException {
        return call("vector", "delete", namespace, id);
    }

    /** Lists the record ids in a namespace. */
    public String list(String namespace) throws IOException {
        return call("vector", "list", namespace);
    }
}
