package com.gft.challenge.rx;

import com.gft.challenge.tree.PathNode;
import com.gft.challenge.tree.TreeDescendantsProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@SessionScope
public class FileEventReactiveStream implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(FileEventReactiveStream.class);

    private final FileSystem fileSystem;
    private Observable<FileEvent> observable;
    private WatchService watchService;

    @Autowired
    public FileEventReactiveStream(FileSystem fileSystem) throws IOException {
        this.fileSystem = fileSystem;
        init();
    }

    private void init() throws IOException {
        watchService = fileSystem.newWatchService();
    }

    public Observable<FileEvent> getEventStream(@NotNull Path path) throws IOException {
        if (!registerDirectory(path)) {
            throw new IOException("Unable to register WatchService for root directory");
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
            List<FileEvent> fileEvents = events.stream()
                    .map(e -> FileEvent.from(e, key.watchable().toString(), fileSystem)).collect(Collectors.toList());
            key.reset();
            return fileEvents;
        }).flatMap(Observable::from).subscribeOn(Schedulers.io()).repeat();
        return observable;
    }

    private void registerNewDirectory(WatchKey key, WatchEvent<?> event) {
        Path path1 = fileSystem.getPath(key.watchable().toString() + fileSystem.getSeparator() + event.context().toString());
        if (Files.isDirectory(path1)) {
            registerDirectory(path1);
        }
    }

    private boolean registerDirectory(@NotNull Path path) {
        try {
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            return true;
        } catch (IOException e) {
            LOG.warn("Unable to register WatchService for directory: {}", e.getMessage());
            LOG.trace("", e);
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        watchService.close();
        observable = null;
    }

    WatchService getWatchService() {
        return watchService;
    }
}
