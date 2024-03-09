package com.edp2021c1.randomseatgenerator.util.exception;

/**
 * Thrown when the given class type cannot be cast into another class type.
 */
public class InvalidClassTypeException extends ClassCastException {
    /**
     * Constructs an instance.
     *
     * @param clazz1 class type to cast to
     * @param clazz2 the given class type
     */
    public InvalidClassTypeException(final Class<?> clazz1, final Class<?> clazz2) {
        super("Unmatched class type: " + clazz1 + " and " + clazz2);
    }
}
