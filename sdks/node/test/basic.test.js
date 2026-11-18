"use strict";

const { test } = require("node:test");
const assert = require("node:assert");
const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");

const { DB, SDK_VERSION } = require("../index.js");

function tmpDb() {
  return fs.mkdtempSync(path.join(os.tmpdir(), "b4n1-db-node-"));
}

test("SDK_VERSION is semver", () => {
  assert.match(SDK_VERSION, /^\d+\.\d+\.\d+$/);
});

test("put then get round-trips", () => {
  const db = new DB(tmpDb());
  db.kvPut("alpha", "1");
  assert.deepStrictEqual(db.kvGet("alpha"), { key: "alpha", value: "1" });
});

test("stats reports the written key", () => {
  const db = new DB(tmpDb());
  db.kvPut("beta", "2");
  const stats = db.stats();
  assert.strictEqual(stats.kv_entries, 1);
});

test("missing key fails loud", () => {
  const db = new DB(tmpDb());
  assert.throws(() => db.kvGet("nope"));
});
