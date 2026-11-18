export interface Stats {
  current_version: number;
  kv_entries: number;
  merkle_nodes: number;
}

export interface KvEntry {
  key: string;
  value: string;
}

export declare class DB {
  constructor(path?: string);
  path: string;
  stats(): Stats;
  kvPut(key: string, value: string): string;
  kvGet(key: string): KvEntry;
  demo(): string;
}

/** SDK version, always equal to the crate version (0.1.1). */
export declare const SDK_VERSION: string;
