package com.gft.challenge.rx;

import com.gft.challenge.rx.event.FileEventReactiveStream;
import com.gft.challenge.rx.event.FileEventReactiveStreamObserver;
import com.gft.challenge.rx.struct.DirStructureReactiveStream;
import com.gft.challenge.rx.struct.DirStructureReactiveStreamObserver;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import rx.Subscription;

import java.io.IOException;
import java.nio.file.FileSystem;

@Component
@SessionScope
public class SubscriptionHandler implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionHandler.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileSystem fileSystem;

    private final FileEventReactiveStream fileEventReactiveStream;
    private final DirStructureReactiveStream dirStructureReactiveStream;

    private Subscription fileEventSubscription;

    private FileEventReactiveStreamObserver observer;

    @Autowired
    public SubscriptionHandler(SimpMessagingTemplate simpMessagingTemplate,
                               FileSystem fileSystem,
                               FileEventReactiveStream fileEventReactiveStream,
                               DirStructureReactiveStream dirStructureReactiveStream) throws IOException {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.fileSystem = fileSystem;
        this.fileEventReactiveStream = fileEventReactiveStream;
        this.dirStructureReactiveStream = dirStructureReactiveStream;
        LOG.info("Successfully created handler: {}", this);
    }

    public Subscription observeDirectory(@NotNull String path, int endpointId) throws IOException {
        if (fileEventSubscription != null) {
            return fileEventSubscription;
        }
        observer = new FileEventReactiveStreamObserver(simpMessagingTemplate, endpointId);
        fileEventSubscription = fileEventReactiveStream.getEventStream(fileSystem.getPath(path))
                .subscribe(observer);
        return fileEventSubscription;
    }

    public void sendDirectoryStructure(@NotNull String path, int endpointId) throws IOException {
        DirStructureReactiveStreamObserver dirObserver = new DirStructureReactiveStreamObserver(simpMessagingTemplate, endpointId);
        dirStructureReactiveStream.getDirStructureStream(fileSystem.getPath(path))
                .subscribe(dirObserver);
    }


    Subscription getSubscription() {
        return fileEventSubscription;
    }

    @Override
    public void close() throws Exception {
        fileEventSubscription.unsubscribe();
        LOG.info("Successfully unsubscribed from reactive stream: {}", this);
    }
}
