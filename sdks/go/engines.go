package b4n1db

import (
	"encoding/json"
	"strconv"
)

// Result is the answer of a verb: the JSON object the binary emits, decoded.
// Reads and mutations share it on purpose — the binary decides the fields and
// the wrapper never invents them.
type Result map[string]any

// call runs one verb with --json and decodes its answer.
func (db *DB) call(args ...string) (Result, error) {
	out, err := db.run(true, args...)
	if err != nil {
		return nil, err
	}
	var res Result
	if err := json.Unmarshal(out, &res); err != nil {
		return nil, err
	}
	return res, nil
}

// jsonArg serialises an argument for a JSON flag; empty when absent.
func jsonArg(value any) string {
	if value == nil {
		return ""
	}
	raw, err := json.Marshal(value)
	if err != nil {
		return ""
	}
	return string(raw)
}

// num renders an integer argument the way the binary parses it.
func num(value int64) string { return strconv.FormatInt(value, 10) }

// KV is the key-value engine: `b4n1-db kv <verb>`.
type KV struct{ db *DB }

// Put stores a value under a key.
func (e KV) Put(key, value string) (Result, error) {
	return e.db.call("kv", "put", key, value)
}

// Get reads a value back.
func (e KV) Get(key string) (Result, error) { return e.db.call("kv", "get", key) }

// Delete removes a key.
func (e KV) Delete(key string) (Result, error) { return e.db.call("kv", "delete", key) }

// Scan lists the pairs whose key starts with the prefix.
func (e KV) Scan(prefix string) (Result, error) { return e.db.call("kv", "scan", prefix) }

// PutTTL stores a value that expires after seconds.
func (e KV) PutTTL(key, value string, seconds int64) (Result, error) {
	return e.db.call("kv", "put-ttl", key, value, num(seconds))
}

// TTL reports the seconds left before a key expires.
func (e KV) TTL(key string) (Result, error) { return e.db.call("kv", "ttl", key) }

// PurgeExpired drops every expired key.
func (e KV) PurgeExpired() (Result, error) { return e.db.call("kv", "purge-expired") }

// SQL is the analytical engine: `b4n1-db sql <verb>`.
type SQL struct{ db *DB }

// Execute runs a statement and reports the rows affected.
func (e SQL) Execute(statement string) (Result, error) {
	return e.db.call("sql", "execute", statement)
}

// Query runs a query and returns its columns and rows.
func (e SQL) Query(statement string) (Result, error) {
	return e.db.call("sql", "query", statement)
}

// Tables lists the tables registered in the engine.
func (e SQL) Tables() (Result, error) { return e.db.call("sql", "tables") }

// RegisterCSV queries a CSV file as a table without copying it.
func (e SQL) RegisterCSV(table, file string) (Result, error) {
	return e.db.call("sql", "register-csv", table, file)
}

// RegisterJSON queries a JSON file as a table without copying it.
func (e SQL) RegisterJSON(table, file string) (Result, error) {
	return e.db.call("sql", "register-json", table, file)
}

// RegisterParquet queries a Parquet file as a table without copying it.
func (e SQL) RegisterParquet(table, file string) (Result, error) {
	return e.db.call("sql", "register-parquet", table, file)
}

// Drop removes a table and its persisted copy.
func (e SQL) Drop(table string) (Result, error) { return e.db.call("sql", "drop", table) }

// Graph is the knowledge graph engine: `b4n1-db graph <verb>`.
type Graph struct{ db *DB }

// NodeTypes are the types the engine accepts; anything else is refused.
var NodeTypes = []string{
	"instruction", "intention", "contract", "pattern", "scaffold", "qapair", "cyclerecord",
}

// AddNode adds a node and returns its id.
func (e Graph) AddNode(nodeType, content string) (Result, error) {
	return e.db.call("graph", "add-node", nodeType, content)
}

// AddRelation relates two existing nodes.
func (e Graph) AddRelation(from, to, relation string, weight *float64) (Result, error) {
	args := []string{"graph", "add-relation", from, to, relation}
	if weight != nil {
		args = append(args, strconv.FormatFloat(*weight, 'f', -1, 64))
	}
	return e.db.call(args...)
}

// Node fetches one node with its relations.
func (e Graph) Node(id string) (Result, error) { return e.db.call("graph", "node", id) }

// Search runs a hybrid search over content and adjacency.
func (e Graph) Search(query string, limit *int64) (Result, error) {
	args := []string{"graph", "search", query}
	if limit != nil {
		args = append(args, num(*limit))
	}
	return e.db.call(args...)
}

// Forget removes a node and cascades across its relations.
func (e Graph) Forget(id string) (Result, error) { return e.db.call("graph", "forget", id) }

// Doc is the document engine: `b4n1-db doc <verb>`.
type Doc struct{ db *DB }

// Insert adds a document from a map of fields and returns its id.
func (e Doc) Insert(collection string, payload map[string]any) (Result, error) {
	return e.db.call("doc", "insert", collection, jsonArg(payload))
}

// FindOne fetches one document by id.
func (e Doc) FindOne(collection, id string) (Result, error) {
	return e.db.call("doc", "find-one", collection, id)
}

// Find lists a collection.
func (e Doc) Find(collection string) (Result, error) { return e.db.call("doc", "find", collection) }

