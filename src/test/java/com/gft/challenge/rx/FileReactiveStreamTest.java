package com.gft.challenge.rx;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import rx.Observable;
import rx.subjects.ReplaySubject;

import java.io.IOException;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FileReactiveStreamTest {

    @Test(timeout = 10000)
    public void shouldCorrectlyGetCreateEventFromNestedDirectoryObservable() throws IOException {
        ReplaySubject<WatchEvent<?>> testSubscriber = ReplaySubject.create();
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path home = fs.getPath("C:\\home");
        Files.createDirectory(home);
        Path temp = Files.createDirectory(fs.getPath("C:\\home\\test"));
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fs);
        Observable<WatchEvent<?>> observable = fileReactiveStream.getEventStream(home.toString());
        observable.subscribe(testSubscriber);

        Files.createFile(temp.resolve("hello.txt"));

        WatchEvent<?> event = testSubscriber.toBlocking().first();
        assertThat(event).isNotNull();
        assertThat(event.kind().name()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE.name());
        assertThat(event.context().toString()).isEqualTo("hello.txt");
    }
}
