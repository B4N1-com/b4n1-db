package com.b4n1.db;

import java.io.IOException;

/** Queue engine: {@code b4n1-db queue <verb>}. */
public final class Queue extends Engine {
    Queue(B4n1Db db) {
        super(db);
    }

    /** Appends a message and returns its id. */
    public String push(String queue, String body) throws IOException {
        return call("queue", "push", queue, body);
    }

    /** Takes the oldest pending message out of the queue. */
    public String pop(String queue) throws IOException {
        return call("queue", "pop", queue);
    }

    /** Looks at the oldest pending message without taking it. */
    public String peek(String queue) throws IOException {
        return call("queue", "peek", queue);
    }

    /** Confirms a message so its parked payload is dropped. */
    public String ack(String queue, String id) throws IOException {
        return call("queue", "ack", queue, id);
    }

    /** Requeues an in-flight message at its original position. */
    public String nack(String queue, String id) throws IOException {
        return call("queue", "nack", queue, id);
    }

    /** Empties the queue. */
    public String clear(String queue) throws IOException {
        return call("queue", "clear", queue);
    }

    /** Counts pending messages. */
    public String len(String queue) throws IOException {
        return call("queue", "len", queue);
    }
}
