package com.happyfresh.happytracker;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.Log;

public class Adapter {

    private Context context;

    protected void onIdentify(Properties properties) {
        String message = "Provider adapter on identify\n" + properties.toString();
        Log.d(getClass().getName(), message);
    }

    protected void onEvent(String event, Properties properties) {
        String message = "Provider adapter on event " + event + "\n" + properties.toString();
        Log.d(getClass().getName(), message);
    }

    protected void onSaveProperties(Properties properties) {

    }

    @Nullable
    protected Context getContext() {
        return this.context;
    }

    void setContext(Context context) {
        this.context = context;
    }
}
