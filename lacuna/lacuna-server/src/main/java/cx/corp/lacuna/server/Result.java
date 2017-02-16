package cx.corp.lacuna.server;

import java.util.Objects;

public final class Result {
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    private String status = null;
    private Object data = null;

    // No public constructor declared so nobody can create `new`
    // instances without using our static methods below.

    private Result(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public static Result success(Object data) {
        return create(SUCCESS, data);
    }

    public static Result error(Object data) {
        return create(ERROR, data);
    }

    private static Result create(String status, Object data) {
        return new Result(status, data);
    }

    public Object getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result result = (Result) o;
        return Objects.equals(status, result.status) && Objects.equals(data, result.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data);
    }
}