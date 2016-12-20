package com.gft.challenge.rx.struct;

import com.gft.challenge.tree.Node;
import com.gft.challenge.tree.PathNode;
import com.gft.challenge.tree.TreeDescendantsProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.nio.file.Path;

@Component
public class DirectoryStructureReactiveStream {

    public Observable<Node<Path>> getDirStructureStream(@NotNull Path path) {
        return Observable.from(() -> TreeDescendantsProvider.getDescendants(new PathNode(path)))
                .subscribeOn(Schedulers.io());
    }
}
