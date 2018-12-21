package com.unmannedairlines.snotbot;
import android.app.Application;
import android.content.Context;
import com.secneo.sdk.Helper;

import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * Created by db on 10/19/18.
 */

public class MApplication extends Application {

    private static BaseProduct product;

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
    }

    public static synchronized BaseProduct getProductInstance() {
        product = DJISDKManager.getInstance().getProduct();
        return product;
    }

    public static boolean isAircraft() {
        return MApplication.getProductInstance() instanceof Aircraft;
    }

    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static boolean isProductModuleAvailable() {
        return (null != MApplication.getProductInstance());
    }

    public static boolean isCameraModuleAvailable() {
        return isProductModuleAvailable() && (null != MApplication.getProductInstance().getCamera());
    }

    public static boolean isFlightControllerAvailable() {
        return isProductModuleAvailable() && isAircraft() && (null != MApplication.getAircraftInstance()
                .getFlightController());
    }

    public static boolean isRemoteControllerAvailable() {
        return isProductModuleAvailable() && isAircraft() && (null != MApplication.getAircraftInstance()
                .getRemoteController());
    }

    public static synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) {
            return null;
        }
        return (Aircraft) getProductInstance();
    }
}

