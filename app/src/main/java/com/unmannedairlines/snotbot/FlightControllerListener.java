package com.unmannedairlines.snotbot;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import dji.common.error.DJIError;
import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;

/**
 * Created by db on 11/3/18.
 */

public class FlightControllerListener implements FlightControllerState.Callback {

    private static final String TAG = FlightControllerListener.class.getName();
    private MainActivity activity;
    private boolean autoRecordVideo;


    // Calculate the rolling averages for pitch and roll using the last 10 readings
    RollingAverage pitchRA = new RollingAverage(10);
    RollingAverage rollRA = new RollingAverage(10);

    public FlightControllerListener(MainActivity activity) {
        this.activity = activity;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.activity);
        autoRecordVideo = prefs.getBoolean("auto_record_video", false);
    }

    @Override
    public void onUpdate(@NonNull FlightControllerState flightControllerState) {
        updateTelemetry(flightControllerState);

        // If motors are on let's determine if we should start recording video
        if (flightControllerState.areMotorsOn() && autoRecordVideo) {

            // Let's not continuously call this
            if (!CameraListener.isCameraRecording) {
                Camera camera = MApplication.getProductInstance().getCamera();
                camera.startRecordVideo(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (null != djiError) {
                            Log.v("FCListener", "Error starting record video");
                        }
                    }
                });
            }
        // Stop recording video
        } else if (!flightControllerState.areMotorsOn() && CameraListener.isCameraRecording) {

            Camera camera = MApplication.getProductInstance().getCamera();
            camera.stopRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                    if (null != djiError) {
                        Log.v("FCListener", "Error stopping record video");
                    }

                }
            });

        }
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

        /*
        // Just log these for now
        Log.v(TAG, "Pitch: " + Double.toString(pitch));
        Log.v(TAG, "Roll: " + Double.toString(roll));
        Log.v(TAG, "Yaw: " + Double.toString(yaw));

        // Get aircraft velocity
        Log.v(TAG, "Vel X: " + Float.toString(fcState.getVelocityX()));
        Log.v(TAG, "Vel Y: " + Float.toString(fcState.getVelocityY()));
        Log.v(TAG, "Vel Z: " + Float.toString(fcState.getVelocityZ()));
        */

        // Get aircraft location
        final LocationCoordinate3D location = fcState.getAircraftLocation();

        /*
        Log.v(TAG, "Lat: " + Double.toString(location.getLatitude()));
        Log.v(TAG, "Lng: " + Double.toString(location.getLongitude()));
        Log.v(TAG, "Alt: " + Double.toString(location.getAltitude()));
        */

        //set altitude variable in feet
        final float alt = location.getAltitude()*(float)3.28;//meters to feet

        // Do this on the UI thread so we can update text views
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Altitude
                TextView printAlt = (TextView) activity.findViewById(R.id.altitude);
                if((alt+activity.takeOff)>=8 && (alt+activity.takeOff) <=15)
                    printAlt.setTextColor(0xff00E51B);
                else
                    printAlt.setTextColor(0xffFF0000);

                String s = String.valueOf(alt+activity.takeOff);
                printAlt.setText(s);

                //altitude scale
                activity.altArrow.setY(1150-((alt+activity.takeOff)*(float)21));

                //Y 100dp = 45ft
                //Y 575dp = 0ft
                //setY(975 - ...) sets arrow to about 8ft


                // Calculate and display tilt
                double tilt = Wind.calculateTilt(pitch, roll);
                Log.v(TAG, "Wind tilt: " + Double.toString(tilt));

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
                    double direction = Wind.calculateDirection(pitchRA.getAverage(), rollRA.getAverage(), Wind.tempYaw) - yaw + Wind.tempYaw; //testing commit message 
                    Log.v(TAG, "Temp wind direction: " + Double.toString(direction));
                    activity.windArrow.setRotation((float)direction);
                }

            }
        });

    }
}
