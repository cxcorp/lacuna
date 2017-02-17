package cx.corp.lacuna.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cx.corp.lacuna.core.LacunaBootstrap;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.NativeProcessCollector;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.domain.NativeProcess;

import static spark.Spark.*;

public final class Main {
    private static LacunaBootstrap lacuna;
    private static Gson gson;

    public static void main(String... args) {
        lacuna = LacunaBootstrap.forCurrentPlatform();
        gson = new GsonBuilder().setPrettyPrinting().create();
        NativeProcessEnumerator processEnumerator = lacuna.getNativeProcessEnumerator();
        NativeProcessCollector processCollector = lacuna.getNativeProcessCollector();
        MemoryReader memoryReader = lacuna.getMemoryReader();

        port(8080);

        exception(Exception.class, ((exception, request, response) -> {
            exception.printStackTrace();
        }));

        webSocket("/processes/memory", ProcessMemoryWebSocketHandler.class);

        after((req, res) -> {
            res.header("Content-Type", "application/json; charset=utf-8");
            res.header("Content-Encoding", "gzip");
        });

        get("/processes", (req, res) -> {
            Thread.sleep(1000);
            return Result.success(processEnumerator.getProcesses());
        }, gson::toJson);

        get("/processes/:pid", (req, res) -> {
            Thread.sleep(1000);
            Integer pid = Integer.parseInt(req.params("pid"));
            return Result.success(processCollector.collect(pid));
        }, gson::toJson);

        /*get("/processes/:pid/memory", (req, res) -> {
            Integer pid = Integer.parseInt(req.params("pid"));
            Integer offset = Integer.parseInt(req.queryParams("offset"), 16);
            NativeProcess process = processCollector.collect(pid);

            Object result = null;
            switch (req.queryParams("mode")) {
                case "int":
                    result = memoryReader.readInt(process, offset);
                    break;
            }

            return Result.success(result);
        }, gson::toJson);*/

        get("*", (req, res) -> {
            res.status(500);
            return Result.error("Not found");
        }, gson::toJson);

        init();
        System.out.println("Server started on port 8080");
    }
}
