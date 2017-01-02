package com.gft.challenge.service;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class FileServiceTest {

    @Test
    public void shouldCreateFileWithGivenName() throws IOException {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path testPath = fileSystem.getPath("/home/");
        Files.createDirectory(testPath);
        FileService fileService = new FileService("/home/", fileSystem);

        Path createdFilePath = fileService.createResource("1/file");

        assertThat(Files.exists(createdFilePath)).isTrue();
        assertThat(Files.isDirectory(createdFilePath)).isFalse();
    }

    @Test
    public void shouldCreateDirectoryWithGivenName() throws IOException {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path testPath = fileSystem.getPath("/home/");
        Files.createDirectory(testPath);
        FileService fileService = new FileService("/home/", fileSystem);

        Path createdDirPath = fileService.createResource("dir/dir2/dir3/");

        assertThat(Files.exists(createdDirPath)).isTrue();
        assertThat(Files.isDirectory(createdDirPath)).isTrue()
        ;
    }

}
