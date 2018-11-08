package com.unmannedairlines.snotbot;

import android.support.annotation.NonNull;

import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;

/**
 * Created by db on 11/3/18.
 */

public class FlightControllerListener implements FlightControllerState.Callback {

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
        final double pitch = att.pitch;
        final double roll = att.roll;
        final double yaw = att.yaw;

        // Do this on the UI thread so we can update text views
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                activity.attPitch.setText(Double.toString(pitch));
                activity.attRoll.setText(Double.toString(roll));
                activity.attYaw.setText(Double.toString(yaw));

                // Calculate and display tilt
                double tilt = Wind.calculateTilt(pitch, roll);
                activity.attTilt.setText(Double.toString(tilt));

                //only update wind if stick inputs are 0
                stickCheck:
                if (RemoteControllerListener.rightStickVertical == 0 && RemoteControllerListener.rightStickHorizontal == 0 && RemoteControllerListener.leftStickVertical == 0 && RemoteControllerListener.leftStickHorizontal == 0)
                {
                    //if drone still has momentum when stopping, use temp values instead of recalculating
                    if(fcState.getVelocityX() >.5 || fcState.getVelocityY() > .5 || fcState.getVelocityY() < -.5 || fcState.getVelocityX() < -.5)
                        break stickCheck;

                    // Set the wind arrow direction
                    double direction = Wind.calculateDirection(pitch, roll, yaw);
                    activity.attDirection.setText(Double.toString(direction));

                    // We're using the average of the sensor values to update the wind widget
                    pitchRA.add(pitch);
                    rollRA.add(roll);
                    activity.windArrow.setRotation((float) Wind.calculateDirection(pitchRA.getAverage(), rollRA.getAverage(), yaw));
                }

                else{
                    double direction = Wind.tempDirection - yaw + Wind.tempYaw;
                    activity.attDirection.setText(Double.toString(direction));

                    activity.windArrow.setRotation((float)direction);
                }
                    // Get aircraft velocity
                    activity.xVel.setText(Float.toString(fcState.getVelocityX()));
                    activity.yVel.setText(Float.toString(fcState.getVelocityY()));
                    activity.zVel.setText(Float.toString(fcState.getVelocityZ()));

                    // Get aircraft location
                    LocationCoordinate3D location = fcState.getAircraftLocation();
                    activity.lat.setText(Double.toString(location.getLatitude()));
                    activity.lng.setText(Double.toString(location.getLongitude()));
                    activity.alt.setText(Float.toString(location.getAltitude()));

            }
        });


    }
}
