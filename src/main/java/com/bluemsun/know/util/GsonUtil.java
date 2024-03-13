package com.bluemsun.know.util;

import org.springframework.stereotype.Component;
import com.google.gson.Gson;

@Component
public class GsonUtil {

    private static final Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

}
