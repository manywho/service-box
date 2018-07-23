package com.manywho.services.box.utilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SystemInteraction implements SystemInteractionInterface {

    public String getFileContent(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
