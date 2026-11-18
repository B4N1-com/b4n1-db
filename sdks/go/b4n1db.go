// Package b4n1db is a thin Go wrapper over the `b4n1-db` standalone binary.
// All storage, SQL, graph, vector and audit logic lives in the binary; this
// package only invokes it.
package b4n1db

import (
	"encoding/json"
	"errors"
	"fmt"
	"os"
	"os/exec"
)

// Version is the SDK version and tracks the crate version.
const Version = "0.1.1"

// Stats mirrors the JSON emitted by `b4n1-db --json stats`.
type Stats struct {
	CurrentVersion int `json:"current_version"`
	KvEntries      int `json:"kv_entries"`
	MerkleNodes    int `json:"merkle_nodes"`
}

// Entry mirrors the JSON emitted by `b4n1-db --json kv get`.
type Entry struct {
	Key   string `json:"key"`
	Value string `json:"value"`
}

// DB is a handle to a database directory.
type DB struct {
	Path string
}

// New opens (creates on demand) the database at path.
func New(path string) *DB {
	if path == "" {
		path = "./data"
	}
	return &DB{Path: path}
}

// Binary resolves the b4n1-db binary: B4N1_DB_BIN, then PATH.
func Binary() (string, error) {
	if env := os.Getenv("B4N1_DB_BIN"); env != "" {
		if _, err := os.Stat(env); err == nil {
			return env, nil
		}
	}
	if found, err := exec.LookPath("b4n1-db"); err == nil {
		return found, nil
	}
	return "", errors.New("b4n1-db binary not found; set B4N1_DB_BIN or install the b4n1-db binary")
}

func (db *DB) run(jsonOut bool, args ...string) ([]byte, error) {
	bin, err := Binary()
	if err != nil {
		return nil, err
	}
	full := []string{"--db", db.Path}
	if jsonOut {
		full = append(full, "--json")
	}
	full = append(full, args...)
	out, err := exec.Command(bin, full...).Output()
	if err != nil {
		var exitErr *exec.ExitError
		if errors.As(err, &exitErr) && len(exitErr.Stderr) > 0 {
			return nil, fmt.Errorf("%s", string(exitErr.Stderr))
		}
		return nil, err
	}
	return out, nil
}

// Stats returns the database counters.
func (db *DB) Stats() (Stats, error) {
	out, err := db.run(true, "stats")
	if err != nil {
		return Stats{}, err
	}
	var s Stats
	if err := json.Unmarshal(out, &s); err != nil {
		return Stats{}, err
	}
	return s, nil
}

// Put writes a key/value pair.
func (db *DB) Put(key, value string) error {
	_, err := db.run(false, "kv", "put", key, value)
	return err
}

// Get reads a key.
func (db *DB) Get(key string) (Entry, error) {
	out, err := db.run(true, "kv", "get", key)
	if err != nil {
		return Entry{}, err
	}
	var e Entry
	if err := json.Unmarshal(out, &e); err != nil {
		return Entry{}, err
	}
	return e, nil
}

// Demo runs the offline one-click demonstration and returns its transcript.
func (db *DB) Demo() (string, error) {
	out, err := db.run(false, "demo")
	if err != nil {
		return "", err
	}
	return string(out), nil
}
