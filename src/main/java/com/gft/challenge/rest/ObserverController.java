package com.gft.challenge.rest;

import com.gft.challenge.rx.FileReactiveStream;
import com.gft.challenge.rx.FileReactiveStreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Paths;

@RestController
public class ObserverController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileSystem fileSystem;

    @Autowired
    public ObserverController(SimpMessagingTemplate simpMessagingTemplate, FileSystem fileSystem) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.fileSystem = fileSystem;
    }

    @GetMapping("/connect")
    public ResponseEntity<Void> connect(@Value("${observable.path}") String path) throws IOException {
        FileReactiveStream fileReactiveStream = new FileReactiveStream(fileSystem);
        fileReactiveStream.getEventStream(Paths.get(path)).repeat()
                .subscribe(new FileReactiveStreamObserver(simpMessagingTemplate));
        return ResponseEntity.ok().build();
    }
}
