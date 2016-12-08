package com.gft.challenge.rx;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class FileReactiveStreamTest {

    @Test(timeout = 10000)
    public void shouldCorrectlyGetCreateEventFromNestedDirectoryObservable() throws IOException {
        TestSubscriber<WatchEvent<?>> testSubscriber = TestSubscriber.create();
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path home = fs.getPath("C:/home");
        Files.createDirectory(home);
        Path temp = Files.createDirectory(fs.getPath("C:/home/test"));
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fs);
        Observable<WatchEvent<?>> observable = fileReactiveStream.getEventStream(home.toString());
        observable.subscribe(testSubscriber);

        Files.createFile(temp.resolve("hello.txt"));

        testSubscriber.awaitValueCount(1, 5, TimeUnit.SECONDS);
        WatchEvent<?> event = testSubscriber.getOnNextEvents().get(0);
        assertThat(event).isNotNull();
        assertThat(event.kind().name()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE.name());
        assertThat(event.context().toString()).isEqualTo("hello.txt");
    }

    @Test
    public void shouldCorrectlyGetFileCreateEventFromNewlyCreatedDirectory() throws IOException, InterruptedException {
        TestSubscriber<WatchEvent<?>> testSubscriber = TestSubscriber.create();
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path home = fs.getPath("/home");
        Files.createDirectory(home);
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fs);
        Observable<WatchEvent<?>> observable = fileReactiveStream.getEventStream(home.toString()).repeat(2);
        observable.subscribe(testSubscriber);

        Path test = Files.createDirectory(home.resolve("test"));
        testSubscriber.awaitValueCount(1, 5, TimeUnit.SECONDS);
        Files.createFile(test.resolve("temp.txt"));

        testSubscriber.awaitValueCount(2, 5, TimeUnit.SECONDS);
        List<WatchEvent<?>> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(2);
        assertThat(events.get(1).kind().name()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE.name());
        assertThat(events.get(1).context().toString()).isEqualTo("temp.txt");
    }
}
