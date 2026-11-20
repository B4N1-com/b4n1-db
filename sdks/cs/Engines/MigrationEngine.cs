namespace B4n1.Db.Engines
{
    /// <summary>Schema migration registry: <c>b4n1-db migration &lt;verb&gt;</c>.</summary>
    public sealed class MigrationEngine : Engine
    {
        internal MigrationEngine(Database db) : base(db) { }

        /// <summary>Current version, applied records and pending migrations.</summary>
        public string List() => Call("migration", "list");

        /// <summary>Registers a migration without applying it.</summary>
        public string Add(long version, string name, string upSql) =>
            Call("migration", "add", Num(version), name, upSql);

        /// <summary>Marks pending migrations applied WITHOUT running their DDL.</summary>
        public string Apply() => Call("migration", "apply");

        /// <summary>Runs each pending migration's up_sql, rolling back on failure.</summary>
        public string Exec() => Call("migration", "exec");

        /// <summary>Rolls the registry back to a target version.</summary>
        public string Rollback(long target) => Call("migration", "rollback", Num(target));
    }
}
