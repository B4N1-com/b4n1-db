package com.b4n1.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Thin Java wrapper over the {@code b4n1-db} standalone binary.
 *
 * <p>All storage, SQL, graph, vector and audit logic lives in the binary; this
 * class only invokes it. JSON is returned as text so the wrapper stays
 * dependency-free: parse it with the JSON library the application already uses.
 */
public final class B4n1Db {

    public static final String VERSION = "0.1.2";

    private static final long TIMEOUT_SECONDS = 60;

    private final String path;

    public B4n1Db() {
        this("./data");
    }

    public B4n1Db(String path) {
        this.path = path;
        this.kv = new Kv(this);
        this.sql = new Sql(this);
        this.graph = new Graph(this);
        this.doc = new Doc(this);
        this.queue = new Queue(this);
        this.vector = new Vector(this);
        this.audit = new Audit(this);
        this.migration = new Migration(this);
        this.transfer = new Transfer(this);
    }

    private final Kv kv;
    private final Sql sql;
    private final Graph graph;
    private final Doc doc;
    private final Queue queue;
    private final Vector vector;
    private final Audit audit;
    private final Migration migration;
    private final Transfer transfer;

    /** Key-value engine. */
    public Kv kv() {
        return kv;
    }

    /** Analytical SQL engine. */
    public Sql sql() {
        return sql;
    }

    /** Knowledge graph engine. */
    public Graph graph() {
        return graph;
    }

    /** Document collections. */
    public Doc doc() {
        return doc;
    }

    /** FIFO queues with ack/nack. */
    public Queue queue() {
        return queue;
    }

    /** Vector index with cosine similarity. */
    public Vector vector() {
        return vector;
    }

    /** Merkle-DAG audit log. */
    public Audit audit() {
        return audit;
    }

    /** Schema migration registry. */
    public Migration migration() {
        return migration;
    }

    /** Backup, restore and JSON transfer. */
    public Transfer transfer() {
        return transfer;
    }

    /**
     * Runs a verb with {@code --json} and returns its answer as text.
     *
     * <p>The engine groups call this; consumers never need to.
     */
    public String invoke(String... args) throws IOException {
        return run(true, args);
    }

    /** Resolves the binary: {@code B4N1_DB_BIN}, then {@code PATH}. */
    public static String binary() {
        String env = System.getenv("B4N1_DB_BIN");
        if (env != null && new File(env).isFile()) {
            return env;
        }
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            for (String dir : pathEnv.split(File.pathSeparator)) {
                File candidate = new File(dir, "b4n1-db");
                if (candidate.isFile()) {
                    return candidate.getAbsolutePath();
                }
            }
        }
        throw new IllegalStateException(
                "b4n1-db binary not found; set B4N1_DB_BIN or install the b4n1-db binary");
    }

    /** Database counters as JSON. */
    public String stats() throws IOException {
        return run(true, "stats");
    }

    /** Writes a key/value pair (alias of {@code kv().put}). */
    public void put(String key, String value) throws IOException {
        kv.put(key, value);
    }

    /** Reads a key (alias of {@code kv().get}); returns the entry as JSON. */
    public String get(String key) throws IOException {
        return kv.get(key);
    }

    /** Runs the offline one-click demonstration and returns its transcript. */
    public String demo() throws IOException {
        return run(false, "demo");
    }

    private String run(boolean jsonOut, String... args) throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add(binary());
        cmd.add("--db");
        cmd.add(path);
        if (jsonOut) {
            cmd.add("--json");
        }
        for (String arg : args) {
            cmd.add(arg);
        }

        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(false);
        Process process = builder.start();
        String stdout = read(process.getInputStream());
        String stderr = read(process.getErrorStream());
        try {
            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IOException("b4n1-db timed out after " + TIMEOUT_SECONDS + "s");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted while waiting for b4n1-db", e);
        }
        if (process.exitValue() != 0) {
            throw new IOException(stderr.isBlank() ? stdout.trim() : stderr.trim());
        }
        return stdout;
    }

    private static String read(InputStream in) throws IOException {
        try (InputStream stream = in) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
