package com.statemachinesystems.util;

/**
 * A dynamically-scoped reference.
 *
 * @param <T>  the type of value being referenced
 */
public class DynamicReference<T> {

    private final InheritableThreadLocal<T> threadLocal;

    public DynamicReference(T initialValue) {
        this.threadLocal = new InheritableThreadLocal<T>() {
            @Override
            protected T initialValue() {
                return initialValue;
            }
        };
    }

    public T get() {
        return threadLocal.get();
    }

    public void withValue(T newValue, Runnable thunk) {
        T oldValue = threadLocal.get();
        threadLocal.set(newValue);

        try {
            thunk.run();
        } finally {
            threadLocal.set(oldValue);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }
}
