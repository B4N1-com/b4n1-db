package com.b4n1.db;

import java.io.IOException;

/** Schema migration registry: {@code b4n1-db migration <verb>}. */
public final class Migration extends Engine {
    Migration(B4n1Db db) {
        super(db);
    }

    /** Current version, applied records and pending migrations. */
    public String list() throws IOException {
        return call("migration", "list");
    }

    /** Registers a migration without applying it. */
    public String add(long version, String name, String upSql) throws IOException {
        return call("migration", "add", num(version), name, upSql);
    }

    /** Marks pending migrations applied WITHOUT running their DDL. */
    public String apply() throws IOException {
        return call("migration", "apply");
    }

    /** Runs each pending migration's up_sql, rolling back the batch on failure. */
    public String exec() throws IOException {
        return call("migration", "exec");
    }

    /** Rolls the registry back to a target version. */
    public String rollback(long target) throws IOException {
        return call("migration", "rollback", num(target));
    }
}
