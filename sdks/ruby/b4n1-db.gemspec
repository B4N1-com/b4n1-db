# frozen_string_literal: true

require_relative "lib/b4n1-db"

Gem::Specification.new do |spec|
  spec.name          = "b4n1-db"
  # Version mirror for the release bump: 0.1.4 (source of truth: B4n1Db::VERSION).
  spec.version       = B4n1Db::VERSION
  spec.authors       = ["Bani Montoya"]
  spec.summary       = "b4n1-db SDK — sovereign embedded database over the b4n1-db binary"
  spec.description   = "Thin Ruby wrapper over the b4n1-db standalone binary: KV, SQL, " \
                       "Merkle audit DAG, documents, graph, queues and vectors."
  spec.homepage      = "https://b4n1.com"
  spec.license       = "BUSL-1.1"
  spec.required_ruby_version = ">= 3.0"

  # bin/ carries the per-platform engine binary (§10m). Without it the gem
  # installs a wrapper that fails on the first call.
  spec.files         = Dir["lib/**/*.rb"] + Dir["bin/*"] + %w[README.md LICENSE EULA.txt]
  spec.require_paths = ["lib"]
end
