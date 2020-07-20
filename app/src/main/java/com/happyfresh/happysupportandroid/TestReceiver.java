package com.happyfresh.happysupportandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.happyfresh.happyrouter.annotations.Extra;

public class TestReceiver extends BroadcastReceiver {

    @Extra(key = "order_number")
    public String orderNumber;

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
