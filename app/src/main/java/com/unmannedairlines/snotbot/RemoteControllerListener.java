package com.unmannedairlines.snotbot;

import android.support.annotation.NonNull;
import android.util.Log;
import dji.common.remotecontroller.HardwareState;

/**
 * Created by db on 11/4/18.
 */

public class RemoteControllerListener implements HardwareState.HardwareStateCallback {

    private static final String TAG = RemoteControllerListener.class.getName();

    @Override
    // Looks like endpoint ranges from -660 to +660 in vertical and horizontal directions
    public void onUpdate(@NonNull HardwareState hardwareState) {

        int leftStickHPosition = hardwareState.getLeftStick().getHorizontalPosition();
        int leftStickVPosition = hardwareState.getLeftStick().getVerticalPosition();
        int rightStickHPosition = hardwareState.getRightStick().getHorizontalPosition();
        int rightStickVPosition = hardwareState.getRightStick().getVerticalPosition();

        Log.v(TAG, "Sticks: " + leftStickHPosition + "," + leftStickVPosition + "," + rightStickHPosition + "," + rightStickVPosition);

    }
}
