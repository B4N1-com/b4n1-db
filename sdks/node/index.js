/**
 * b4n1-db SDK for Node.js — thin wrapper over the `b4n1-db` binary (§9/§10m).
 *
 * The heavy lifting lives in the Rust binary; this module only shells out to
 * it, so consumers never need a Rust toolchain.
 */

"use strict";

const { execFileSync } = require("node:child_process");
const fs = require("node:fs");
const path = require("node:path");

const { attachEngines } = require("./engines");

const SDK_VERSION = "0.1.2";

const PLATFORM_BINARIES = {
  "linux-x64": "b4n1-db-linux-amd64",
  "linux-arm64": "b4n1-db-linux-arm64",
  "darwin-x64": "b4n1-db-macos-x64",
  "darwin-arm64": "b4n1-db-macos-arm64",
  "win32-x64": "b4n1-db-windows-amd64.exe",
};

function bundledBinary() {
  const name = PLATFORM_BINARIES[`${process.platform}-${process.arch}`];
  if (!name) return null;
  const candidate = path.join(__dirname, "bin", name);
  return fs.existsSync(candidate) ? candidate : null;
}

function findBinary() {
  const fromEnv = process.env.B4N1_DB_BIN;
  if (fromEnv && fs.existsSync(fromEnv)) return fromEnv;
  const bundled = bundledBinary();
  if (bundled) return bundled;
  const onPath = process.env.PATH.split(path.delimiter)
    .map((dir) => path.join(dir, "b4n1-db"))
    .find((candidate) => fs.existsSync(candidate));
  if (onPath) return onPath;
  throw new Error(
    "b4n1-db binary not found; set B4N1_DB_BIN or install the b4n1-db binary",
  );
}

class DB {
  constructor(dbPath = "./data") {
    this.path = dbPath;
    Object.assign(this, attachEngines(this));
  }

  _run(args, jsonOut = true) {
    const cmd = [findBinary(), "--db", this.path];
    if (jsonOut) cmd.push("--json");
    cmd.push(...args);
    const stdout = execFileSync(cmd[0], cmd.slice(1), {
      encoding: "utf8",
      stdio: ["ignore", "pipe", "pipe"],
    });
    if (jsonOut && stdout.trim()) return JSON.parse(stdout);
    return stdout;
  }

  stats() {
    return this._run(["stats"]);
  }

  /** Alias of `db.kv.put(...)`, kept from 0.1. */
  kvPut(key, value) {
    return this.kv.put(key, value);
  }

  /** Alias of `db.kv.get(...)`, kept from 0.1. */
  kvGet(key) {
    return this.kv.get(key);
  }

  demo() {
    return this._run(["demo"], false);
  }
}

module.exports = { DB, SDK_VERSION };
