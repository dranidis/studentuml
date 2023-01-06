package edu.city.studentuml.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScaleRoundTest {
    @Test
    public void testRoundTo05() {
        assertEquals(1.25, ScaleRound.roundTo05(1.2750), 0);
        assertEquals(1.30, ScaleRound.roundTo05(1.2751), 0);
        assertEquals(1.30, ScaleRound.roundTo05(1.2755), 0);
        assertEquals(1.30, ScaleRound.roundTo05(1.3155), 0);
        assertEquals(1.30, ScaleRound.roundTo05(1.3156), 0);
        assertEquals(0.00, ScaleRound.roundTo05(0.0050), 0);
        assertEquals(0.00, ScaleRound.roundTo05(0.0049), 0);
        assertEquals(0.05, ScaleRound.roundTo05(0.0451), 0);
        assertEquals(0.15, ScaleRound.roundTo05(0.13), 0);
        assertEquals(0.95, ScaleRound.roundTo05(0.945), 0);
    }    
}
