package com.vasyl.emergencyalert;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class MyApplication extends android.app.Application {

    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "QGemkZs1GXIMbmyPJKNX2mIQLSIvb9qdDdx3F0Is", "SdPhvxVuVF4PpzSZJ81dUOgxWfNoAUfCWvsexRFL");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
