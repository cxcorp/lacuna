package cx.corp.lacuna.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

class ExceptionEater<T> {
    private final T object;
    private final Set<Class> eatenExceptions;
    private Map<Class, List<Consumer<Exception>>> exceptionHandlers = new HashMap<>();

    ExceptionEater(T object, Class... eatenExceptions) {
        this.object = object;
        this.eatenExceptions = new HashSet<>(Arrays.<Class>asList(eatenExceptions));
    }

    void safeInvoke(Consumer<T> method) {
        Objects.requireNonNull(method);
        try {
            method.accept(object);
        } catch (Exception ex) {
            log(ex);
            if (!eatenExceptions.contains(ex.getClass())) {
                throw ex;
            }
            handleIfHandlerExists(ex);
        }
    }

    <R> R safeInvokeReturn(Function<T, R> method, R defaultRet) {
        Objects.requireNonNull(method);
        try {
            return method.apply(object);
        } catch(Exception ex) {
            log(ex);
            if (!eatenExceptions.contains(ex.getClass())) {
                throw ex;
            }
            handleIfHandlerExists(ex);
            return defaultRet;
        }
    }

    void addHandlerForEatenException(Class<? extends Exception> type, Consumer<Exception> handler) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(handler);
        if (!isExceptionEaten(type)) {
            throw new IllegalArgumentException("No exception of type " + type.toString() + " is configured to be eaten!");
        }

        List<Consumer<Exception>> handlers = exceptionHandlers.computeIfAbsent(type, k -> new ArrayList<>());
        handlers.add(handler);
    }

    private boolean isExceptionEaten(Class<? extends Exception> type) {
        return eatenExceptions.contains(type);
    }

    private void handleIfHandlerExists(Exception ex) {
        List<Consumer<Exception>> handlers = exceptionHandlers.get(ex.getClass());
        if (handlers != null) {
            handlers.forEach(h -> h.accept(ex));
        }
    }

    private void log(Exception ex) {
        StringWriter errors = new StringWriter();
        errors.append("[SafeInvoke] ")
            .append(this.object.getClass().toString())
            .append(": ");
        ex.printStackTrace(new PrintWriter(errors));
        System.out.println(errors.toString());
        System.out.flush();
    }
}