export interface Stats {
  current_version: number;
  kv_entries: number;
  merkle_nodes: number;
}

export interface KvEntry {
  key: string;
  value: string;
}

/** `--json` answers of a mutation carry `ok: true` plus the facts. */
export interface Ok {
  ok: true;
  [key: string]: unknown;
}

export interface SqlResult {
  columns: string[];
  rows: Record<string, unknown>[];
}

export interface KvEngine {
  put(key: string, value: string): Ok;
  get(key: string): KvEntry;
  delete(key: string): Ok;
  scan(prefix: string): { prefix: string; count: number; rows: KvEntry[] };
  putTtl(key: string, value: string, seconds: number): Ok;
  ttl(key: string): { key: string; ttl_secs: number };
  purgeExpired(): Ok & { purged: number };
}

export interface SqlEngine {
  execute(statement: string): Ok & { rows_affected: number };
  query(statement: string): SqlResult;
  tables(): { count: number; tables: string[] };
  registerCsv(table: string, file: string): Ok;
  registerJson(table: string, file: string): Ok;
  registerParquet(table: string, file: string): Ok;
  drop(table: string): Ok;
}

export type NodeType =
  | "instruction"
  | "intention"
  | "contract"
  | "pattern"
  | "scaffold"
  | "qapair"
  | "cyclerecord";

export interface GraphEngine {
  NODE_TYPES: NodeType[];
  addNode(type: NodeType, content: string): Ok & { id: string };
  addRelation(from: string, to: string, relation: string, weight?: number): Ok;
  node(id: string): { node: unknown };
  search(query: string, limit?: number): { count: number; rows: unknown[] };
  forget(id: string): Ok;
}

export interface DocEngine {
  insert(collection: string, payload: Record<string, unknown>): Ok & { id: string };
  findOne(collection: string, id: string): { document: Record<string, unknown> };
  find(collection: string): { count: number; documents: Record<string, unknown>[] };
  findByField(collection: string, field: string, value: unknown): {
    count: number;
    documents: Record<string, unknown>[];
  };
  update(collection: string, id: string, payload: Record<string, unknown>): Ok;
  delete(collection: string, id: string): Ok;
}

export interface Message {
  id: string;
  timestamp: number;
  body: string;
}

export interface QueueEngine {
  push(queue: string, body: string): Ok & { id: string };
  pop(queue: string): Message;
  peek(queue: string): Message;
  ack(queue: string, id: string): Ok;
  nack(queue: string, id: string): Ok;
  clear(queue: string): Ok & { cleared: number };
  len(queue: string): { queue: string; pending: number };
}

export interface VectorEngine {
  insert(namespace: string, vector: number[], metadata?: Record<string, unknown>): Ok & { id: string };
  search(
    namespace: string,
    query: number[],
    topK?: number,
    filter?: Record<string, unknown>,
  ): { count: number; hits: { id: string; score: number; metadata: unknown }[] };
  delete(namespace: string, id: string): Ok;
  list(namespace: string): { count: number; ids: string[] };
}

export interface AuditEngine {
  verify(): Ok & { nodes: number; intact: boolean };
  history(key: string): { key: string; count: number; nodes: unknown[] };
  latest(key: string): { key: string; node: { hash: string; timestamp: number } };
  timeTravel(key: string, timestampMs: number): { key: string; node: unknown };
  rootHash(): { root_hash: string };
}

export interface MigrationEngine {
  list(): {
    current_version: number;
    registered: number;
    applied: unknown[];
    pending: { version: number; name: string }[];
  };
  add(version: number, name: string, upSql: string): Ok & { version: number };
  apply(): Ok & { applied: number[]; ddl_executed: boolean };
  exec(): Ok & { executed: number[]; ddl_executed: boolean };
  rollback(target: number): Ok & { rolled_back: number[] };
}

export interface TransferEngine {
  backup(destination: string): Ok & { files: number; bytes: number };
  restore(source: string): Ok;
  export(destination: string): Ok & { bytes: number };
  import(source: string): Ok & { records: number };
}

export declare class DB {
  constructor(path?: string);
  path: string;
  kv: KvEngine;
  sql: SqlEngine;
  graph: GraphEngine;
  doc: DocEngine;
  queue: QueueEngine;
  vector: VectorEngine;
  audit: AuditEngine;
  migration: MigrationEngine;
  transfer: TransferEngine;
  stats(): Stats;
  kvPut(key: string, value: string): Ok;
  kvGet(key: string): KvEntry;
  demo(): string;
}

/** SDK version, always equal to the crate version (0.1.4). */
export declare const SDK_VERSION: string;
