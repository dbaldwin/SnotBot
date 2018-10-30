package com.unmannedairlines.snotbot;

/**
 * Created by db on 10/30/18.
 */

public class SensorUtils {

    // A basic averaging function to see if we can reduce the jumpiness of the wind indicator
    public static double getAverageSensorValue(double[] sensorReadings) {

        double currentValue = 0;

        for (int i = 0; i < sensorReadings.length; i++) {

             currentValue += sensorReadings[i];

        }

        return currentValue/sensorReadings.length;
    }

    // Perhaps this is worth a try below
    // http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
    static final float ALPHA = 0.15f;

    protected static double[] lowPass( double[] input, double[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}
