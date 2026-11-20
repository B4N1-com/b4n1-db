package b4n1db

import (
	"os"
	"path/filepath"
	"testing"
)

// fresh returns a handle on a throwaway directory. The binary comes from
// B4N1_DB_BIN, which the suite points at the build under test.
func fresh(t *testing.T) (*DB, string) {
	t.Helper()
	dir := t.TempDir()
	return New(filepath.Join(dir, "data")), dir
}

// must fails the test when err is set.
func must(t *testing.T, err error) {
	t.Helper()
	if err != nil {
		t.Fatalf("call failed: %v", err)
	}
}

// mustFail fails the test when err is nil.
func mustFail(t *testing.T, err error) {
	t.Helper()
	if err == nil {
		t.Fatal("call should have failed")
	}
}

func TestKVSurface(t *testing.T) {
	db, _ := fresh(t)

	res, err := db.KV.Put("k", "v")
	must(t, err)
	if res["ok"] != true {
		t.Fatal("put did not ack")
	}

	entry, err := db.KV.Get("k")
	must(t, err)
	if entry["value"] != "v" {
		t.Fatalf("get = %v", entry)
	}

	scan, err := db.KV.Scan("")
	must(t, err)
	if scan["count"] != float64(1) {
		t.Fatalf("scan = %v", scan)
	}

	_, err = db.KV.PutTTL("t", "x", 60)
	must(t, err)
	ttl, err := db.KV.TTL("t")
	must(t, err)
	if ttl["ttl_secs"].(float64) <= 0 {
		t.Fatalf("ttl = %v", ttl)
	}

	_, err = db.KV.Delete("k")
	must(t, err)
	_, err = db.KV.PurgeExpired()
	must(t, err)
	_, err = db.KV.Get("k")
	mustFail(t, err)
}

func TestSQLSurface(t *testing.T) {
	db, dir := fresh(t)

	_, err := db.SQL.Execute("CREATE TABLE u (id BIGINT, name VARCHAR)")
	must(t, err)
	inserted, err := db.SQL.Execute("INSERT INTO u VALUES (1, 'ada')")
	must(t, err)
	if inserted["rows_affected"] != float64(1) {
		t.Fatalf("insert = %v", inserted)
	}

	query, err := db.SQL.Query("SELECT * FROM u")
	must(t, err)
	if rows := query["rows"].([]any); len(rows) != 1 {
		t.Fatalf("rows = %v", rows)
	}

	tables, err := db.SQL.Tables()
	must(t, err)
	if names := tables["tables"].([]any); len(names) != 1 {
		t.Fatalf("tables = %v", names)
	}

	// A second handle is a second process: the table must be on disk.
	second := New(filepath.Join(dir, "data"))
	reopened, err := second.SQL.Query("SELECT name FROM u")
	must(t, err)
	if rows := reopened["rows"].([]any); len(rows) != 1 {
		t.Fatal("table did not survive the reopen")
	}

	csv := filepath.Join(dir, "rows.csv")
	if err := os.WriteFile(csv, []byte("id\n7\n"), 0o644); err != nil {
		t.Fatal(err)
	}
	_, err = db.SQL.RegisterCSV("c1", csv)
	must(t, err)
	_, err = db.SQL.Query("SELECT id FROM c1")
	must(t, err)
	_, err = second.SQL.Drop("u")
	must(t, err)

	after, err := second.SQL.Tables()
	must(t, err)
	if names := after["tables"].([]any); len(names) != 1 {
		t.Fatalf("only the registered csv must remain, got %v", names)
	}
}

func TestGraphSurface(t *testing.T) {
	db, _ := fresh(t)

	node, err := db.Graph.AddNode("pattern", "ada")
	must(t, err)
	other, err := db.Graph.AddNode("pattern", "grace")
	must(t, err)

	id, _ := node["id"].(string)
	if len(id) != 36 {
		t.Fatalf("id = %v", node["id"])
	}

	fetched, err := db.Graph.Node(id)
	must(t, err)
	if fetched["node"] == nil {
		t.Fatal("node lookup returned nothing")
	}

	hits, err := db.Graph.Search("ada", nil)
	must(t, err)
	if hits["count"].(float64) < 1 {
		t.Fatalf("search = %v", hits)
	}

	weight := 0.5
	related, err := db.Graph.AddRelation(id, other["id"].(string), "follows", &weight)
	must(t, err)
	if related["relation"] != "follows" {
		t.Fatalf("relation = %v", related)
	}

	_, err = db.Graph.Forget(id)
	must(t, err)
	_, err = db.Graph.Node(id)
	mustFail(t, err)
}

func TestDocSurface(t *testing.T) {
	db, _ := fresh(t)

	inserted, err := db.Doc.Insert("users", map[string]any{"name": "ada", "age": 36})
	must(t, err)
	id := inserted["id"].(string)
	if inserted["collection"] != "users" {
		t.Fatalf("collection = %v", inserted["collection"])
	}

	found, err := db.Doc.FindOne("users", id)
	must(t, err)
	if found["document"] == nil {
		t.Fatal("find-one returned nothing")
	}

	list, err := db.Doc.Find("users")
	must(t, err)
	if list["count"] != float64(1) {
		t.Fatalf("find = %v", list)
	}

	_, err = db.Doc.Update("users", id, map[string]any{"age": 37})
	must(t, err)
	byField, err := db.Doc.FindByField("users", "name", "ada")
	must(t, err)
	if byField["count"] != float64(1) {
		t.Fatalf("find-by-field = %v", byField)
	}

	_, err = db.Doc.Delete("users", id)
	must(t, err)
	empty, err := db.Doc.Find("users")
	must(t, err)
	if empty["count"] != float64(0) {
		t.Fatal("document survived the delete")
	}
}

