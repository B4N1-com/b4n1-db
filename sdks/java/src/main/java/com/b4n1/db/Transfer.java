package com.b4n1.db;

import java.io.IOException;

/** Backup and transfer engine: {@code b4n1-db transfer <verb>}. */
public final class Transfer extends Engine {
    Transfer(B4n1Db db) {
        super(db);
    }

    /** Copies the whole database directory to a destination. */
    public String backup(String destination) throws IOException {
        return call("transfer", "backup", destination);
    }

    /** Merges another database into this one, forward only. */
    public String restore(String source) throws IOException {
        return call("transfer", "restore", source);
    }

    /** Writes a portable JSON Lines dump. */
    public String export(String destination) throws IOException {
        return call("transfer", "export", destination);
    }

    /** Loads a JSON Lines dump produced by {@link #export}. */
    public String importFrom(String source) throws IOException {
        return call("transfer", "import", source);
    }
}
