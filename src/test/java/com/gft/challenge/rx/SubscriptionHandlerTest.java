package com.gft.challenge.rx;

import com.gft.challenge.rx.event.FileEventReactiveStream;
import com.gft.challenge.rx.struct.DirectoryStructureReactiveStream;
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
        FileEventReactiveStream fileEventReactiveStream =  new FileEventReactiveStream(fs);
        SubscriptionHandler subscriptionHandler = new SubscriptionHandler(mock(SimpMessagingTemplate.class),
                fs, fileEventReactiveStream, new DirectoryStructureReactiveStream());
        fileEventReactiveStream.init();

        Subscription subscription = subscriptionHandler.observeDirectory(rootPath.toString(), 1);

        assertThat(subscription).isNotNull();
        assertThat(subscription.isUnsubscribed()).isFalse();
    }

    @Test
    public void shouldReturnSameInstanceOfSubscriptionWhenCalledManyTimes() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fs.getPath("/home");
        Files.createDirectory(rootPath);
        FileEventReactiveStream fileEventReactiveStream =  new FileEventReactiveStream(fs);
        SubscriptionHandler subscriptionHandler = new SubscriptionHandler(mock(SimpMessagingTemplate.class),
                fs, fileEventReactiveStream, new DirectoryStructureReactiveStream());
        fileEventReactiveStream.init();

        Subscription subscriptionOne = subscriptionHandler.observeDirectory(rootPath.toString(), 1);
        Subscription subscriptionTwo = subscriptionHandler.observeDirectory(rootPath.toString(), 1);

        assertThat(subscriptionOne).isSameAs(subscriptionTwo);
    }

    @Test
    public void shouldUnsubscribeCorrectly() throws Exception {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path rootPath = fs.getPath("/home");
        Files.createDirectory(rootPath);
        FileEventReactiveStream fileEventReactiveStream =  new FileEventReactiveStream(fs);
        SubscriptionHandler subscriptionHandler = new SubscriptionHandler(mock(SimpMessagingTemplate.class),
                fs, fileEventReactiveStream, new DirectoryStructureReactiveStream());
        fileEventReactiveStream.init();

        subscriptionHandler.observeDirectory("/home", 1);

        assertThat(subscriptionHandler.getSubscription().isUnsubscribed()).isFalse();
        subscriptionHandler.close();
        assertThat(subscriptionHandler.getSubscription().isUnsubscribed()).isTrue();
    }
}
