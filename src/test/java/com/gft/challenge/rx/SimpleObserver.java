package com.gft.challenge.rx;

import com.gft.challenge.tree.FileNode;
import com.gft.challenge.tree.Node;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import rx.Observer;

import java.io.File;
import java.io.IOException;

public class SimpleObserver {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldObserverObservable() throws IOException {
        temporaryFolder.newFolder("1");
        temporaryFolder.newFile("1.txt");
        temporaryFolder.newFolder("2");

        FileNode fileNode = new FileNode(new File(temporaryFolder.getRoot().getAbsolutePath()));
        FileReactiveStream.getObservableFileStream(fileNode).filter(fileNode1 -> fileNode1.get().isDirectory())
                .subscribe(new Observer<Node<File>>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Done");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Node<File> fileNode) {
                        System.out.println(fileNode.get().getAbsolutePath());
                    }
                });
    }
}
