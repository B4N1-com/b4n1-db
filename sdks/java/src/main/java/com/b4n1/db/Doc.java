package com.b4n1.db;

import java.io.IOException;

/** Document collection engine: {@code b4n1-db doc <verb>}. */
public final class Doc extends Engine {
    Doc(B4n1Db db) {
        super(db);
    }

    /** Inserts a document from a JSON object of fields and returns its id. */
    public String insert(String collection, String payloadJson) throws IOException {
        return call("doc", "insert", collection, payloadJson);
    }

    /** Fetches one document by id. */
    public String findOne(String collection, String id) throws IOException {
        return call("doc", "find-one", collection, id);
    }

    /** Lists a collection. */
    public String find(String collection) throws IOException {
        return call("doc", "find", collection);
    }

    /** Queries by an indexed field, falling back to a full scan. */
    public String findByField(String collection, String field, String valueJson) throws IOException {
        return call("doc", "find-by-field", collection, field, valueJson);
    }

    /** Applies a partial update from a JSON object of fields. */
    public String update(String collection, String id, String payloadJson) throws IOException {
        return call("doc", "update", collection, id, payloadJson);
    }

    /** Deletes a document. */
    public String delete(String collection, String id) throws IOException {
        return call("doc", "delete", collection, id);
    }
}
