package com.b4n1.db;

import java.io.IOException;

/** Merkle-DAG audit engine: {@code b4n1-db audit <verb>}. */
public final class Audit extends Engine {
    Audit(B4n1Db db) {
        super(db);
    }

    /** Recomputes every node hash; fails at the first mismatch. */
    public String verify() throws IOException {
        return call("audit", "verify");
    }

    /** Full history of a key, oldest first. */
    public String history(String key) throws IOException {
        return call("audit", "history", key);
    }

    /** Newest node for a key. */
    public String latest(String key) throws IOException {
        return call("audit", "latest", key);
    }

    /** Reads a key as of a past instant (epoch milliseconds). */
    public String timeTravel(String key, long timestampMs) throws IOException {
        return call("audit", "time-travel", key, num(timestampMs));
    }

    /** Folds every node hash into one root hash. */
    public String rootHash() throws IOException {
        return call("audit", "root-hash");
    }
}
