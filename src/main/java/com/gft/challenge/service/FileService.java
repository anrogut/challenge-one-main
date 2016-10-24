package com.gft.challenge.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
class FileService {

    public File createFile(String fullPath) throws IOException {
        File newFile = new File(fullPath);
        newFile.createNewFile();
        return newFile;
    }
}
