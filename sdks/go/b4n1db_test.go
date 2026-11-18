package b4n1db

import (
	"strings"
	"testing"
)

func tempDB(t *testing.T) *DB {
	t.Helper()
	return New(t.TempDir())
}

func TestVersionIsSemver(t *testing.T) {
	if parts := strings.Split(Version, "."); len(parts) != 3 {
		t.Fatalf("unexpected version %q", Version)
	}
}

func TestPutGetRoundTrip(t *testing.T) {
	db := tempDB(t)
	if err := db.Put("alpha", "1"); err != nil {
		t.Fatalf("put: %v", err)
	}
	entry, err := db.Get("alpha")
	if err != nil {
		t.Fatalf("get: %v", err)
	}
	if entry.Key != "alpha" || entry.Value != "1" {
		t.Fatalf("unexpected entry %+v", entry)
	}
}

func TestStatsCountsKeys(t *testing.T) {
	db := tempDB(t)
	if err := db.Put("beta", "2"); err != nil {
		t.Fatalf("put: %v", err)
	}
	stats, err := db.Stats()
	if err != nil {
		t.Fatalf("stats: %v", err)
	}
	if stats.KvEntries != 1 {
		t.Fatalf("expected 1 kv entry, got %d", stats.KvEntries)
	}
}

func TestMissingKeyFailsLoud(t *testing.T) {
	db := tempDB(t)
	if _, err := db.Get("nope"); err == nil {
		t.Fatal("expected an error for a missing key")
	}
}
