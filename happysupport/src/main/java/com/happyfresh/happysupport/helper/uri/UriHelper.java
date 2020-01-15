package com.happyfresh.happysupport.helper.uri;

import android.net.Uri;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class UriHelper {

    public static void parse(Object target, Uri uri) {
        Method[] methods = target.getClass().getMethods();
        if (methods == null || methods.length == 0) {
            return;
        }

        List<String> UriPathSegmentsActual = uri.getPathSegments();

        for (Method method : methods) {
            if (!method.isAnnotationPresent(UriParse.class)) {
                continue;
            }

            UriParse uriParse = method.getAnnotation(UriParse.class);
            String UriPathExpected = uriParse.value();
            String[] UriPathSegmentsExpected = UriPathExpected.split("/");

            if (UriPathSegmentsActual.size() != UriPathSegmentsExpected.length) {
                continue;
            }

            boolean isMatch = true;
            Annotation[][] annotations = method.getParameterAnnotations();
            Object[] inputParameters = new Object[annotations.length];
            int parameterIndex = 0;
            for (int i = 0; i < UriPathSegmentsActual.size(); i++) {
                String actual = UriPathSegmentsActual.get(i);
                String expected = UriPathSegmentsExpected[i];

                if (expected.startsWith("{") && expected.endsWith("}")) {
                    Annotation annotation = annotations[parameterIndex][0];
                    if (!(annotation instanceof UriPath)) {
                        continue;
                    }

                    UriPath uriPath = (UriPath) annotation;
                    if (!expected.equals("{".concat(uriPath.value()).concat("}"))) {
                        continue;
                    }

                    inputParameters[parameterIndex++] = actual;
                }
                else if (!actual.equals(expected)) {
                    isMatch = false;
                    break;
                }
            }

            if (isMatch) {
                try {
                    method.invoke(target, inputParameters);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
