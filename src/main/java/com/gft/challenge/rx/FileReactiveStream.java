package com.gft.challenge.rx;

import com.gft.challenge.tree.PathNode;
import com.gft.challenge.tree.TreeDescendantsProvider;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

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

    @NotNull
    Observable<WatchEvent<?>> getEventStream(String path) throws IOException {
        Path rootPath = fileSystem.getPath(path);
        registerDirectory(rootPath);

        TreeDescendantsProvider.getDescendants(new PathNode(rootPath)).forEachRemaining(pathNode -> {
            if (Files.isDirectory(pathNode.get())) {
                try {
                    registerDirectory(pathNode.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return Observable.fromCallable(() -> {
            WatchKey key = watchService.take();
            key.reset();
            List<WatchEvent<?>> events = key.pollEvents();
            events.forEach(watchEvent ->
                registerNewDirectory(key,watchEvent));
            key.watchable();
            return events;
        }).flatMap(Observable::from).subscribeOn(Schedulers.io());
    }

    private void registerNewDirectory(WatchKey key, WatchEvent<?> event) {
        Path path1 = fileSystem.getPath(key.watchable().toString() + fileSystem.getSeparator() + event.context().toString());
        if(Files.isDirectory(path1)) {
            try {
                registerDirectory(path1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerDirectory(Path path) throws IOException {
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
    }
}
