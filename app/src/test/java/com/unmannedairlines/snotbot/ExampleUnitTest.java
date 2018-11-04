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

    @Test
    public void rollingAverage() throws Exception {
        RollingAverage ra = new RollingAverage(10);

        ra.add(45);
        ra.add(42);
        ra.add(30);
        ra.add(35);
        ra.add(39);
        ra.add(37);
        ra.add(41);
        ra.add(41);
        ra.add(33);
        ra.add(39);

        assertEquals(38.2, ra.getAverage(), 0);

    }

}