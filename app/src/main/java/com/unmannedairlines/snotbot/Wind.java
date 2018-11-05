package com.unmannedairlines.snotbot;

/**
 * Created by db on 10/27/18.
 */

public class Wind {

    static double tempDirection;
    static double tempYaw;

    protected static double calculateTilt(double pitch, double roll) {
        double pitchDegrees = pitch * Math.PI/180;
        double rollDegrees = roll * Math.PI/180;
        double tiltDegrees = Math.acos(Math.cos(pitchDegrees)*Math.cos(rollDegrees)) * 180/Math.PI;
        return Math.ceil(tiltDegrees);

    }

    private static int calculateQuadrant(double pitch, double roll){
        int quadrant = 1;

        // Top right quadrant
        if(roll >= 0 && pitch <= 0)
            quadrant = 1;
        // Bottom right quadrant
        if(roll >= 0 && pitch >= 0)
            quadrant = 2;
        // Bottom left quadrant
        if(roll <= 0 && pitch >= 0)
            quadrant = 3;
        // Top left quadrant
        if(roll <= 0 && pitch <= 0)
            quadrant = 4;

        return quadrant;
    }

    protected static double calculateDirection(double pitch, double roll, double yaw) {
        double pitchDegrees = pitch * Math.PI/180;
        double rollDegrees = roll * Math.PI/180;
        double directionDegrees = Math.atan(Math.sin(pitchDegrees)/Math.tan(rollDegrees)) * 180/Math.PI;
        int quad = calculateQuadrant(pitch, roll);
        switch (quad)
        {
            case 1 :
                directionDegrees = (directionDegrees + 90.0);
                tempYaw = yaw;
                tempDirection = directionDegrees;
                break; // break is optional

            case 2 :
                directionDegrees = (directionDegrees + 90.0);
                tempYaw = yaw;
                tempDirection = directionDegrees;
                break; // break is optional

            case 3 :
                directionDegrees = (270 + directionDegrees);
                tempYaw = yaw;
                tempDirection = directionDegrees;
                break; // break is optional

            case 4 :
                directionDegrees = (270.0 + directionDegrees);
                tempYaw = yaw;
                tempDirection = directionDegrees;
                break; // break is optional

            default :
                directionDegrees = directionDegrees +0.0;
                tempYaw = yaw;
                tempDirection = directionDegrees;
        }

        return directionDegrees;
    }

}