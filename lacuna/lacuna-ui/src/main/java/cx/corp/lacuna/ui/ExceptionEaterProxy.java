package cx.corp.lacuna.ui;

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

class ExceptionEaterProxy<T> implements InvocationProxy<T> {
    private final InvocationProxy<T> decoratedProxy;
    private final Set<Class> eatenExceptions;
    private Map<Class, List<Consumer<Exception>>> exceptionHandlers = new HashMap<>();

    ExceptionEaterProxy(InvocationProxy<T> proxy, Class... eatenExceptions) {
        this.decoratedProxy = proxy;
        this.eatenExceptions = new HashSet<>(Arrays.<Class>asList(eatenExceptions));
    }

    @Override
    public void invoke(Consumer<T> method) {
        Objects.requireNonNull(method);
        try {
            decoratedProxy.invoke(method);
        } catch (Exception ex) {
            if (!eatenExceptions.contains(ex.getClass())) {
                throw ex;
            }
            handleIfHandlerExists(ex);
        }
    }

    public <R> R invoke(Function<T, R> method, R defaultRet) {
        Objects.requireNonNull(method);
        try {
            return decoratedProxy.invoke(method, defaultRet);
        } catch (Exception ex) {
            if (!eatenExceptions.contains(ex.getClass())) {
                throw ex;
            }
            handleIfHandlerExists(ex);
            return defaultRet;
        }
    }

    public void addEatListener(Class<? extends Exception> type, Consumer<Exception> handler) {
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
}