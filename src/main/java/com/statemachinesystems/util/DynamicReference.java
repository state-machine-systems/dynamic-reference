package com.statemachinesystems.util;

/**
 * A dynamically-scoped reference.
 *
 * @param <T>  the type of value being referenced
 */
public class DynamicReference<T> {

    private final InheritableThreadLocal<T> threadLocal;

    /**
     * Create a dynamically-scoped reference with the given initial default value.
     *
     * @param initialValue  the initial default value to use
     */
    public DynamicReference(T initialValue) {
        this.threadLocal = new InheritableThreadLocal<T>() {
            @Override
            protected T initialValue() {
                return initialValue;
            }
        };
    }

    /**
     * Retrieve the current value of the reference.
     *
     * @return  the current value of the reference
     */
    public T get() {
        return threadLocal.get();
    }

    /**
     * Override the value of the reference while executing the given block.
     *
     * @param newValue  the overridden value to use within the given block
     * @param block     the block to execute
     */
    public void withValue(T newValue, Runnable block) {
        T oldValue = threadLocal.get();
        threadLocal.set(newValue);

        try {
            block.run();
        } finally {
            threadLocal.set(oldValue);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }
}
