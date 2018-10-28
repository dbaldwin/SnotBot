package com.unmannedairlines.snotbot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private static BaseProduct mProduct;
    private Handler mHandler;

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private List<String> missingPermission = new ArrayList<>();
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    private static final int REQUEST_PERMISSION_CODE = 12345;

    // Text views to display telemetry data
    private TextView attPitch, attRoll, attYaw, attTilt, attDirection;
    private TextView xVel, yVel, zVel;
    private TextView lat, lng, alt;
    private ImageView windArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAndRequestPermissions();

        setContentView(R.layout.activity_main);

        //Initialize DJI SDK Manager
        mHandler = new Handler(Looper.getMainLooper());

        // Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Init the text views
        attPitch = (TextView) findViewById(R.id.attPitch);
        attRoll = (TextView) findViewById(R.id.attRoll);
        attYaw = (TextView) findViewById(R.id.attYaw);
        attTilt = (TextView) findViewById(R.id.attTilt);
        attDirection = (TextView) findViewById(R.id.attDirection);
        xVel = (TextView) findViewById(R.id.xVel);
        yVel = (TextView) findViewById(R.id.yVel);
        zVel = (TextView) findViewById(R.id.zVel);
        lat = (TextView) findViewById(R.id.lat);
        lng = (TextView) findViewById(R.id.lng);
        alt = (TextView) findViewById(R.id.alt);
        windArrow = (ImageView) findViewById(R.id.windArrow);

    }

    /**
     * Checks if there is are missing permissions, and
     * requests runtime permission if needed.
     */
    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showToast("Need to grant permissions to proceed.");
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }
    }
    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            showToast("Missing permissions.");
        }
    }

    private void startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    showToast("SDK registration in progress...");
                    DJISDKManager.getInstance().registerApp(MainActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                        @Override
                        public void onRegister(DJIError djiError) {
                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                                showToast("SDK registered successfully...");
                                DJISDKManager.getInstance().startConnectionToProduct();
                            } else {
                                showToast("SDK registration failds, please check the bundle id and network connection...");
                            }
                            Log.v(TAG, djiError.getDescription());
                        }
                        @Override
                        public void onProductDisconnect() {
                            Log.d(TAG, "onProductDisconnect");
                            showToast("Product Disconnected");
                            notifyStatusChange();
                        }
                        @Override
                        public void onProductConnect(BaseProduct baseProduct) {
                            Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
                            showToast(baseProduct.getModel().getDisplayName() + " connected");
                            notifyStatusChange();

                            // TODO: Refactor into separate class
                            // This sets up a listener for us to grab FC attitude, velocity, and location
                            Aircraft a = (Aircraft) DJISDKManager.getInstance().getProduct();
                            FlightController f = a.getFlightController();
                            f.setStateCallback(new FlightControllerState.Callback() {
                                @Override
                                public void onUpdate(@NonNull FlightControllerState flightControllerState) {

                                    // Method to update the UI
                                    updateTelemetry(flightControllerState);

                                }
                            });

                        }
                        @Override
                        public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                                      BaseComponent newComponent) {
                            if (newComponent != null) {
                                newComponent.setComponentListener(new BaseComponent.ComponentListener() {
                                    @Override
                                    public void onConnectivityChange(boolean isConnected) {
                                        Log.d(TAG, "onComponentConnectivityChanged: " + isConnected);
                                        notifyStatusChange();
                                    }
                                });
                            }
                            Log.d(TAG,
                                    String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                            componentKey,
                                            oldComponent,
                                            newComponent));
                        }
                    });
                }
            });
        }
    }

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            sendBroadcast(intent);
        }
    };

    // Toast popup message
    private void showToast(final String toastMsg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTelemetry(FlightControllerState flightControllerState) {

        // Must be declared final to use within inner class
        final FlightControllerState fcState = flightControllerState;

        // Do this on the UI thread so we can update text views
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Get aircraft attitude
                Attitude att = fcState.getAttitude();
                double pitch = att.pitch;
                double roll = att.roll;
                double yaw = att.yaw;
                attPitch.setText(Double.toString(pitch));
                attRoll.setText(Double.toString(roll));
                attYaw.setText(Double.toString(yaw));

                // Calculate and display tilt
                double tilt = Wind.calculateTilt(pitch, roll);
                attTilt.setText(Double.toString(tilt));

                // Set the wind arrow direction
                // setRotation starts at
                double direction = Wind.calculateDirection(pitch, roll);
                windArrow.setRotation((float)direction);
                attDirection.setText(Double.toString(direction));

                // Get aircraft velocity
                xVel.setText(Float.toString(fcState.getVelocityX()));
                yVel.setText(Float.toString(fcState.getVelocityY()));
                zVel.setText(Float.toString(fcState.getVelocityZ()));

                // Get aircraft location
                LocationCoordinate3D location = fcState.getAircraftLocation();
                lat.setText(Double.toString(location.getLatitude()));
                lng.setText(Double.toString(location.getLongitude()));
                alt.setText(Float.toString(location.getAltitude()));
            }

        });


    }
}
