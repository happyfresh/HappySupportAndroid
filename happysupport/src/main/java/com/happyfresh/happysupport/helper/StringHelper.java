package com.happyfresh.happysupport.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

    @NonNull
    public static String getString(@NonNull Context context, @NonNull @StringRes int stringResId, Object[]... objects) {
        String value;
        if (objects.length == 0) {
            value = context.getString(stringResId);
        }
        else {
            value = context.getString(stringResId, objects[0]);
        }

        Pattern pattern = Pattern.compile("\\{@\\w*\\/\\w*\\}");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String s = matcher.group(0);
            String sResId = s.replace("{", "").replace("}", "");
            String resType = sResId.substring(1, sResId.indexOf("/")).toLowerCase();
            int resId = context.getResources().getIdentifier(sResId, resType, context.getPackageName());

            if (resId == 0) {
                continue;
            }

            if (resType.equals("color")) {
                String colorHex = context.getString(resId);
                colorHex = colorHex.substring(0, 1).concat(colorHex.substring(3));
                value = value.replace(s, colorHex);
            }
            else {
                if (objects.length > 1) {
                    value = value.replace(s, getString(context, resId, Arrays.copyOfRange(objects, 1, objects.length)));
                }
                else {
                    value = value.replace(s, getString(context, resId));
                }
            }
        }

        return value;
    }

}
