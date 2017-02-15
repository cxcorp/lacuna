package cx.corp.lacuna.server;

import com.google.gson.Gson;

import static spark.Spark.*;

public class Main {

    private static Gson gson;

    public static void main(String... args) {
        gson = new Gson();

        port(8080);

        before((req, res) -> {
            res.header("Content-Type", "application/json; charset=utf-8");
        });

        get("/", (req, res) -> {
            return "ayy";
        }, gson::toJson);

        System.out.println("Server started on port 8080");
    }
}
