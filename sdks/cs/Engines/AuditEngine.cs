namespace B4n1.Db.Engines
{
    /// <summary>Merkle-DAG audit engine: <c>b4n1-db audit &lt;verb&gt;</c>.</summary>
    public sealed class AuditEngine : Engine
    {
        internal AuditEngine(Database db) : base(db) { }

        /// <summary>Recomputes every node hash; fails at the first mismatch.</summary>
        public string Verify() => Call("audit", "verify");

        /// <summary>Full history of a key, oldest first.</summary>
        public string History(string key) => Call("audit", "history", key);

        /// <summary>Newest node for a key.</summary>
        public string Latest(string key) => Call("audit", "latest", key);

        /// <summary>Reads a key as of a past instant (epoch milliseconds).</summary>
        public string TimeTravel(string key, long timestampMs) =>
            Call("audit", "time-travel", key, Num(timestampMs));

        /// <summary>Folds every node hash into one root hash.</summary>
        public string RootHash() => Call("audit", "root-hash");
    }
}
