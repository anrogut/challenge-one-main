package com.gft.challenge.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class FileService {

    private String path = "";

    FileService(@Value("${observable.path}") String path) {
        this.path = path;
    }

    public boolean createFile(@NotNull String fileName) throws IOException {
        File newFile = new File(path + File.separator + fileName);

        return newFile.createNewFile();
    }
}
