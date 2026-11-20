import com.b4n1.db.B4n1Db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Every engine of the binary, exercised through the Java SDK.
 *
 * <p>Plain Java, no JUnit: the suite compiles the sources with javac and runs
 * this class, with B4N1_DB_BIN pointing at the build under test.
 */
public final class SurfaceSmoke {

    private static int failures = 0;

    private static void check(String what, boolean ok) {
        if (!ok) {
            failures++;
            System.err.println("  FAIL " + what);
        }
    }

    private static String id(String json) {
        int at = json.indexOf("\"id\": \"");
        if (at < 0) {
            return "";
        }
        return json.substring(at + 7, json.indexOf('"', at + 7));
    }

    private static String field(String json, String name) {
        int at = json.indexOf("\"" + name + "\": ");
        if (at < 0) {
            return "";
        }
        int start = at + name.length() + 4;
        int end = start;
        while (end < json.length() && ",}\n".indexOf(json.charAt(end)) < 0) {
            end++;
        }
        return json.substring(start, end).replace("\"", "").trim();
    }

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("b4n1-db-java-");
        B4n1Db db = new B4n1Db(dir.resolve("data").toString());

        // kv
        check("kv.put", db.kv().put("k", "v").contains("\"ok\": true"));
        check("kv.get", db.kv().get("k").contains("\"value\": \"v\""));
        check("kv.scan", db.kv().scan("").contains("\"count\": 1"));
        check("kv.put_ttl", db.kv().putTtl("t", "x", 60).contains("\"ttl_secs\": 60"));
        check("kv.ttl", !db.kv().ttl("t").contains("\"ttl_secs\": 0"));
        check("kv.delete", db.kv().delete("k").contains("\"ok\": true"));
        check("kv.purge_expired", db.kv().purgeExpired().contains("\"purged\": 0"));
        boolean threw = false;
        try {
            db.kv().get("k");
        } catch (IOException expected) {
            threw = true;
        }
        check("kv.get missing throws", threw);

        // sql
        check("sql.execute", db.sql().execute("CREATE TABLE u (id BIGINT, name VARCHAR)")
                .contains("\"rows_affected\": 0"));
        check("sql.execute insert", db.sql().execute("INSERT INTO u VALUES (1, 'ada')")
                .contains("\"rows_affected\": 1"));
        check("sql.query", db.sql().query("SELECT * FROM u").contains("ada"));
        check("sql.tables", db.sql().tables().contains("\"u\""));

        B4n1Db second = new B4n1Db(dir.resolve("data").toString());
        check("sql survives a second handle", second.sql().query("SELECT name FROM u").contains("ada"));

        Path csv = dir.resolve("rows.csv");
        Files.writeString(csv, "id\n7\n");
        check("sql.register_csv", db.sql().registerCsv("c1", csv.toString()).contains("\"ok\": true"));
        check("sql.query csv", db.sql().query("SELECT id FROM c1").contains("\"id\": 7"));
        check("sql.drop", second.sql().drop("u").contains("\"ok\": true"));
        check("sql.tables after drop", !second.sql().tables().contains("\"u\""));

        // graph
        String node = db.graph().addNode("pattern", "ada");
        String other = db.graph().addNode("pattern", "grace");
        check("graph.add_node", id(node).length() == 36);
        check("graph.node", db.graph().node(id(node)).contains("\"node\""));
        check("graph.search", !db.graph().search("ada", 5).contains("\"count\": 0"));
        check("graph.add_relation",
                db.graph().addRelation(id(node), id(other), "follows", 0.5).contains("\"relation\": \"follows\""));
        check("graph.forget", db.graph().forget(id(node)).contains("\"ok\": true"));

        // documents
        String inserted = db.doc().insert("users", "{\"name\":\"ada\",\"age\":36}");
        check("doc.insert", inserted.contains("\"collection\": \"users\""));
        check("doc.find_one", db.doc().findOne("users", id(inserted)).contains(id(inserted)));
        check("doc.find", db.doc().find("users").contains("\"count\": 1"));
        check("doc.update", db.doc().update("users", id(inserted), "{\"age\":37}").contains("\"ok\": true"));
        check("doc.find_by_field", db.doc().findByField("users", "name", "\"ada\"").contains("\"count\": 1"));
        check("doc.delete", db.doc().delete("users", id(inserted)).contains("\"ok\": true"));
        check("doc.find after delete", db.doc().find("users").contains("\"count\": 0"));

