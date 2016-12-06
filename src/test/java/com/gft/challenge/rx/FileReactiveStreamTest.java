package com.gft.challenge.rx;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import rx.Observable;
import rx.subjects.ReplaySubject;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import static org.assertj.core.api.Assertions.assertThat;

public class FileReactiveStreamTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldObserverObservable() throws IOException {
        ReplaySubject<WatchEvent<?>> testSubscriber = ReplaySubject.create();
        FileReactiveStream fileReactiveStream = new FileReactiveStream(FileSystems.getDefault());
        Observable<WatchEvent<?>> observable = fileReactiveStream.getEventStream(temporaryFolder.getRoot().getAbsolutePath());
        observable.subscribe(testSubscriber);

        temporaryFolder.newFolder("test");

        WatchEvent<?> event = testSubscriber.toBlocking().first();
        assertThat(event).isNotNull();
        assertThat(event.kind().name()).isEqualTo(StandardWatchEventKinds.ENTRY_CREATE.name());
        assertThat(event.context().toString()).isEqualTo("test");
    }


}
