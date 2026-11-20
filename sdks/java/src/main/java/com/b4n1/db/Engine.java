package com.b4n1.db;

import java.io.IOException;

/**
 * Base for the nine engine groups: argument marshalling only.
 *
 * <p>Every method returns the binary's {@code --json} answer as text, so the
 * package stays dependency-free. The contract is {@code sdk-surface.tsv} in the
 * crate root and the suite fails when a wrapper drifts from it.
 */
abstract class Engine {
    private final B4n1Db db;

    Engine(B4n1Db db) {
        this.db = db;
    }

    /** Runs a verb and returns its JSON answer. */
    String call(String... args) throws IOException {
        return db.invoke(args);
    }

    /** Renders an integer argument the way the binary parses it. */
    static String num(long value) {
        return Long.toString(value);
    }

    /** Escapes a string into a JSON string literal (quotes included). */
    static String quote(String value) {
        StringBuilder out = new StringBuilder(value.length() + 2).append('"');
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.append('"').toString();
    }
}
