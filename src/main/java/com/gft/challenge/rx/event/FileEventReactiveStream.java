package com.gft.challenge.rx.event;

import com.gft.challenge.rx.event.model.FileEvent;
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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@SessionScope
public class FileEventReactiveStream implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(FileEventReactiveStream.class);

    private final FileSystem fileSystem;
    private WatchService watchService;

    @Autowired
    public FileEventReactiveStream(FileSystem fileSystem) throws IOException {
        this.fileSystem = fileSystem;
    }

    /**
     * Should be called before getting event stream
     *
     * @throws IOException when unable to create WatchService
     */
    @PostConstruct
    public void init() throws IOException {
        this.watchService = fileSystem.newWatchService();
    }

    public Observable<FileEvent> getEventStream(@NotNull Path path) throws IOException {
        if (!registerDirectoryForCreateAndDeleteWatch(path)) {
            throw new IOException("Unable to register WatchService for root directory");
        }

        TreeDescendantsProvider.getDescendants(new PathNode(path)).forEachRemaining(pathNode -> {
            if (Files.isDirectory(pathNode.get())) {
                registerDirectoryForCreateAndDeleteWatch(pathNode.get());
            }
        });

        return Observable.fromCallable(() -> {
            WatchKey key = watchService.take();
            List<WatchEvent<?>> events = key.pollEvents();
            events.forEach(watchEvent ->
                    registerNewDirectoryForCreateAndDeleteWatch(key, watchEvent));
            List<FileEvent> fileEvents = events.stream()
                    .map(e -> FileEvent.from(e, key.watchable().toString(), fileSystem)).collect(Collectors.toList());
            key.reset();
            return fileEvents;
        }).flatMap(Observable::from).subscribeOn(Schedulers.io()).repeat();
    }

    private void registerNewDirectoryForCreateAndDeleteWatch(@NotNull WatchKey key, @NotNull WatchEvent<?> event) {
        Path path1 = fileSystem.getPath(key.watchable().toString() + fileSystem.getSeparator() + event.context().toString());
        if (Files.isDirectory(path1)) {
            registerDirectoryForCreateAndDeleteWatch(path1);
        }
    }

    private boolean registerDirectoryForCreateAndDeleteWatch(@NotNull Path path) {
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
    }

    public WatchService getWatchService() {
        return watchService;
    }
}
