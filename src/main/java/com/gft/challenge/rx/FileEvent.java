package com.gft.challenge.rx;

import java.nio.file.FileSystem;
import java.nio.file.WatchEvent;

public class FileEvent {

    private String eventType;
    private String absolutePath;

    public FileEvent() {
    }

    private FileEvent(String eventType, String absolutePath) {
        this.eventType = eventType;
        this.absolutePath = absolutePath;
    }

    static FileEvent from(WatchEvent<?> watchEvent, String parentPath, FileSystem fs) {
        return new FileEvent(watchEvent.kind().name(), parentPath +
                fs.getSeparator() + watchEvent.context().toString());
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
