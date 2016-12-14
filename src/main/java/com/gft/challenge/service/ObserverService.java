package com.gft.challenge.service;

import com.gft.challenge.rx.FileReactiveStream;
import com.gft.challenge.rx.FileReactiveStreamObserver;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import rx.Subscription;

import java.io.IOException;
import java.nio.file.FileSystem;

@Service
public class ObserverService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileSystem fileSystem;

    public ObserverService(SimpMessagingTemplate simpMessagingTemplate, FileSystem fileSystem) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.fileSystem = fileSystem;
    }

    public Subscription observeDirectory(String path) throws IOException {
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fileSystem);
        return fileReactiveStream.getEventStream(fileSystem.getPath(path))
                .subscribe(new FileReactiveStreamObserver(simpMessagingTemplate));
    }
}
