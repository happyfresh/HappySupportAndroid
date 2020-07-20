package com.happyfresh.happysupport.helper;

import android.content.Context;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class StringHelper {

    @NonNull
    public static String getString(@NonNull Context context, @NonNull @StringRes int stringResId, Object[]... objects) {
        String text;
        if (objects.length == 0) {
            text = context.getString(stringResId);
        }
        else {
            text = context.getString(stringResId, objects[0]);
        }

        if (objects.length > 1) {
            return getString(context, text, Arrays.copyOfRange(objects, 1, objects.length));
        }
        else {
            return getString(context, text);
        }
    }

    public static String getString(@NonNull Context context, @NonNull String text, Object[]... objects) {
        Pattern pattern = Pattern.compile("\\{@\\w*\\/\\w*\\}");
        Matcher matcher = pattern.matcher(text);
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
                text = text.replace(s, colorHex);
            }
            else {
                if (objects.length > 0) {
                    text = text.replace(s, getString(context, resId, objects));
                }
                else {
                    text = text.replace(s, getString(context, resId));
                }
            }
        }

        return text;
    }

    @NonNull
    public static String[] getStringArray(@NonNull Context context, @ArrayRes int stringResId,
                                          Object[][]... objects) {
        String[] text = context.getResources().getStringArray(stringResId);

        if (text.length > 0) {
            for (int i = 0; i < text.length; i++) {
                if (objects.length > i) {
                    text[i] = getString(context, text[i], objects[i]);
                }
                else {
                    text[i] = getString(context, text[i]);
                }
            }
        }

        return text;
    }
}