// FindByField queries by an indexed field, falling back to a full scan.
func (e Doc) FindByField(collection, field string, value any) (Result, error) {
	return e.db.call("doc", "find-by-field", collection, field, jsonArg(value))
}

// Update applies a partial update from a map of fields.
func (e Doc) Update(collection, id string, payload map[string]any) (Result, error) {
	return e.db.call("doc", "update", collection, id, jsonArg(payload))
}

// Delete removes a document.
func (e Doc) Delete(collection, id string) (Result, error) {
	return e.db.call("doc", "delete", collection, id)
}

// Queue is the FIFO engine: `b4n1-db queue <verb>`.
type Queue struct{ db *DB }

// Push appends a message and returns its id.
func (e Queue) Push(queue, body string) (Result, error) {
	return e.db.call("queue", "push", queue, body)
}

// Pop takes the oldest pending message out of the queue.
func (e Queue) Pop(queue string) (Result, error) { return e.db.call("queue", "pop", queue) }

// Peek looks at the oldest pending message without taking it.
func (e Queue) Peek(queue string) (Result, error) { return e.db.call("queue", "peek", queue) }

// Ack confirms a message so its parked payload is dropped.
func (e Queue) Ack(queue, id string) (Result, error) {
	return e.db.call("queue", "ack", queue, id)
}

// Nack requeues an in-flight message at its original position.
func (e Queue) Nack(queue, id string) (Result, error) {
	return e.db.call("queue", "nack", queue, id)
}

// Clear empties the queue.
func (e Queue) Clear(queue string) (Result, error) { return e.db.call("queue", "clear", queue) }

// Len counts pending messages.
func (e Queue) Len(queue string) (Result, error) { return e.db.call("queue", "len", queue) }

// Vector is the similarity engine: `b4n1-db vector <verb>`.
type Vector struct{ db *DB }

// Insert stores a vector with optional metadata.
func (e Vector) Insert(namespace string, vector []float64, metadata map[string]any) (Result, error) {
	return e.db.call("vector", "insert", namespace, jsonArg(vector), jsonArg(metadata))
}

// Search returns the nearest neighbours by cosine similarity.
func (e Vector) Search(namespace string, query []float64, topK *int64, filter map[string]any) (Result, error) {
	args := []string{"vector", "search", namespace, jsonArg(query)}
	if topK != nil || filter != nil {
		k := int64(5)
		if topK != nil {
			k = *topK
		}
		args = append(args, num(k))
	}
	if filter != nil {
		args = append(args, jsonArg(filter))
	}
	return e.db.call(args...)
}

// Delete removes a record.
func (e Vector) Delete(namespace, id string) (Result, error) {
	return e.db.call("vector", "delete", namespace, id)
}

// List returns the record ids in a namespace.
func (e Vector) List(namespace string) (Result, error) {
	return e.db.call("vector", "list", namespace)
}

// Audit is the Merkle-DAG engine: `b4n1-db audit <verb>`.
type Audit struct{ db *DB }

// Verify recomputes every node hash and fails at the first mismatch.
func (e Audit) Verify() (Result, error) { return e.db.call("audit", "verify") }

// History returns the full history of a key, oldest first.
func (e Audit) History(key string) (Result, error) { return e.db.call("audit", "history", key) }

// Latest returns the newest node for a key.
func (e Audit) Latest(key string) (Result, error) { return e.db.call("audit", "latest", key) }

// TimeTravel reads a key as of a past instant (epoch milliseconds).
func (e Audit) TimeTravel(key string, timestampMs int64) (Result, error) {
	return e.db.call("audit", "time-travel", key, num(timestampMs))
}

// RootHash folds every node hash into one root hash.
func (e Audit) RootHash() (Result, error) { return e.db.call("audit", "root-hash") }

// Migration is the schema registry: `b4n1-db migration <verb>`.
type Migration struct{ db *DB }

// List reports the current version, applied records and pending migrations.
func (e Migration) List() (Result, error) { return e.db.call("migration", "list") }

// Add registers a migration without applying it.
func (e Migration) Add(version int64, name, upSQL string) (Result, error) {
	return e.db.call("migration", "add", num(version), name, upSQL)
}

// Apply marks pending migrations applied WITHOUT running their DDL.
func (e Migration) Apply() (Result, error) { return e.db.call("migration", "apply") }

// Exec runs each pending migration's up_sql, rolling back the batch on failure.
func (e Migration) Exec() (Result, error) { return e.db.call("migration", "exec") }

// Rollback rolls the registry back to a target version.
func (e Migration) Rollback(target int64) (Result, error) {
	return e.db.call("migration", "rollback", num(target))
}

// Transfer is the backup engine: `b4n1-db transfer <verb>`.
type Transfer struct{ db *DB }

// Backup copies the whole database directory to a destination.
func (e Transfer) Backup(destination string) (Result, error) {
	return e.db.call("transfer", "backup", destination)
}

// Restore merges another database into this one, forward only.
func (e Transfer) Restore(source string) (Result, error) {
	return e.db.call("transfer", "restore", source)
}

// Export writes a portable JSON Lines dump.
func (e Transfer) Export(destination string) (Result, error) {
	return e.db.call("transfer", "export", destination)
}

// Import loads a JSON Lines dump produced by Export.
func (e Transfer) Import(source string) (Result, error) {
	return e.db.call("transfer", "import", source)
}
