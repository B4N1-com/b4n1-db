package com.b4n1.db;

import java.io.IOException;

/** Key-value engine: {@code b4n1-db kv <verb>}. */
public final class Kv extends Engine {
    Kv(B4n1Db db) {
        super(db);
    }

    /** Stores a value under a key. */
    public String put(String key, String value) throws IOException {
        return call("kv", "put", key, value);
    }

    /** Reads a value back as {"key","value"}. */
    public String get(String key) throws IOException {
        return call("kv", "get", key);
    }

    /** Removes a key. */
    public String delete(String key) throws IOException {
        return call("kv", "delete", key);
    }

    /** Lists the pairs whose key starts with the prefix. */
    public String scan(String prefix) throws IOException {
        return call("kv", "scan", prefix);
    }

    /** Stores a value that expires after the given seconds. */
    public String putTtl(String key, String value, long seconds) throws IOException {
        return call("kv", "put-ttl", key, value, num(seconds));
    }

    /** Seconds left before the key expires. */
    public String ttl(String key) throws IOException {
        return call("kv", "ttl", key);
    }

    /** Drops every expired key. */
    public String purgeExpired() throws IOException {
        return call("kv", "purge-expired");
    }
}
