package com.gft.challenge.rx;

import com.gft.challenge.tree.PathNode;
import com.gft.challenge.tree.TreeDescendantsProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observer;

import java.nio.file.Path;

public class FileEventReactiveStreamObserver implements Observer<FileEvent> {

    private static final String TOPIC_EVENT = "/topic/event/";
    private static final String TOPIC_DIR = "/topic/dir/";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final int endpointId;

    public FileEventReactiveStreamObserver(SimpMessagingTemplate simpMessagingTemplate, int endpointId) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.endpointId = endpointId;
    }

    @Override
    public void onCompleted() {
        simpMessagingTemplate
                .convertAndSend(TOPIC_EVENT + endpointId, "done");
    }

    @Override
    public void onError(Throwable e) {
        simpMessagingTemplate
                .convertAndSend(TOPIC_EVENT + endpointId, e.getMessage() == null ? "Error" : e.getMessage());
    }

    @Override
    public void onNext(FileEvent event) {
        simpMessagingTemplate
                .convertAndSend(TOPIC_EVENT + endpointId, event);
    }

    public void sendDirectoryStructure(Path path) {
        TreeDescendantsProvider.getDescendants(new PathNode(path))
                .forEachRemaining(node -> simpMessagingTemplate.convertAndSend(TOPIC_DIR + endpointId, node));
    }
}
