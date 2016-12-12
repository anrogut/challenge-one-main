package com.gft.challenge.rx;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.WatchServiceConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileReactiveStreamTest {

    private FileSystem fileSystem;
    private Path home;

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix()
                .toBuilder()
                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(10, TimeUnit.MILLISECONDS))
                .build());
        home = fileSystem.getPath("/home");
        Files.createDirectory(home);
    }

    @After
    public void cleanUp() throws IOException {
        fileSystem.close();
    }

    @Test
    public void shouldCorrectlyGetCreateEventFromNestedDirectoryObservable() throws IOException {
        TestSubscriber<WatchEvent<?>> testSubscriber = TestSubscriber.create();
        Path temp = Files.createDirectory(fileSystem.getPath("/home/test"));
        Files.createFile(fileSystem.getPath("/home/file.txt"));
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fileSystem);
        Observable<WatchEvent<?>> observable = fileReactiveStream.getEventStream(home);
        observable.subscribe(testSubscriber);

        Files.createFile(temp.resolve("hello.txt"));
        testSubscriber.awaitValueCount(1, 5000, TimeUnit.MILLISECONDS);

        testSubscriber.assertValueCount(1);
        WatchEvent<?> event = testSubscriber.getOnNextEvents().get(0);
        assertThat(event).isNotNull();
        assertThat(event.kind().name()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE.name());
        assertThat(event.context().toString()).isEqualTo("hello.txt");
    }

    @Test
    public void shouldCorrectlyGetFileCreateEventFromNewlyCreatedDirectory() throws IOException, InterruptedException {
        TestSubscriber<WatchEvent<?>> testSubscriber = TestSubscriber.create();
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fileSystem);
        Observable<WatchEvent<?>> observable = fileReactiveStream.getEventStream(home).repeat(2);
        observable.subscribe(testSubscriber);

        Path test = Files.createDirectory(home.resolve("test"));
        testSubscriber.awaitValueCount(1, 5000, TimeUnit.MILLISECONDS);
        testSubscriber.assertValueCount(1);
        Files.createFile(test.resolve("temp.txt"));
        testSubscriber.awaitValueCount(2, 5000, TimeUnit.MILLISECONDS);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(2);
        List<WatchEvent<?>> events = testSubscriber.getOnNextEvents();
        assertThat(events.get(1).kind().name()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE.name());
        assertThat(events.get(1).context().toString()).isEqualTo("temp.txt");
    }

    @Test
    public void shouldCloseCorrectly() throws Exception {
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fileSystem);
        fileReactiveStream.close();

        assertThatExceptionOfType(ClosedWatchServiceException.class)
                .isThrownBy(() -> fileReactiveStream.getWatchService().take());
    }

    @Test
    public void shouldThrowIOExceptionWhenCannotRegisterWatchServiceForRootPath() throws IOException {
        Path home = mock(Path.class);
        when(home.register(any(WatchService.class), eq(StandardWatchEventKinds.ENTRY_CREATE), eq(StandardWatchEventKinds.ENTRY_DELETE)))
                .thenThrow(new IOException());

        FileReactiveStream fileReactiveStream = new FileReactiveStream(fileSystem);

        assertThatExceptionOfType(IOException.class).isThrownBy(() -> fileReactiveStream.getEventStream(home));
    }

}
