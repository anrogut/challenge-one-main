package com.gft.challenge.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileService {

    private final String path;
    private final FileSystem fileSystem;

    FileService(@Value("${observable.path}") String path, FileSystem fileSystem) {
        this.path = path;
        this.fileSystem = fileSystem;
    }

    public Path createResource(@NotNull String path) throws IOException {
        if(path.endsWith("/")) {
            return createDirectory(path);
        } else {
            return createFile(path);
        }
    }

    private Path createFile(@NotNull String filename) throws IOException {
        return Files.createFile(fileSystem.getPath(this.path + filename));
    }

    private Path createDirectory(@NotNull String path) throws IOException {
        return Files.createDirectories(fileSystem.getPath(this.path + path));
    }
}
