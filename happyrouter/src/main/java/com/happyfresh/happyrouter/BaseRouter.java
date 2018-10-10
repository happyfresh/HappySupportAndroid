package com.happyfresh.happyrouter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public abstract class BaseRouter {

    protected Intent intent;

    public BaseRouter() {
        intent = new Intent();
    }

    protected Intent create(Context context, Class<? extends Activity> clazz) {
        intent.setComponent(new ComponentName(context, clazz));
        return intent;
    }
}