func TestQueueSurface(t *testing.T) {
	db, _ := fresh(t)

	pushed, err := db.Queue.Push("jobs", "build")
	must(t, err)
	id := pushed["id"].(string)

	length, err := db.Queue.Len("jobs")
	must(t, err)
	if length["pending"] != float64(1) {
		t.Fatalf("len = %v", length)
	}

	peeked, err := db.Queue.Peek("jobs")
	must(t, err)
	if peeked["body"] != "build" {
		t.Fatalf("peek = %v", peeked)
	}

	popped, err := db.Queue.Pop("jobs")
	must(t, err)
	if popped["id"] != id {
		t.Fatal("pop returned another message")
	}

	nacked, err := db.Queue.Nack("jobs", id)
	must(t, err)
	if nacked["requeued"] != true {
		t.Fatalf("nack = %v", nacked)
	}

	_, err = db.Queue.Pop("jobs")
	must(t, err)
	acked, err := db.Queue.Ack("jobs", id)
	must(t, err)
	if acked["ok"] != true {
		t.Fatalf("ack = %v", acked)
	}

	_, err = db.Queue.Clear("jobs")
	must(t, err)
}

func TestVectorSurface(t *testing.T) {
	db, _ := fresh(t)

	embedding := make([]float64, 1536)
	embedding[0] = 1.0
	record, err := db.Vector.Insert("ns", embedding, map[string]any{"tag": "a"})
	must(t, err)
	id := record["id"].(string)

	hits, err := db.Vector.Search("ns", embedding, nil, map[string]any{"tag": "a"})
	must(t, err)
	list := hits["hits"].([]any)
	if len(list) != 1 || list[0].(map[string]any)["id"] != id {
		t.Fatalf("hits = %v", list)
	}

	ids, err := db.Vector.List("ns")
	must(t, err)
	if ids["count"] != float64(1) {
		t.Fatalf("list = %v", ids)
	}

	_, err = db.Vector.Delete("ns", id)
	must(t, err)
}

func TestAuditSurface(t *testing.T) {
	db, _ := fresh(t)

	_, err := db.KV.Put("a", "1")
	must(t, err)

	verified, err := db.Audit.Verify()
	must(t, err)
	if verified["intact"] != true {
		t.Fatalf("verify = %v", verified)
	}

	latestResponse, err := db.Audit.Latest("a")
	must(t, err)
	latest := latestResponse["node"].(map[string]any)

	history, err := db.Audit.History("a")
	must(t, err)
	if history["count"] != float64(1) {
		t.Fatalf("history = %v", history)
	}

	stamp := int64(latest["timestamp"].(float64))
	pastResponse, err := db.Audit.TimeTravel("a", stamp)
	must(t, err)
	past := pastResponse["node"].(map[string]any)
	if past["hash"] != latest["hash"] {
		t.Fatal("time travel returned another version")
	}

	root, err := db.Audit.RootHash()
	must(t, err)
	if hash := root["root_hash"].(string); len(hash) != 64 {
		t.Fatalf("root hash = %v", hash)
	}
}

func TestMigrationSurface(t *testing.T) {
	db, dir := fresh(t)

	added, err := db.Migration.Add(1, "init", "CREATE TABLE m (id BIGINT)")
	must(t, err)
	if added["registered"] != true {
		t.Fatalf("add = %v", added)
	}

	second := New(filepath.Join(dir, "data"))
	listed, err := second.Migration.List()
	must(t, err)
	if listed["registered"] != float64(1) {
		t.Fatalf("the registry did not persist: %v", listed)
	}

	_, err = second.Migration.Exec()
	must(t, err)
	after, err := second.Migration.List()
	must(t, err)
	if after["current_version"] != float64(1) {
		t.Fatalf("exec did not advance the version: %v", after)
	}

	_, err = second.Migration.Apply()
	must(t, err)
	_, err = second.Migration.Rollback(0)
	must(t, err)
	_, err = second.Migration.Add(1, "again", "SELECT 1")
	mustFail(t, err)
}

func TestTransferSurface(t *testing.T) {
	db, dir := fresh(t)

	_, err := db.KV.Put("k", "v")
	must(t, err)

	backup, err := db.Transfer.Backup(filepath.Join(dir, "bak"))
	must(t, err)
	if backup["files"].(float64) < 1 {
		t.Fatalf("backup = %v", backup)
	}

	dump := filepath.Join(dir, "dump.jsonl")
	exported, err := db.Transfer.Export(dump)
	must(t, err)
	if exported["bytes"].(float64) < 1 {
		t.Fatalf("export = %v", exported)
	}

	imported, err := db.Transfer.Import(dump)
	must(t, err)
	if imported["records"].(float64) < 1 {
		t.Fatalf("import = %v", imported)
	}

	restored, err := db.Transfer.Restore(filepath.Join(dir, "bak"))
	must(t, err)
	if restored["ok"] != true {
		t.Fatalf("restore = %v", restored)
	}
}

func TestStatsAndAliases(t *testing.T) {
	db, _ := fresh(t)

	must(t, db.Put("pk", "pv"))
	entry, err := db.Get("pk")
	must(t, err)
	if entry.Value != "pv" {
		t.Fatalf("get alias = %v", entry)
	}

	stats, err := db.Stats()
	must(t, err)
	if stats.KvEntries != 1 {
		t.Fatalf("stats = %v", stats)
	}

	transcript, err := db.Demo()
	must(t, err)
	if transcript == "" {
		t.Fatal("demo returned nothing")
	}
}
