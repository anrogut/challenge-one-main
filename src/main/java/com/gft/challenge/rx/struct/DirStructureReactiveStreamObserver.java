package com.gft.challenge.rx.struct;

import com.gft.challenge.tree.Node;
import com.gft.challenge.tree.PathNode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observer;

import java.nio.file.Path;

public class DirStructureReactiveStreamObserver implements Observer<Node<Path>> {

    private static final String TOPIC_DIR = "/topic/dir/";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final int endpointId;

    public DirStructureReactiveStreamObserver(SimpMessagingTemplate simpMessagingTemplate, int endpointId) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.endpointId = endpointId;
    }

    @Override
    public void onCompleted() {
        simpMessagingTemplate
                .convertAndSend(TOPIC_DIR + endpointId, "done");
    }

    @Override
    public void onError(Throwable e) {
        simpMessagingTemplate
                .convertAndSend(TOPIC_DIR + endpointId, e.getMessage() == null ? "Error" : e.getMessage());
    }

    @Override
    public void onNext(Node<Path> node) {
        simpMessagingTemplate
                .convertAndSend(TOPIC_DIR + endpointId, node);
    }
}
