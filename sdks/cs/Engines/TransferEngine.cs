namespace B4n1.Db.Engines
{
    /// <summary>Backup and transfer engine: <c>b4n1-db transfer &lt;verb&gt;</c>.</summary>
    public sealed class TransferEngine : Engine
    {
        internal TransferEngine(Database db) : base(db) { }

        /// <summary>Copies the whole database directory to a destination.</summary>
        public string Backup(string destination) => Call("transfer", "backup", destination);

        /// <summary>Merges another database into this one, forward only.</summary>
        public string Restore(string source) => Call("transfer", "restore", source);

        /// <summary>Writes a portable JSON Lines dump.</summary>
        public string Export(string destination) => Call("transfer", "export", destination);

        /// <summary>Loads a JSON Lines dump produced by <see cref="Export"/>.</summary>
        public string Import(string source) => Call("transfer", "import", source);
    }
}
