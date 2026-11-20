using System;
using System.Collections.Generic;
using System.IO;
using B4n1.Db;

// Every engine of the binary, exercised through the .NET SDK. The binary comes
// from B4N1_DB_BIN, which the suite points at the build under test.
var dir = Path.Combine(Path.GetTempPath(), "b4n1-db-cs-" + Guid.NewGuid().ToString("N"));
Directory.CreateDirectory(dir);
var db = new Database(Path.Combine(dir, "data"));
var failures = 0;

void Check(string what, bool ok)
{
    if (!ok)
    {
        failures++;
        Console.Error.WriteLine($"  FAIL {what}");
    }
}

string J(string s) => s;

// kv
Check("kv.put", db.Kv.Put("k", "v").Contains("\"ok\": true"));
Check("kv.get", db.Kv.Get("k").Contains("\"value\": \"v\""));
Check("kv.scan", db.Kv.Scan("").Contains("\"count\": 1"));
Check("kv.put_ttl", db.Kv.PutTtl("t", "x", 60).Contains("\"ttl_secs\": 60"));
Check("kv.ttl", !db.Kv.Ttl("t").Contains("\"ttl_secs\": 0"));
Check("kv.delete", db.Kv.Delete("k").Contains("\"ok\": true"));
Check("kv.purge_expired", db.Kv.PurgeExpired().Contains("\"purged\": 0"));
try { db.Kv.Get("k"); Check("kv.get missing must throw", false); }
catch (InvalidOperationException) { }

// sql
Check("sql.execute", db.Sql.Execute("CREATE TABLE u (id BIGINT, name VARCHAR)").Contains("\"rows_affected\": 0"));
Check("sql.execute insert", db.Sql.Execute("INSERT INTO u VALUES (1, 'ada')").Contains("\"rows_affected\": 1"));
Check("sql.query", db.Sql.Query("SELECT * FROM u").Contains("ada"));
Check("sql.tables", db.Sql.Tables().Contains("\"u\""));

var second = new Database(Path.Combine(dir, "data"));
Check("sql survives a second handle", second.Sql.Query("SELECT name FROM u").Contains("ada"));

var csv = Path.Combine(dir, "rows.csv");
File.WriteAllText(csv, "id\n7\n");
Check("sql.register_csv", db.Sql.RegisterCsv("c1", csv).Contains("\"ok\": true"));
Check("sql.query csv", db.Sql.Query("SELECT id FROM c1").Contains("\"id\": 7"));
Check("sql.drop", second.Sql.Drop("u").Contains("\"ok\": true"));
Check("sql.tables after drop", !second.Sql.Tables().Contains("\"u\""));

// graph
var node = db.Graph.AddNode("pattern", "ada");
var otherResponse = db.Graph.AddNode("pattern", "grace");
var nodeId = node.Split("\"id\": \"")[1].Split('"')[0];
var otherId = otherResponse.Split("\"id\": \"")[1].Split('"')[0];
Check("graph.add_node", nodeId.Length == 36);
Check("graph.node", db.Graph.Node(nodeId).Contains("\"node\""));
Check("graph.search", !db.Graph.Search("ada", 5).Contains("\"count\": 0"));
Check("graph.add_relation", db.Graph.AddRelation(nodeId, otherId, "follows", 0.5).Contains("\"relation\": \"follows\""));
Check("graph.forget", db.Graph.Forget(nodeId).Contains("\"ok\": true"));

// documents
var inserted = db.Doc.Insert("users", new Dictionary<string, object> { ["name"] = "ada", ["age"] = 36 });
var docId = inserted.Split("\"id\": \"")[1].Split('"')[0];
Check("doc.insert", inserted.Contains("\"collection\": \"users\""));
Check("doc.find_one", db.Doc.FindOne("users", docId).Contains(docId));
Check("doc.find", db.Doc.Find("users").Contains("\"count\": 1"));
Check("doc.update", db.Doc.Update("users", docId, new Dictionary<string, object> { ["age"] = 37 }).Contains("\"ok\": true"));
Check("doc.find_by_field", db.Doc.FindByField("users", "name", "ada").Contains("\"count\": 1"));
Check("doc.delete", db.Doc.Delete("users", docId).Contains("\"ok\": true"));
Check("doc.find after delete", db.Doc.Find("users").Contains("\"count\": 0"));

