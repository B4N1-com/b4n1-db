namespace B4n1.Db.Engines
{
    /// <summary>Vector engine: <c>b4n1-db vector &lt;verb&gt;</c>.</summary>
    public sealed class VectorEngine : Engine
    {
        internal VectorEngine(Database db) : base(db) { }

        /// <summary>Inserts a vector (dimension fixed by the engine) with metadata.</summary>
        public string Insert(string ns, double[] vector, object? metadata = null) =>
            Call("vector", "insert", ns, Json(vector), Json(metadata));

        /// <summary>Returns the nearest neighbours by cosine similarity.</summary>
        public string Search(string ns, double[] query, long? topK = null, object? filter = null)
        {
            if (topK is null && filter is null)
            {
                return Call("vector", "search", ns, Json(query));
            }

            var k = Num(topK ?? 5);
            return filter is null
                ? Call("vector", "search", ns, Json(query), k)
                : Call("vector", "search", ns, Json(query), k, Json(filter));
        }

        /// <summary>Removes a record.</summary>
        public string Delete(string ns, string id) => Call("vector", "delete", ns, id);

        /// <summary>Lists the record ids in a namespace.</summary>
        public string List(string ns) => Call("vector", "list", ns);
    }
}
