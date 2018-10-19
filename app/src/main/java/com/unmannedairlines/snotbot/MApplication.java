package com.unmannedairlines.snotbot;
import android.app.Application;
import android.content.Context;
import com.secneo.sdk.Helper;

/**
 * Created by db on 10/19/18.
 */

public class MApplication extends Application {
    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
    }
}