// queue
var pushed = db.Queue.Push("jobs", "build");
var msgId = pushed.Split("\"id\": \"")[1].Split('"')[0];
Check("queue.len", db.Queue.Len("jobs").Contains("\"pending\": 1"));
Check("queue.peek", db.Queue.Peek("jobs").Contains("\"body\": \"build\""));
Check("queue.pop", db.Queue.Pop("jobs").Contains(msgId));
Check("queue.nack", db.Queue.Nack("jobs", msgId).Contains("\"requeued\": true"));
db.Queue.Pop("jobs");
Check("queue.ack", db.Queue.Ack("jobs", msgId).Contains("\"ok\": true"));
Check("queue.clear", db.Queue.Clear("jobs").Contains("\"ok\": true"));

// vectors
var embedding = new double[1536];
embedding[0] = 1.0;
var record = db.Vector.Insert("ns", embedding, new Dictionary<string, object> { ["tag"] = "a" });
var recordId = record.Split("\"id\": \"")[1].Split('"')[0];
Check("vector.insert", recordId.Length == 36);
Check("vector.search", db.Vector.Search("ns", embedding, 3, new Dictionary<string, object> { ["tag"] = "a" }).Contains(recordId));
Check("vector.list", db.Vector.List("ns").Contains("\"count\": 1"));
Check("vector.delete", db.Vector.Delete("ns", recordId).Contains("\"ok\": true"));

// audit
db.Kv.Put("a", "1");
Check("audit.verify", db.Audit.Verify().Contains("\"intact\": true"));
Check("audit.history", db.Audit.History("a").Contains("\"count\": 1"));
var latest = db.Audit.Latest("a");
var stamp = J(latest.Split("\"timestamp\": ")[1].Split(',')[0].Split('}')[0].Trim());
Check("audit.latest", latest.Contains("\"key\": \"a\""));
Check("audit.time_travel", db.Audit.TimeTravel("a", long.Parse(stamp)).Contains("\"node\""));
Check("audit.root_hash", db.Audit.RootHash().Contains("\"root_hash\": \""));

// migrations
Check("migration.add", db.Migration.Add(1, "init", "CREATE TABLE m (id BIGINT)").Contains("\"registered\": true"));
var third = new Database(Path.Combine(dir, "data"));
Check("migration.list", third.Migration.List().Contains("\"registered\": 1"));
Check("migration.exec", third.Migration.Exec().Contains("\"executed\": [\n    1\n  ]") || third.Migration.List().Contains("\"current_version\": 1"));
Check("migration.apply", third.Migration.Apply().Contains("\"ddl_executed\": false"));
Check("migration.rollback", third.Migration.Rollback(0).Contains("\"rolled_back\": ["));
try { third.Migration.Add(1, "again", "SELECT 1"); Check("migration.add duplicate must throw", false); }
catch (InvalidOperationException) { }

// transfer
db.Kv.Put("keep", "me");
Check("transfer.backup", db.Transfer.Backup(Path.Combine(dir, "bak")).Contains("\"files\": "));
Check("transfer.export", db.Transfer.Export(Path.Combine(dir, "dump.jsonl")).Contains("\"bytes\": "));
Check("transfer.import", db.Transfer.Import(Path.Combine(dir, "dump.jsonl")).Contains("\"records\": "));
Check("transfer.restore", db.Transfer.Restore(Path.Combine(dir, "bak")).Contains("\"ok\": true"));

// stats and the 0.1 aliases
Check("stats", db.Stats().Contains("\"kv_entries\""));
db.Put("pk", "pv");
Check("put alias", db.Get("pk").Contains("\"value\": \"pv\""));
Check("demo", db.Demo().Contains("database"));

Directory.Delete(dir, true);
Console.WriteLine(failures == 0 ? "surface smoke: ok" : $"surface smoke: {failures} failure(s)");
return failures == 0 ? 0 : 1;
