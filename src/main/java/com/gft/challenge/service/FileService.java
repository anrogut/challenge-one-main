package com.gft.challenge.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
class FileService {

    File createFile(String fullPath) throws IOException {
        File newFile = new File(fullPath);
        Path path = Paths.get(fullPath);
        Files.createFile(path);

        return newFile;
    }
}
