/**
 * Every engine of the binary, exercised through the Node SDK.
 *
 * The binary comes from B4N1_DB_BIN (the suite sets it), so this runs against
 * the build under test rather than a stale bundled copy.
 */

"use strict";

const test = require("node:test");
const assert = require("node:assert");
const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");

const { DB } = require("../index.js");

function fresh() {
  const dir = fs.mkdtempSync(path.join(os.tmpdir(), "b4n1-db-node-"));
  return { dir, db: new DB(path.join(dir, "data")) };
}

test("kv engine", () => {
  const { db } = fresh();
  assert.strictEqual(db.kv.put("k", "v").ok, true);
  assert.deepStrictEqual(db.kv.get("k"), { key: "k", value: "v" });
  assert.strictEqual(db.kv.scan("").count, 1);
  db.kv.putTtl("t", "x", 60);
  assert.ok(db.kv.ttl("t").ttl_secs > 0);
  assert.strictEqual(db.kv.delete("k").ok, true);
  assert.strictEqual(db.kv.purgeExpired().purged, 0);
  assert.throws(() => db.kv.get("k"));
});

test("sql engine keeps tables across handles", () => {
  const { dir, db } = fresh();
  db.sql.execute("CREATE TABLE u (id BIGINT, name VARCHAR)");
  db.sql.execute("INSERT INTO u VALUES (1, 'ada')");
  assert.strictEqual(db.sql.query("SELECT * FROM u").rows.length, 1);
  assert.deepStrictEqual(db.sql.tables().tables, ["u"]);

  const second = new DB(path.join(dir, "data"));
  assert.deepStrictEqual(second.sql.query("SELECT name FROM u").rows, [{ name: "ada" }]);
  second.sql.drop("u");
  assert.strictEqual(second.sql.tables().count, 0);
});

test("sql registers a file as a table", () => {
  const { dir, db } = fresh();
  const csv = path.join(dir, "rows.csv");
  fs.writeFileSync(csv, "id\n7\n");
  db.sql.registerCsv("c1", csv);
  assert.deepStrictEqual(db.sql.query("SELECT id FROM c1").rows, [{ id: 7 }]);
});

test("graph engine", () => {
  const { db } = fresh();
  const node = db.graph.addNode("pattern", "ada");
  const other = db.graph.addNode("pattern", "grace");
  assert.strictEqual(node.id.length, 36);
  assert.ok(db.graph.node(node.id).node);
  assert.ok(db.graph.search("ada", 5).count >= 1);
  assert.strictEqual(db.graph.addRelation(node.id, other.id, "follows", 0.5).relation, "follows");
  assert.strictEqual(db.graph.forget(node.id).ok, true);
});

test("document engine", () => {
  const { db } = fresh();
  const inserted = db.doc.insert("users", { name: "ada", age: 36 });
  assert.strictEqual(inserted.collection, "users");
  assert.strictEqual(db.doc.findOne("users", inserted.id).document._id, inserted.id);
  assert.strictEqual(db.doc.find("users").count, 1);
  assert.strictEqual(db.doc.update("users", inserted.id, { age: 37 }).ok, true);
  assert.strictEqual(db.doc.findByField("users", "name", "ada").count, 1);
  assert.strictEqual(db.doc.delete("users", inserted.id).ok, true);
  assert.strictEqual(db.doc.find("users").count, 0);
});

test("queue engine", () => {
  const { db } = fresh();
  const id = db.queue.push("jobs", "build").id;
  assert.strictEqual(db.queue.len("jobs").pending, 1);
  assert.strictEqual(db.queue.peek("jobs").body, "build");
  assert.strictEqual(db.queue.pop("jobs").id, id);
  assert.strictEqual(db.queue.nack("jobs", id).requeued, true);
  db.queue.pop("jobs");
  assert.strictEqual(db.queue.ack("jobs", id).ok, true);
  assert.strictEqual(db.queue.clear("jobs").ok, true);
});

test("vector engine", () => {
  const { db } = fresh();
  const embedding = [1, ...new Array(1535).fill(0)];
  const id = db.vector.insert("ns", embedding, { tag: "a" }).id;
  const hits = db.vector.search("ns", embedding, 3, { tag: "a" });
  assert.strictEqual(hits.hits[0].id, id);
  assert.strictEqual(db.vector.list("ns").count, 1);
  assert.strictEqual(db.vector.delete("ns", id).ok, true);
});

test("audit engine", () => {
  const { db } = fresh();
  db.kv.put("a", "1");
  assert.strictEqual(db.audit.verify().intact, true);
  const latest = db.audit.latest("a").node;
  assert.strictEqual(db.audit.history("a").nodes.length, 1);
  assert.strictEqual(db.audit.timeTravel("a", latest.timestamp).node.hash, latest.hash);
  assert.strictEqual(db.audit.rootHash().root_hash.length, 64);
});

test("migration engine persists the registry", () => {
  const { dir, db } = fresh();
  db.migration.add(1, "init", "CREATE TABLE m (id BIGINT)");
  const second = new DB(path.join(dir, "data"));
  assert.strictEqual(second.migration.list().registered, 1);
  assert.strictEqual(second.migration.list().pending[0].version, 1);
  assert.deepStrictEqual(second.migration.exec().executed, [1]);
  assert.deepStrictEqual(second.migration.rollback(0).rolled_back, [1]);
  assert.throws(() => second.migration.add(1, "again", "SELECT 1"));
});

test("transfer engine", () => {
  const { dir, db } = fresh();
  db.kv.put("k", "v");
  const backup = path.join(dir, "bak");
  assert.ok(db.transfer.backup(backup).files > 0);
  const dump = path.join(dir, "dump.jsonl");
  assert.ok(db.transfer.export(dump).bytes > 0);
  assert.ok(db.transfer.import(dump).records >= 1);
  assert.strictEqual(db.transfer.restore(backup).ok, true);
});
