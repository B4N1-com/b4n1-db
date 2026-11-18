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

    public static final String VERSION = "0.1.1";

    private static final long TIMEOUT_SECONDS = 60;

    private final String path;

    public B4n1Db() {
        this("./data");
    }

    public B4n1Db(String path) {
        this.path = path;
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

    /** Writes a key/value pair. */
    public void put(String key, String value) throws IOException {
        run(false, "kv", "put", key, value);
    }

    /** Reads a key; returns the entry as JSON. */
    public String get(String key) throws IOException {
        return run(true, "kv", "get", key);
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
