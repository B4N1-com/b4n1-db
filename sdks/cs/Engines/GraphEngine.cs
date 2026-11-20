namespace B4n1.Db.Engines
{
    /// <summary>Knowledge graph engine: <c>b4n1-db graph &lt;verb&gt;</c>.</summary>
    public sealed class GraphEngine : Engine
    {
        internal GraphEngine(Database db) : base(db) { }

        /// <summary>Node types the engine accepts; anything else is refused.</summary>
        public static readonly string[] NodeTypes =
        {
            "instruction", "intention", "contract", "pattern", "scaffold", "qapair", "cyclerecord",
        };

        /// <summary>Adds a node and returns its id.</summary>
        public string AddNode(string type, string content) => Call("graph", "add-node", type, content);

        /// <summary>Relates two existing nodes (weight defaults to 1.0).</summary>
        public string AddRelation(string from, string to, string relation, double? weight = null) =>
            weight is null
                ? Call("graph", "add-relation", from, to, relation)
                : Call("graph", "add-relation", from, to, relation,
                       weight.Value.ToString("0.0###", System.Globalization.CultureInfo.InvariantCulture));

        /// <summary>Fetches one node with its relations.</summary>
        public string Node(string id) => Call("graph", "node", id);

        /// <summary>Hybrid search over content and graph adjacency.</summary>
        public string Search(string query, long? limit = null) =>
            limit is null ? Call("graph", "search", query) : Call("graph", "search", query, Num(limit.Value));

        /// <summary>Removes a node and cascades across its relations.</summary>
        public string Forget(string id) => Call("graph", "forget", id);
    }
}
