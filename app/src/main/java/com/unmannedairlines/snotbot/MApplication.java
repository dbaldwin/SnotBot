package com.unmannedairlines.snotbot;
import android.content.Context;
import com.secneo.sdk.Helper;

/**
 * Created by db on 10/19/18.
 */

public class MApplication {
    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
    }
}

