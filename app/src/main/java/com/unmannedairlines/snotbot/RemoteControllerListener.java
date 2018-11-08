package com.unmannedairlines.snotbot;

import android.support.annotation.NonNull;
import android.util.Log;
import dji.common.remotecontroller.HardwareState;

/**
 * Created by db on 11/4/18.
 */

public class RemoteControllerListener implements HardwareState.HardwareStateCallback {

    protected static int leftStickHorizontal, leftStickVertical, rightStickHorizontal, rightStickVertical;

    private static final String TAG = RemoteControllerListener.class.getName();

    @Override
    // Looks like endpoint ranges from -660 to +660 in vertical and horizontal directions
    public void onUpdate(@NonNull HardwareState hardwareState) {

         leftStickHorizontal = hardwareState.getLeftStick().getHorizontalPosition();
         leftStickVertical = hardwareState.getLeftStick().getVerticalPosition();
         rightStickHorizontal = hardwareState.getRightStick().getHorizontalPosition();
         rightStickVertical = hardwareState.getRightStick().getVerticalPosition();

        Log.v(TAG, "Sticks: " + leftStickHorizontal + "," + leftStickVertical + "," + rightStickHorizontal + "," + rightStickVertical);

    }
}
