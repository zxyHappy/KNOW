package com.bluemsun.know.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDUtil {
    public static String get() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
