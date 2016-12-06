package com.gft.challenge.rx;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import rx.Observable;
import rx.subjects.ReplaySubject;

import java.io.IOException;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FileReactiveStreamTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldObserverObservable() throws IOException {
        ReplaySubject<WatchEvent<?>> testSubscriber = ReplaySubject.create();
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path home = fs.getPath("/home");
        Files.createDirectory(fs.getPath("/home"));
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fs);

        Observable<WatchEvent<?>> observable = fileReactiveStream.getEventStream(home.toString());
        observable.subscribe(testSubscriber);

        Files.createFile(home.resolve("hello.txt"));

        WatchEvent<?> event = testSubscriber.toBlocking().first();
        assertThat(event).isNotNull();
        assertThat(event.kind().name()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE.name());
        assertThat(event.context().toString()).isEqualTo("hello.txt");
    }


}
