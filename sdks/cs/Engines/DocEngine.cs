namespace B4n1.Db.Engines
{
    /// <summary>Document collection engine: <c>b4n1-db doc &lt;verb&gt;</c>.</summary>
    public sealed class DocEngine : Engine
    {
        internal DocEngine(Database db) : base(db) { }

        /// <summary>Inserts a document from an object of fields and returns its id.</summary>
        public string Insert(string collection, object payload) =>
            Call("doc", "insert", collection, Json(payload));

        /// <summary>Fetches one document by id.</summary>
        public string FindOne(string collection, string id) => Call("doc", "find-one", collection, id);

        /// <summary>Lists a collection.</summary>
        public string Find(string collection) => Call("doc", "find", collection);

        /// <summary>Queries by an indexed field, falling back to a full scan.</summary>
        public string FindByField(string collection, string field, object value) =>
            Call("doc", "find-by-field", collection, field, Json(value));

        /// <summary>Applies a partial update from an object of fields.</summary>
        public string Update(string collection, string id, object payload) =>
            Call("doc", "update", collection, id, Json(payload));

        /// <summary>Deletes a document.</summary>
        public string Delete(string collection, string id) => Call("doc", "delete", collection, id);
    }
}
