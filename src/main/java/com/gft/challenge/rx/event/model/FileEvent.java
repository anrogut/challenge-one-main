package com.gft.challenge.rx.event.model;

import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.nio.file.WatchEvent;

public class FileEvent {

    private String eventType;
    private String absolutePath;

    private FileEvent() {
    }

    private FileEvent(String eventType, String absolutePath) {
        this.eventType = eventType;
        this.absolutePath = absolutePath;
    }

    public static FileEvent from(@NotNull WatchEvent<?> watchEvent,@NotNull String parentPath,@NotNull FileSystem fs) {
        return new FileEvent(watchEvent.kind().name(), parentPath +
                fs.getSeparator() + watchEvent.context().toString());
    }

    public static FileEvent empty() {
        return new FileEvent();
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getEventType() {
        return eventType;
    }
}
