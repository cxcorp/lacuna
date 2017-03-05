package cx.corp.lacuna.ui;

import java.util.function.Consumer;
import java.util.function.Function;

class PassthroughProxy<T> implements InvocationProxy<T> {

    private final T targetObject;

    public PassthroughProxy(T targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public void invoke(Consumer<T> invoker) {
        invoker.accept(targetObject);
    }

    @Override
    public <R> R invoke(Function<T, R> invoker, R defaultReturn) {
        return invoker.apply(targetObject);
    }
}
