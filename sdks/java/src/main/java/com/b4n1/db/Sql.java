package com.b4n1.db;

import java.io.IOException;

/** Analytical SQL engine: {@code b4n1-db sql <verb>}. */
public final class Sql extends Engine {
    Sql(B4n1Db db) {
        super(db);
    }

    /** Runs a statement and reports the rows affected. */
    public String execute(String statement) throws IOException {
        return call("sql", "execute", statement);
    }

    /** Runs a query and returns {"columns","rows"}. */
    public String query(String statement) throws IOException {
        return call("sql", "query", statement);
    }

    /** Lists the tables registered in the engine. */
    public String tables() throws IOException {
        return call("sql", "tables");
    }

    /** Queries a CSV file as a table without copying it. */
    public String registerCsv(String table, String file) throws IOException {
        return call("sql", "register-csv", table, file);
    }

    /** Queries a JSON file as a table without copying it. */
    public String registerJson(String table, String file) throws IOException {
        return call("sql", "register-json", table, file);
    }

    /** Queries a Parquet file as a table without copying it. */
    public String registerParquet(String table, String file) throws IOException {
        return call("sql", "register-parquet", table, file);
    }

    /** Removes a table and its persisted copy. */
    public String drop(String table) throws IOException {
        return call("sql", "drop", table);
    }
}
