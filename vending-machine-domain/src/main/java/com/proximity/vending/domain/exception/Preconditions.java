package com.proximity.vending.domain.exception;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Supplier;

@UtilityClass
public class Preconditions {

    /**
     *
     * @param expression A boolean expression
     * @param action The supplier which will return the exception to be thrown
     * @param <X> Type of exception to be thrown
     * @throws X if {@code expression} is {@code true}
     */
    public static <X extends Throwable> void checkArgument(boolean expression, Supplier<? extends X> action) throws X {
        if (expression) {
            throw action.get();
        }
    }

    /**
     *
     * @param expression A boolean expression
     * @param action The supplier which will return the exception to be thrown
     * @param <X> Type of exception to be thrown
     * @throws X if {@code expression} is {@code false}
     */
    public static <X extends Throwable> void checkNotArgument(boolean expression, Supplier<? extends X> action) throws X {
        if (!expression) {
            throw action.get();
        }
    }

    /**
     *
     * @param reference An object to evaluate
     * @param action The supplier which will return the exception to be thrown
     * @param <X> Type of exception to be thrown
     * @throws X if {@code reference} is {@code null}
     */
    public static <T, X extends Throwable> void checkNotNull(T reference, Supplier<? extends X> action) throws X {
        if (Objects.isNull(reference)) {
            throw action.get();
        }
    }
}