        // queue
        String pushed = db.queue().push("jobs", "build");
        check("queue.len", db.queue().len("jobs").contains("\"pending\": 1"));
        check("queue.peek", db.queue().peek("jobs").contains("\"body\": \"build\""));
        check("queue.pop", db.queue().pop("jobs").contains(id(pushed)));
        check("queue.nack", db.queue().nack("jobs", id(pushed)).contains("\"requeued\": true"));
        db.queue().pop("jobs");
        check("queue.ack", db.queue().ack("jobs", id(pushed)).contains("\"ok\": true"));
        check("queue.clear", db.queue().clear("jobs").contains("\"ok\": true"));

        // vectors
        double[] embedding = new double[1536];
        embedding[0] = 1.0;
        String record = db.vector().insert("ns", embedding, "{\"tag\":\"a\"}");
        check("vector.insert", id(record).length() == 36);
        check("vector.search", db.vector().search("ns", embedding, 3, "{\"tag\":\"a\"}").contains(id(record)));
        check("vector.list", db.vector().list("ns").contains("\"count\": 1"));
        check("vector.delete", db.vector().delete("ns", id(record)).contains("\"ok\": true"));

        // audit
        db.kv().put("a", "1");
        check("audit.verify", db.audit().verify().contains("\"intact\": true"));
        check("audit.history", db.audit().history("a").contains("\"count\": 1"));
        String latest = db.audit().latest("a");
        check("audit.latest", latest.contains("\"key\": \"a\""));
        long stamp = Long.parseLong(field(latest, "timestamp"));
        check("audit.time_travel", db.audit().timeTravel("a", stamp).contains("\"node\""));
        check("audit.root_hash", db.audit().rootHash().contains("\"root_hash\": \""));

        // migrations
        check("migration.add", db.migration().add(1, "init", "CREATE TABLE m (id BIGINT)")
                .contains("\"registered\": true"));
        B4n1Db third = new B4n1Db(dir.resolve("data").toString());
        check("migration.list", third.migration().list().contains("\"registered\": 1"));
        third.migration().exec();
        check("migration.exec", third.migration().list().contains("\"current_version\": 1"));
        check("migration.apply", third.migration().apply().contains("\"ddl_executed\": false"));
        check("migration.rollback", third.migration().rollback(0).contains("\"rolled_back\": ["));
        boolean duplicate = false;
        try {
            third.migration().add(1, "again", "SELECT 1");
        } catch (IOException expected) {
            duplicate = true;
        }
        check("migration.add duplicate throws", duplicate);

        // transfer
        db.kv().put("keep", "me");
        check("transfer.backup", db.transfer().backup(dir.resolve("bak").toString()).contains("\"files\": "));
        check("transfer.export", db.transfer().export(dir.resolve("dump.jsonl").toString()).contains("\"bytes\": "));
        check("transfer.import", db.transfer().importFrom(dir.resolve("dump.jsonl").toString()).contains("\"records\": "));
        check("transfer.restore", db.transfer().restore(dir.resolve("bak").toString()).contains("\"ok\": true"));

        // stats and the 0.1 aliases
        check("stats", db.stats().contains("\"kv_entries\""));
        db.put("pk", "pv");
        check("put alias", db.get("pk").contains("\"value\": \"pv\""));
        check("demo", db.demo().contains("database"));

        try (var walk = Files.walk(dir)) {
            walk.sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                            // best effort: the temp dir is disposable
                        }
                    });
        }

        System.out.println(failures == 0 ? "surface smoke: ok" : "surface smoke: " + failures + " failure(s)");
        System.exit(failures == 0 ? 0 : 1);
    }

    private SurfaceSmoke() {
        // static runner
    }
}
