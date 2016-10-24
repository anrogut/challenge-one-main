package com.gft.challenge.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileServiceTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldCreateFileWithGivenName() throws IOException{
        FileService fileService = new FileService();
        String fileName = "test.txt";
        File createdFile = fileService.createFile(tempFolder.getRoot().getAbsolutePath() + File.separator + fileName);

        assertThat(createdFile.exists());
        assertThat(tempFolder.getRoot().listFiles())
                .contains(new File(tempFolder.getRoot().getAbsolutePath() + File.separator + fileName));
    }

    @Test (expected = IOException.class)
    public void shouldThrowIOException() throws IOException{
        FileService fileService = mock(FileService.class);

        when(fileService.createFile(any())).thenThrow(new IOException());

        then(fileService.createFile(any()));
    }
}
