package com.unmannedairlines.snotbot;

import android.support.annotation.NonNull;
import android.util.Log;

import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;

/**
 * Created by db on 12/20/18.
 */

public class CameraListener implements SystemState.Callback {

    public CameraListener() {

        // Default the camera to video
        setCameraMode(SettingsDefinitions.CameraMode.RECORD_VIDEO);

    }

    @Override
    public void onUpdate(@NonNull SystemState systemState) {

    }

    // Set camera mode to video so the CameraCaptureWidget will start/stop video when pressed
    private void setCameraMode(SettingsDefinitions.CameraMode cameraMode) {

        Camera camera = MApplication.getProductInstance().getCamera();

        if (camera != null) {

            camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                    if (djiError == null) {
                        Log.v("CameraListener", "Camera mode set to video");
                    } else {
                        Log.v("CameraListener", "Error setting camera mode to video");
                    }

                }
            });

        }

    }
}
