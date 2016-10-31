package com.manywho.services.box.test;

import com.manywho.services.box.utilities.SystemInteractionInterface;

import java.io.IOException;

public class SystemInteractionTest implements SystemInteractionInterface{
    @Override
    public String getFileContent(String path) throws IOException {
        return "aaaa";
    }
}
