package com.gft.challenge.rx;

import com.gft.challenge.tree.PathNode;
import com.gft.challenge.tree.TreeDescendantsProvider;
import org.jetbrains.annotations.NotNull;
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

    @NotNull Observable<WatchEvent<?>> getEventStream(String path) throws IOException {
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
            return key.pollEvents();
        }).flatMap(Observable::from).subscribeOn(Schedulers.io()).repeat();
    }

    private void registerDirectory(Path path) throws IOException {
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
    }
}
