# frozen_string_literal: true

require "minitest/autorun"
require "tmpdir"
require_relative "../lib/b4n1-db"

# Every engine of the binary, exercised through the Ruby SDK.
class TestSurface < Minitest::Test
  def setup
    @dir = Dir.mktmpdir("b4n1-db-ruby-surface-")
    @db = B4n1Db::DB.new(File.join(@dir, "data"))
  end

  def teardown
    FileUtils.remove_entry(@dir) if File.directory?(@dir)
  end

  def test_kv
    assert_equal true, @db.kv.put("k", "v")["ok"]
    assert_equal({ "key" => "k", "value" => "v" }, @db.kv.get("k"))
    assert_equal 1, @db.kv.scan("")["count"]
    @db.kv.put_ttl("t", "x", 60)
    assert_operator @db.kv.ttl("t")["ttl_secs"], :>, 0
    assert_equal true, @db.kv.delete("k")["ok"]
    assert_equal 0, @db.kv.purge_expired["purged"]
    assert_raises(B4n1Db::Error) { @db.kv.get("k") }
  end

  def test_sql_survives_a_second_handle
    @db.sql.execute("CREATE TABLE u (id BIGINT, name VARCHAR)")
    @db.sql.execute("INSERT INTO u VALUES (1, 'ada')")
    assert_equal 1, @db.sql.query("SELECT * FROM u")["rows"].length
    assert_equal ["u"], @db.sql.tables["tables"]

    second = B4n1Db::DB.new(File.join(@dir, "data"))
    assert_equal [{ "name" => "ada" }], second.sql.query("SELECT name FROM u")["rows"]
    second.sql.drop("u")
    assert_equal 0, second.sql.tables["count"]
  end

  def test_sql_registers_a_file
    csv = File.join(@dir, "rows.csv")
    File.write(csv, "id\n7\n")
    @db.sql.register_csv("c1", csv)
    assert_equal [{ "id" => 7 }], @db.sql.query("SELECT id FROM c1")["rows"]
  end

  def test_graph
    node = @db.graph.add_node("pattern", "ada")
    other = @db.graph.add_node("pattern", "grace")
    assert_equal 36, node["id"].length
    refute_nil @db.graph.node(node["id"])["node"]
    assert_operator @db.graph.search("ada", 5)["count"], :>=, 1
    assert_equal "follows", @db.graph.add_relation(node["id"], other["id"], "follows", 0.5)["relation"]
    assert_equal true, @db.graph.forget(node["id"])["ok"]
  end

  def test_documents
    inserted = @db.doc.insert("users", { "name" => "ada", "age" => 36 })
    assert_equal "users", inserted["collection"]
    assert_equal inserted["id"], @db.doc.find_one("users", inserted["id"])["document"]["_id"]
    assert_equal 1, @db.doc.find("users")["count"]
    assert_equal true, @db.doc.update("users", inserted["id"], { "age" => 37 })["ok"]
    assert_equal 1, @db.doc.find_by_field("users", "name", "ada")["count"]
    assert_equal true, @db.doc.delete("users", inserted["id"])["ok"]
    assert_equal 0, @db.doc.find("users")["count"]
  end

  def test_queue
    id = @db.queue.push("jobs", "build")["id"]
    assert_equal 1, @db.queue.len("jobs")["pending"]
    assert_equal "build", @db.queue.peek("jobs")["body"]
    assert_equal id, @db.queue.pop("jobs")["id"]
    assert_equal true, @db.queue.nack("jobs", id)["requeued"]
    @db.queue.pop("jobs")
    assert_equal true, @db.queue.ack("jobs", id)["ok"]
    assert_equal true, @db.queue.clear("jobs")["ok"]
  end

  def test_vectors
    embedding = [1.0] + Array.new(1535, 0.0)
    id = @db.vector.insert("ns", embedding, { "tag" => "a" })["id"]
    hits = @db.vector.search("ns", embedding, 3, { "tag" => "a" })
    assert_equal id, hits["hits"][0]["id"]
    assert_equal 1, @db.vector.list("ns")["count"]
    assert_equal true, @db.vector.delete("ns", id)["ok"]
  end

  def test_audit
    @db.kv.put("a", "1")
    assert_equal true, @db.audit.verify["intact"]
    latest = @db.audit.latest("a")["node"]
    assert_equal 1, @db.audit.history("a")["nodes"].length
    assert_equal latest["hash"], @db.audit.time_travel("a", latest["timestamp"])["node"]["hash"]
    assert_equal 64, @db.audit.root_hash["root_hash"].length
  end

  def test_migrations_persist
    @db.migration.add(1, "init", "CREATE TABLE m (id BIGINT)")
    second = B4n1Db::DB.new(File.join(@dir, "data"))
    assert_equal 1, second.migration.list["registered"]
    assert_equal 1, second.migration.list["pending"][0]["version"]
    assert_equal [1], second.migration.exec["executed"]
    assert_equal [1], second.migration.rollback(0)["rolled_back"]
    assert_raises(B4n1Db::Error) { second.migration.add(1, "again", "SELECT 1") }
  end

  def test_transfer
    @db.kv.put("k", "v")
    backup = File.join(@dir, "bak")
    assert_operator @db.transfer.backup(backup)["files"], :>, 0
    dump = File.join(@dir, "dump.jsonl")
    assert_operator @db.transfer.export(dump)["bytes"], :>, 0
    assert_operator @db.transfer.import(dump)["records"], :>=, 1
    assert_equal true, @db.transfer.restore(backup)["ok"]
  end

  def test_stats_and_aliases
    @db.put("k", "v")
    assert_equal({ "key" => "k", "value" => "v" }, @db.get("k"))
    assert_equal 1, @db.stats["kv_entries"]
    assert_includes @db.demo, "database"
  end
end
