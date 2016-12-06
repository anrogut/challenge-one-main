package com.gft.challenge.rx;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.*;

final class FileReactiveStream {

    private final FileSystem fileSystem;
    private WatchService watchService;

    FileReactiveStream(FileSystem fileSystem) throws IOException {
        this.fileSystem = fileSystem;
        init();
    }

    private void init() throws IOException {
        watchService = fileSystem.newWatchService();
    }

    // TODO: 12/6/2016 add recursive registering for all directories in given path
    Observable<WatchEvent<?>> getEventStream(String path) throws IOException {
        Path path1 = fileSystem.getPath(path);
        path1.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

        return Observable.fromCallable(() -> {
            WatchKey key = watchService.take();
            return key.pollEvents();
        }).flatMap(Observable::from).subscribeOn(Schedulers.io());
    }
}
