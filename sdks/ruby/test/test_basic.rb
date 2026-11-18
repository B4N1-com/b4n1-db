# frozen_string_literal: true

require "minitest/autorun"
require "tmpdir"
require_relative "../lib/b4n1-db"

class TestB4n1Db < Minitest::Test
  def setup
    @db = B4n1Db::DB.new(Dir.mktmpdir("b4n1-db-ruby-"))
  end

  def test_version_is_semver
    assert_match(/\A\d+\.\d+\.\d+\z/, B4n1Db::VERSION)
  end

  def test_put_get_round_trip
    @db.put("alpha", "1")
    assert_equal({ "key" => "alpha", "value" => "1" }, @db.get("alpha"))
  end

  def test_stats_counts_keys
    @db.put("beta", "2")
    assert_equal 1, @db.stats["kv_entries"]
  end

  def test_missing_key_fails_loud
    assert_raises(B4n1Db::Error) { @db.get("nope") }
  end
end
