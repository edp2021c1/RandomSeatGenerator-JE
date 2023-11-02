package com.edp2021c1.randomseatgenerator.core;

/**
 * Thrown if {@code SeatConfig} has an illegal format.
 *
 * @author Calboot
 */
public class IllegalSeatConfigException extends RuntimeException {

    /**
     * Constructs an {@code IllegalSeatConfigException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public IllegalSeatConfigException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in the detail of this exception
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method). (A {@code null} value
     *                is permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public IllegalSeatConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
