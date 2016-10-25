package com.gft.challenge.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
class FileService {

    File createFile(String fullPath) throws IOException {
        File newFile = new File(fullPath);
        if (newFile.createNewFile()) {
            return newFile;
        } else {
            throw new IOException("File not created");
        }
    }
}
