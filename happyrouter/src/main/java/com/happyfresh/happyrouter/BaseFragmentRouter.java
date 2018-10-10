package com.happyfresh.happyrouter;

import android.content.Intent;
import android.support.annotation.Nullable;

public class BaseFragmentRouter {

    protected Intent intent;

    public BaseFragmentRouter() {
        intent = new Intent();
    }

    @Nullable
    protected <T extends android.app.Fragment> T create(Class<T> clazz) {
        try {
            T fragment = clazz.newInstance();
            fragment.setArguments(intent.getExtras());
            return fragment;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    protected <T extends android.support.v4.app.Fragment> T createV4(Class<T> clazz) {
        try {
            T fragment = clazz.newInstance();
            fragment.setArguments(intent.getExtras());
            return fragment;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
