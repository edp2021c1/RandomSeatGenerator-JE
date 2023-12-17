package com.edp2021c1.randomseatgenerator;

import org.junit.Test;

public class RandomSeatGeneratorTest {
    @Test
    public void testGUI() {
        RandomSeatGenerator.main(new String[0]);
    }

    @Test
    public void testConsole() {
        RandomSeatGenerator.main(new String[]{"--nogui"});
    }
}
