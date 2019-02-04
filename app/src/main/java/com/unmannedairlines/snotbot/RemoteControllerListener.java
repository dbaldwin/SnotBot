package com.unmannedairlines.snotbot;

import android.support.annotation.NonNull;
import android.util.Log;

import dji.common.gimbal.Attitude;
import dji.common.gimbal.Rotation;
import dji.common.remotecontroller.HardwareState;
import dji.sdk.mission.timeline.actions.GimbalAttitudeAction;

/**
 * Created by db on 11/4/18.
 */

public class RemoteControllerListener implements HardwareState.HardwareStateCallback {

    protected static int leftStickHorizontal, leftStickVertical, rightStickHorizontal, rightStickVertical;
    private static final String TAG = RemoteControllerListener.class.getName();
    public static boolean recordVideoWithRCButton = false;

    @Override
    // Looks like endpoint ranges from -660 to +660 in vertical and horizontal directions
    public void onUpdate(@NonNull HardwareState hardwareState) {


        leftStickHorizontal = hardwareState.getLeftStick().getHorizontalPosition();
        leftStickVertical = hardwareState.getLeftStick().getVerticalPosition();
        rightStickHorizontal = hardwareState.getRightStick().getHorizontalPosition();
        rightStickVertical = hardwareState.getRightStick().getVerticalPosition();

        //Log.v(TAG, "Sticks: " + leftStickHorizontal + "," + leftStickVertical + "," + rightStickHorizontal + "," + rightStickVertical);

        // All he RC video button to override the video recording settings
        if (hardwareState.getRecordButton().isClicked()) {

            if (!CameraListener.isCameraRecording)
                recordVideoWithRCButton = true;
            else
                recordVideoWithRCButton = false;


        }

        if (hardwareState.getC1Button().isClicked()) {
            Attitude attitude = new Attitude(-30, Rotation.NO_ROTATION, Rotation.NO_ROTATION);
            GimbalAttitudeAction gimbalAction = new GimbalAttitudeAction(attitude);
            gimbalAction.setCompletionTime(1);
        }

    }
}
