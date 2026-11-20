namespace B4n1.Db.Engines
{
    /// <summary>Analytical SQL engine: <c>b4n1-db sql &lt;verb&gt;</c>.</summary>
    public sealed class SqlEngine : Engine
    {
        internal SqlEngine(Database db) : base(db) { }

        /// <summary>Runs a statement and reports the rows affected.</summary>
        public string Execute(string statement) => Call("sql", "execute", statement);

        /// <summary>Runs a query and returns <c>{"columns","rows"}</c>.</summary>
        public string Query(string statement) => Call("sql", "query", statement);

        /// <summary>Lists the tables registered in the engine.</summary>
        public string Tables() => Call("sql", "tables");

        /// <summary>Queries a CSV file as a table without copying it.</summary>
        public string RegisterCsv(string table, string file) =>
            Call("sql", "register-csv", table, file);

        /// <summary>Queries a JSON file as a table without copying it.</summary>
        public string RegisterJson(string table, string file) =>
            Call("sql", "register-json", table, file);

        /// <summary>Queries a Parquet file as a table without copying it.</summary>
        public string RegisterParquet(string table, string file) =>
            Call("sql", "register-parquet", table, file);

        /// <summary>Removes a table and its persisted copy.</summary>
        public string Drop(string table) => Call("sql", "drop", table);
    }
}
