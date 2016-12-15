package com.gft.challenge.service;

import com.gft.challenge.rx.FileEventReactiveStream;
import com.gft.challenge.rx.SubscriptionHandler;
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
public class SubscriptionHandlerTest {

    @Test
    public void shouldStartObservingDirectory() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fs.getPath("/home");
        Files.createDirectory(rootPath);

        SubscriptionHandler subscriptionHandler = new SubscriptionHandler(mock(SimpMessagingTemplate.class),
                fs, new FileEventReactiveStream(fs));

        Subscription subscription = subscriptionHandler.observeDirectory(rootPath.toString(),"1");

        assertThat(subscription).isNotNull();
        assertThat(subscription.isUnsubscribed()).isFalse();
    }

    @Test
    public void shouldReleaseResourcesCorrectly() throws Exception {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fs.getPath("/home");
        Files.createDirectory(rootPath);

        SubscriptionHandler subscriptionHandler = new SubscriptionHandler(mock(SimpMessagingTemplate.class),
                fs, new FileEventReactiveStream(fs));

        subscriptionHandler.observeDirectory("/home","1");

        assertThat(subscriptionHandler.getSubscriptions()).hasSize(1);
        Subscription subscription = subscriptionHandler.getSubscriptions().get(0);
        subscriptionHandler.close();
        assertThat(subscriptionHandler.getSubscriptions()).isEmpty();
        assertThat(subscription.isUnsubscribed()).isTrue();
    }
}
