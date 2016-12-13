package com.gft.challenge.websocket;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

class TestWatchEvent implements WatchEvent<Path> {

    @Override
    public Kind<Path> kind() {
        return StandardWatchEventKinds.ENTRY_CREATE;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Path context() {
        return Paths.get("/test");
    }
}
