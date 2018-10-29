package com.unmannedairlines.snotbot;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void subtraction_isCorrect() throws Exception {
        assertEquals(10, 20-10);
    }

    @Test
    public void calculate60DegreeTilt() throws Exception {
        double tilt = Wind.calculateTilt(45, 45);
        assertEquals(60.0, tilt, 0);
    }

    @Test
    public void calculateDirection() throws Exception {
        double direction = Wind.calculateDirection(25, 15);
        assertEquals(327, direction, 1);
    }


}