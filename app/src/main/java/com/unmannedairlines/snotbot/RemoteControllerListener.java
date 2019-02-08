package com.unmannedairlines.snotbot;

import android.preference.ListPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.Log;

import dji.common.gimbal.Attitude;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.remotecontroller.AircraftMapping;
import dji.common.remotecontroller.HardwareState;
import dji.keysdk.GimbalKey;
import dji.sdk.base.BaseProduct;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.mission.timeline.actions.GimbalAttitudeAction;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

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

        //set gimbal to 60 degrees on button press
        if (hardwareState.getC1Button().isClicked()) {

            BaseProduct product = DJISDKManager.getInstance().getProduct();
            Gimbal gimbal = product.getGimbal();

            Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);

            builder.pitch(-60);
            builder.roll(0);
            builder.yaw(0);

            sendRotateGimbalCommand(builder.build());
        }
    }//end onUpdate

    private void sendRotateGimbalCommand(Rotation rotation) {


        BaseProduct product = DJISDKManager.getInstance().getProduct();
        Gimbal gimbal = product.getGimbal();

        if (gimbal == null) {

            return;

        }

        gimbal.rotate(rotation, null);

    }

}
