package com.gft.challenge.rx;

import com.gft.challenge.tree.PathNode;
import com.gft.challenge.tree.TreeDescendantsProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

final class FileReactiveStream implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(FileReactiveStream.class);

    private final FileSystem fileSystem;
    private Observable<WatchEvent<?>> observable;
    private WatchService watchService;

    FileReactiveStream(FileSystem fileSystem) throws IOException {
        this.fileSystem = fileSystem;
        init();
    }

    private void init() throws IOException {
        watchService = fileSystem.newWatchService();
    }

    Observable<WatchEvent<?>> getEventStream(Path path) throws IllegalStateException {
        if(!registerDirectory(path)) {
            throw new IllegalStateException("Unable to register WatchService for root directory");
        }

        TreeDescendantsProvider.getDescendants(new PathNode(path)).forEachRemaining(pathNode -> {
            if (Files.isDirectory(pathNode.get())) {
                registerDirectory(pathNode.get());
            }
        });

        observable = Observable.fromCallable(() -> {
            WatchKey key = watchService.take();
            List<WatchEvent<?>> events = key.pollEvents();
            events.forEach(watchEvent ->
                    registerNewDirectory(key, watchEvent));
            key.reset();
            return events;
        }).flatMap(Observable::from).subscribeOn(Schedulers.io());
        return observable;
    }

    private void registerNewDirectory(WatchKey key, WatchEvent<?> event) {
        Path path1 = fileSystem.getPath(key.watchable().toString() + fileSystem.getSeparator() + event.context().toString());
        if (Files.isDirectory(path1)) {
            registerDirectory(path1);
        }
    }

    private boolean registerDirectory(Path path) {
        try {
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            return true;
        } catch (IOException e) {
            LOG.warn("Unable to register WatchService for directory: {},", e.getMessage());
            LOG.trace("", e);
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        observable = null;
        watchService.close();
    }

    WatchService getWatchService() {
        return watchService;
    }
}
