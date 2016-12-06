package com.gft.challenge.rx;

import rx.Subscriber;

import java.nio.file.WatchEvent;

public class FileReactiveStreamObserver extends Subscriber<WatchEvent<?>> {
    @Override
    public void onCompleted() {
        System.out.println("done");
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onNext(WatchEvent<?> watchEvent) {
        System.out.println(watchEvent.kind().name());
    }
}
