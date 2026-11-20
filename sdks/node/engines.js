/**
 * The nine engines of the `b4n1-db` binary, one group each.
 *
 * Every method is argument marshalling and nothing else: the binary owns the
 * behaviour, so a fix in Rust fixes all seven SDKs. The contract is
 * `sdk-surface.tsv` in the crate root, and the suite fails when a wrapper
 * drifts from it.
 */

"use strict";

/** Send an object/array as JSON; leave absent values empty. */
const json = (value) => (value === undefined || value === null ? "" : JSON.stringify(value));

/** Encode one scalar as a JSON value (`"ada"`, `36`) — `find-by-field` parses it. */
const jsonValue = (value) => JSON.stringify(value);

const kv = (db) => ({
  put: (key, value) => db._run(["kv", "put", key, String(value)]),
  get: (key) => db._run(["kv", "get", key]),
  delete: (key) => db._run(["kv", "delete", key]),
  scan: (prefix) => db._run(["kv", "scan", prefix]),
  putTtl: (key, value, seconds) =>
    db._run(["kv", "put-ttl", key, String(value), String(Math.trunc(seconds))]),
  ttl: (key) => db._run(["kv", "ttl", key]),
  purgeExpired: () => db._run(["kv", "purge-expired"]),
});

const sql = (db) => ({
  execute: (statement) => db._run(["sql", "execute", statement]),
  query: (statement) => db._run(["sql", "query", statement]),
  tables: () => db._run(["sql", "tables"]),
  registerCsv: (table, file) => db._run(["sql", "register-csv", table, String(file)]),
  registerJson: (table, file) => db._run(["sql", "register-json", table, String(file)]),
  registerParquet: (table, file) =>
    db._run(["sql", "register-parquet", table, String(file)]),
  drop: (table) => db._run(["sql", "drop", table]),
});

const graph = (db) => ({
  /** Node types the engine accepts; anything else is refused. */
  NODE_TYPES: [
    "instruction",
    "intention",
    "contract",
    "pattern",
    "scaffold",
    "qapair",
    "cyclerecord",
  ],
  addNode: (type, content) => db._run(["graph", "add-node", type, content]),
  addRelation: (from, to, relation, weight) =>
    db._run(
      weight === undefined
        ? ["graph", "add-relation", from, to, relation]
        : ["graph", "add-relation", from, to, relation, String(Number(weight))],
    ),
  node: (id) => db._run(["graph", "node", id]),
  search: (query, limit) =>
    db._run(
      limit === undefined
        ? ["graph", "search", query]
        : ["graph", "search", query, String(Math.trunc(limit))],
    ),
  forget: (id) => db._run(["graph", "forget", id]),
});

const documents = (db) => ({
  insert: (collection, payload) => db._run(["doc", "insert", collection, json(payload)]),
  findOne: (collection, id) => db._run(["doc", "find-one", collection, id]),
  find: (collection) => db._run(["doc", "find", collection]),
  findByField: (collection, field, value) =>
    db._run(["doc", "find-by-field", collection, field, jsonValue(value)]),
  update: (collection, id, payload) =>
    db._run(["doc", "update", collection, id, json(payload)]),
  delete: (collection, id) => db._run(["doc", "delete", collection, id]),
});

const queues = (db) => ({
  push: (queue, body) => db._run(["queue", "push", queue, body]),
  pop: (queue) => db._run(["queue", "pop", queue]),
  peek: (queue) => db._run(["queue", "peek", queue]),
  ack: (queue, id) => db._run(["queue", "ack", queue, id]),
  nack: (queue, id) => db._run(["queue", "nack", queue, id]),
  clear: (queue) => db._run(["queue", "clear", queue]),
  len: (queue) => db._run(["queue", "len", queue]),
});

const vectors = (db) => ({
  insert: (namespace, vector, metadata) =>
    db._run(["vector", "insert", namespace, json(vector), json(metadata)]),
  search: (namespace, query, topK, filter) => {
    const args = ["vector", "search", namespace, json(query)];
    if (topK !== undefined || filter !== undefined) {
      args.push(topK === undefined ? "5" : String(Math.trunc(topK)));
    }
    if (filter !== undefined) args.push(json(filter));
    return db._run(args);
  },
  delete: (namespace, id) => db._run(["vector", "delete", namespace, id]),
  list: (namespace) => db._run(["vector", "list", namespace]),
});

const audit = (db) => ({
  verify: () => db._run(["audit", "verify"]),
  history: (key) => db._run(["audit", "history", key]),
  latest: (key) => db._run(["audit", "latest", key]),
  timeTravel: (key, timestampMs) =>
    db._run(["audit", "time-travel", key, String(Math.trunc(timestampMs))]),
  rootHash: () => db._run(["audit", "root-hash"]),
});

const migration = (db) => ({
  list: () => db._run(["migration", "list"]),
  add: (version, name, upSql) =>
    db._run(["migration", "add", String(Math.trunc(version)), name, upSql]),
  apply: () => db._run(["migration", "apply"]),
  exec: () => db._run(["migration", "exec"]),
  rollback: (target) => db._run(["migration", "rollback", String(Math.trunc(target))]),
});

const transfer = (db) => ({
  backup: (destination) => db._run(["transfer", "backup", String(destination)]),
  restore: (source) => db._run(["transfer", "restore", String(source)]),
  export: (destination) => db._run(["transfer", "export", String(destination)]),
  import: (source) => db._run(["transfer", "import", String(source)]),
});

/** Build the nine groups for one handle. */
function attachEngines(db) {
  return {
    kv: kv(db),
    sql: sql(db),
    graph: graph(db),
    doc: documents(db),
    queue: queues(db),
    vector: vectors(db),
    audit: audit(db),
    migration: migration(db),
    transfer: transfer(db),
  };
}

module.exports = { attachEngines };
