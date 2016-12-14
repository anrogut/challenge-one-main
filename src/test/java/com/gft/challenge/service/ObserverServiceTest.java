package com.gft.challenge.service;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Subscription;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ObserverServiceTest {

    @Test
    public void shouldStartObservingDirectory() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fs.getPath("/home");
        Files.createDirectory(rootPath);

        ObserverService observerService = new ObserverService(mock(SimpMessagingTemplate.class), fs);

        Subscription subscription = observerService.observeDirectory(rootPath.toString());

        assertThat(subscription).isNotNull();
        assertThat(subscription.isUnsubscribed()).isFalse();
    }
}
