package cx.corp.lacuna.ui;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @param <T> the type of the object passed to the proxy invoker.
 */
public interface InvocationProxy<T> {
    void invoke(Consumer<T> invoker);
    <R> R invoke(Function<T, R> invoker, R defaultReturn);
}
