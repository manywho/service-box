package com.manywho.services.box.utilities;

import java.util.Base64;

public class Base64Helper {
    public static String encode(String plainText) {
        return new String(Base64.getEncoder().encode(plainText.getBytes()));
    }

    public static String decode(String encodedText) {
        return new String(Base64.getDecoder().decode(encodedText));
    }
}