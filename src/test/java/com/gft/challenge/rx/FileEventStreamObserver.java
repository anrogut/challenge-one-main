package com.gft.challenge.rx;

import rx.Observer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;

public class FileEventStreamObserver {

    public void observe() throws IOException {
        FileReactiveStream fileReactiveStream = new FileReactiveStream(FileSystems.getDefault());
        fileReactiveStream.getEventStream("C:\\Temp").repeat().toBlocking().subscribe(new Observer<WatchEvent<?>>() {
            @Override
            public void onCompleted() {
                System.out.println("done");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(WatchEvent<?> watchEvent) {
                System.out.println(watchEvent.kind().name() + " : " + watchEvent.context());
            }
        });
    }
}
