package com.edp2021c1.randomseatgenerator.core;

/**
 * Seat table generator class.
 *
 * @author Calboot
 * @since 1.5.0
 */
@FunctionalInterface
public interface SeatTableGenerator {

    /**
     * The default implementation of this class.
     */
    SeatTableGenerator defaultGenerator = SeatTableGeneratorAndCheckerImpl.instance;


    /**
     * Generates a seat table.
     *
     * @param config used to generate the seat table
     * @param seed   used to generate the seat table
     * @return an instance of {@code SeatTable}
     * @throws IllegalConfigException if config is null or has an illegal format
     */
    SeatTable generate(SeatConfig config, String seed)
            throws NullPointerException, IllegalConfigException;

}
