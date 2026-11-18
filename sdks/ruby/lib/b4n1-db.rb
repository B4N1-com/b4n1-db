# frozen_string_literal: true

# b4n1-db Ruby SDK — thin wrapper over the `b4n1-db` binary.
# All storage, SQL, graph, vector and audit logic lives in the binary; this
# library only invokes it.
require "json"
require "open3"

module B4n1Db
  VERSION = "0.1.1"

  class Error < StandardError; end
  class BinaryNotFound < Error; end

  # Platform → file name inside the gem's bin/ dir. Must match the names
  # scripts/bundle-sdk-binaries.sh writes (one list, four readers).
  BUNDLED = {
    ["x86_64-linux", "x86_64-linux-gnu", "x86_64-linux-musl"] => "b4n1-db-linux-amd64",
    ["aarch64-linux", "aarch64-linux-gnu", "aarch64-linux-musl"] => "b4n1-db-linux-arm64",
    ["x86_64-darwin", "x86_64-darwin20"] => "b4n1-db-macos-x64",
    ["arm64-darwin", "arm64-darwin23"] => "b4n1-db-macos-arm64",
    ["x64-mingw-ucrt", "x64-mingw32"] => "b4n1-db-windows-amd64.exe"
  }.each_with_object({}) { |(plats, name), acc| plats.each { |p| acc[p] = name } }

  # Resolves the binary: B4N1_DB_BIN, then the copy bundled in this gem, then
  # PATH. The bundled copy is what lets `gem install b4n1-db` work with no
  # extra download.
  def self.binary
    env = ENV["B4N1_DB_BIN"]
    return env if env && File.file?(env)

    name = BUNDLED[RUBY_PLATFORM]
    if name
      bundled = File.expand_path(File.join("..", "bin", name), __dir__)
      return bundled if File.file?(bundled)
    end

    found = ENV["PATH"].to_s.split(File::PATH_SEPARATOR).map { |d| File.join(d, "b4n1-db") }
                 .find { |c| File.file?(c) }
    return found if found

    raise BinaryNotFound, "b4n1-db binary not found; set B4N1_DB_BIN or install the b4n1-db binary"
  end

  # Handle to a database directory.
  class DB
    attr_reader :path

    def initialize(path = "./data")
      @path = path
    end

    def stats
      JSON.parse(run(true, "stats"))
    end

    def put(key, value)
      run(false, "kv", "put", key, value.to_s)
      true
    end

    def get(key)
      JSON.parse(run(true, "kv", "get", key))
    end

    def demo
      run(false, "demo")
    end

    private

    def run(json_out, *args)
      cmd = [B4n1Db.binary, "--db", @path]
      cmd << "--json" if json_out
      cmd.concat(args)
      out, err, status = Open3.capture3(*cmd)
      raise Error, (err.strip.empty? ? out.strip : err.strip) unless status.success?

      out
    end
  end
end
