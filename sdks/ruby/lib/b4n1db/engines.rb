# frozen_string_literal: true

# One class per engine of the `b4n1-db` binary. Every method forwards to the
# binary and parses its `--json` answer; the contract is `sdk-surface.tsv` in
# the crate root and the suite fails when a wrapper drifts from it.
module B4n1Db
  # Shared plumbing: argument marshalling only.
  class Engine
    def initialize(db)
      @db = db
    end

    def call(*args)
      JSON.parse(@db.invoke(*args))
    end

    # Serialise an object/array for a flag that takes JSON; nil stays empty.
    def json(value)
      return "" if value.nil?

      JSON.generate(value)
    end

    # Encode a scalar as a JSON value: `find-by-field` parses its argument.
    def json_value(value)
      JSON.generate(value)
    end
  end

  class Kv < Engine
    def put(key, value) = call("kv", "put", key, value.to_s)
    def get(key) = call("kv", "get", key)
    def delete(key) = call("kv", "delete", key)
    def scan(prefix) = call("kv", "scan", prefix)
    def put_ttl(key, value, seconds) = call("kv", "put-ttl", key, value.to_s, seconds.to_i.to_s)
    def ttl(key) = call("kv", "ttl", key)
    def purge_expired = call("kv", "purge-expired")
  end

  class Sql < Engine
    def execute(statement) = call("sql", "execute", statement)
    def query(statement) = call("sql", "query", statement)
    def tables = call("sql", "tables")
    def register_csv(table, path) = call("sql", "register-csv", table, path.to_s)
    def register_json(table, path) = call("sql", "register-json", table, path.to_s)
    def register_parquet(table, path) = call("sql", "register-parquet", table, path.to_s)
    def drop(table) = call("sql", "drop", table)
  end

  class Graph < Engine
    # Node types the engine accepts; anything else is refused.
    NODE_TYPES = %w[instruction intention contract pattern scaffold qapair cyclerecord].freeze

    def add_node(type, content) = call("graph", "add-node", type, content)
    def add_relation(from, to, relation, weight = nil)
      args = ["graph", "add-relation", from, to, relation]
      args << weight.to_f.to_s unless weight.nil?
      call(*args)
    end

    def node(id) = call("graph", "node", id)

    def search(query, limit = nil)
      limit.nil? ? call("graph", "search", query) : call("graph", "search", query, limit.to_i.to_s)
    end

    def forget(id) = call("graph", "forget", id)
  end

  class Doc < Engine
    def insert(collection, payload) = call("doc", "insert", collection, json(payload))
    def find_one(collection, id) = call("doc", "find-one", collection, id)
    def find(collection) = call("doc", "find", collection)
    def find_by_field(collection, field, value)
      call("doc", "find-by-field", collection, field, json_value(value))
    end

    def update(collection, id, payload) = call("doc", "update", collection, id, json(payload))
    def delete(collection, id) = call("doc", "delete", collection, id)
  end

  class Queue < Engine
    def push(queue, body) = call("queue", "push", queue, body)
    def pop(queue) = call("queue", "pop", queue)
    def peek(queue) = call("queue", "peek", queue)
    def ack(queue, id) = call("queue", "ack", queue, id)
    def nack(queue, id) = call("queue", "nack", queue, id)
    def clear(queue) = call("queue", "clear", queue)
    def len(queue) = call("queue", "len", queue)
  end

  class Vector < Engine
    def insert(namespace, vector, metadata = nil)
      call("vector", "insert", namespace, json(vector), json(metadata))
    end

    def search(namespace, query, top_k = nil, filter = nil)
      args = ["vector", "search", namespace, json(query)]
      args << (top_k.nil? ? "5" : top_k.to_i.to_s) unless top_k.nil? && filter.nil?
      args << json(filter) unless filter.nil?
      call(*args)
    end

    def delete(namespace, id) = call("vector", "delete", namespace, id)
    def list(namespace) = call("vector", "list", namespace)
  end

  class Audit < Engine
    def verify = call("audit", "verify")
    def history(key) = call("audit", "history", key)
    def latest(key) = call("audit", "latest", key)
    def time_travel(key, timestamp_ms) = call("audit", "time-travel", key, timestamp_ms.to_i.to_s)
    def root_hash = call("audit", "root-hash")
  end

  class Migration < Engine
    def list = call("migration", "list")
    def add(version, name, up_sql) = call("migration", "add", version.to_i.to_s, name, up_sql)
    def apply = call("migration", "apply")
    def exec = call("migration", "exec")
    def rollback(target) = call("migration", "rollback", target.to_i.to_s)
  end

  class Transfer < Engine
    def backup(destination) = call("transfer", "backup", destination.to_s)
    def restore(source) = call("transfer", "restore", source.to_s)
    def export(destination) = call("transfer", "export", destination.to_s)
    def import(source) = call("transfer", "import", source.to_s)
  end
end
