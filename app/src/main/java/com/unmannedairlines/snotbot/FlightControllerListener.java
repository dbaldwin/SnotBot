package com.unmannedairlines.snotbot;

import android.location.Location;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.util.Log;

import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;

/**
 * Created by db on 11/3/18.
 */

public class FlightControllerListener implements FlightControllerState.Callback {

    private static final String TAG = FlightControllerListener.class.getName();
    private MainActivity activity;


    // Calculate the rolling averages for pitch and roll using the last 10 readings
    RollingAverage pitchRA = new RollingAverage(10);
    RollingAverage rollRA = new RollingAverage(10);

    public FlightControllerListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onUpdate(@NonNull FlightControllerState flightControllerState) {
        updateTelemetry(flightControllerState);
    }

    // By default this method should run at 10 Hz
    private void updateTelemetry(FlightControllerState flightControllerState) {

        // Must be declared final to use within inner class
        final FlightControllerState fcState = flightControllerState;

        // Get aircraft attitude
        Attitude att = fcState.getAttitude();
        LocationCoordinate3D position = flightControllerState.getAircraftLocation();
        final double pitch = att.pitch;
        final double roll = att.roll;
        final double yaw = att.yaw;

        // Just log these for now
        Log.v(TAG, "Pitch: " + Double.toString(pitch));
        Log.v(TAG, "Roll: " + Double.toString(roll));
        Log.v(TAG, "Yaw: " + Double.toString(yaw));

        // Get aircraft velocity
        Log.v(TAG, "Vel X: " + Float.toString(fcState.getVelocityX()));
        Log.v(TAG, "Vel Y: " + Float.toString(fcState.getVelocityY()));
        Log.v(TAG, "Vel Z: " + Float.toString(fcState.getVelocityZ()));

        // Get aircraft location
        final LocationCoordinate3D location = fcState.getAircraftLocation();
        Log.v(TAG, "Lat: " + Double.toString(location.getLatitude()));
        Log.v(TAG, "Lng: " + Double.toString(location.getLongitude()));
        Log.v(TAG, "Alt: " + Double.toString(location.getAltitude()));

        // Do this on the UI thread so we can update text views
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Calculate and display tilt
                double tilt = Wind.calculateTilt(pitch, roll);
                Log.v(TAG, "Wind tilt: " + Double.toString(tilt));

                // display altitude on scale
                float alt = location.getAltitude();
                activity.altArrow.setY(1150-(alt*(float)69));

                //Y 100dp = 45ft
                //Y 575dp = 0ft
                //setY(975 - ...) sets arrow to about 8ft

                //only update wind if stick inputs are 0
                stickCheck:
                if (RemoteControllerListener.rightStickVertical == 0 && RemoteControllerListener.rightStickHorizontal == 0 && RemoteControllerListener.leftStickVertical == 0 && RemoteControllerListener.leftStickHorizontal == 0)
                {
                    //if drone still has momentum when stopping, use temp values instead of recalculating
                    if(fcState.getVelocityX() >.5 || fcState.getVelocityY() > .5 || fcState.getVelocityY() < -.5 || fcState.getVelocityX() < -.5)
                        break stickCheck;

                    // Set the wind arrow direction
                    double direction = Wind.calculateDirection(pitch, roll, yaw);
                    Log.v(TAG, "Updated wind direction: " + Double.toString(direction));

                    // We're using the average of the sensor values to update the wind widget
                    pitchRA.add(pitch);
                    rollRA.add(roll);
                    activity.windArrow.setRotation((float) Wind.calculateDirection(pitchRA.getAverage(), rollRA.getAverage(), yaw));
                    activity.maxWind.setRotation((float) Wind.calculateDirection(pitchRA.sorted[9], rollRA.sorted[9], yaw));
                    activity.minWind.setRotation((float) Wind.calculateDirection(pitchRA.sorted[0], rollRA.sorted[0], yaw));
                }

                else{
                    double direction = Wind.tempDirection - yaw + Wind.tempYaw;
                    Log.v(TAG, "Temp wind direction: " + Double.toString(direction));
                    activity.windArrow.setRotation((float)direction);
                }

            }
        });


    }
}
