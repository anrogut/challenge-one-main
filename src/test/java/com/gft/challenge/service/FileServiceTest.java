package com.gft.challenge.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class FileServiceTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldCreateFileWithGivenName() {
        FileService fileService = new FileService();
        String fileName = "test.txt";
        File createdFile = fileService.createFile(tempFolder.getRoot().getAbsolutePath() + File.separator + fileName);

        assertThat(createdFile.exists());
        assertThat(tempFolder.getRoot().listFiles())
                .contains(new File(tempFolder.getRoot().getAbsolutePath() + File.separator + fileName));
    }
}
