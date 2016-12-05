package com.gft.challenge.rx;

import com.gft.challenge.tree.FileNode;
import com.gft.challenge.tree.Node;
import com.gft.challenge.tree.TreeDescendantsProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.io.File;

@Component
class ReactiveTree {

    private ReactiveTree(){}

    @NotNull
    static Observable<Node<File>> getObservableFileStream(FileNode fileNode) {
        return Observable.from(() -> TreeDescendantsProvider.getDescendants(fileNode));
    }
}
