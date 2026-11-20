namespace B4n1.Db.Engines
{
    /// <summary>Key-value engine: <c>b4n1-db kv &lt;verb&gt;</c>.</summary>
    public sealed class KvEngine : Engine
    {
        internal KvEngine(Database db) : base(db) { }

        /// <summary>Stores a value under a key.</summary>
        public string Put(string key, string value) => Call("kv", "put", key, value);

        /// <summary>Reads a value back as <c>{"key","value"}</c>.</summary>
        public string Get(string key) => Call("kv", "get", key);

        /// <summary>Removes a key.</summary>
        public string Delete(string key) => Call("kv", "delete", key);

        /// <summary>Lists the pairs whose key starts with the prefix.</summary>
        public string Scan(string prefix) => Call("kv", "scan", prefix);

        /// <summary>Stores a value that expires after <paramref name="seconds"/>.</summary>
        public string PutTtl(string key, string value, long seconds) =>
            Call("kv", "put-ttl", key, value, Num(seconds));

        /// <summary>Seconds left before the key expires.</summary>
        public string Ttl(string key) => Call("kv", "ttl", key);

        /// <summary>Drops every expired key.</summary>
        public string PurgeExpired() => Call("kv", "purge-expired");
    }
}
