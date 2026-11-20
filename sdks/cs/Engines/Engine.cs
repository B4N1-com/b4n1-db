using System.Globalization;
using System.Text.Json;

namespace B4n1.Db.Engines
{
    /// <summary>
    /// Base for the nine engine groups: argument marshalling only.
    /// Every method returns the binary's <c>--json</c> answer as text, so the
    /// package stays dependency-free. The contract is <c>sdk-surface.tsv</c> in
    /// the crate root and the suite fails when a wrapper drifts from it.
    /// </summary>
    public abstract class Engine
    {
        /// <summary>The handle that owns the database path and the binary.</summary>
        protected readonly Database Db;

        /// <summary>Wires a group to its handle.</summary>
        protected Engine(Database db)
        {
            Db = db;
        }

        /// <summary>Runs a verb and returns its JSON answer.</summary>
        protected string Call(params string[] args) => Db.Invoke(args);

        /// <summary>Serialises an object/array for a JSON flag.</summary>
        protected static string Json(object? value) =>
            value is null ? string.Empty : JsonSerializer.Serialize(value);

        /// <summary>Numbers as integers: the binary parses <c>3</c>, not <c>3.0</c>.</summary>
        protected static string Num(long value) => value.ToString(CultureInfo.InvariantCulture);
    }
}
