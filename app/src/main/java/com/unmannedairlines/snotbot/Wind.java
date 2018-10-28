package com.unmannedairlines.snotbot;

/**
 * Created by db on 10/27/18.
 */

public class Wind {

    public static double calculateTilt(double pitch, double roll) {
        double pitchDegrees = pitch * Math.PI/180;
        double rollDegrees = roll * Math.PI/180;
        double tiltDegrees = Math.acos(Math.cos(pitchDegrees)*Math.cos(rollDegrees)) * 180/Math.PI;
        return Math.ceil(tiltDegrees);

    }

    public static void calculateDirection() {

    }

}
