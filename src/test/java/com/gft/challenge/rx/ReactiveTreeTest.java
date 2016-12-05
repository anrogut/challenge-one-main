package com.gft.challenge.rx;

import com.gft.challenge.tree.FileNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import rx.Observable;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ReactiveTreeTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldCreateReactiveStreamFromFileNode() {
        FileNode fileNode = new FileNode(new File(temporaryFolder.getRoot().getAbsolutePath()));

        assertThat(ReactiveTree.getObservableFileStream(fileNode)).isInstanceOf(Observable.class);
    }

    @Test
    public void shouldCreateReactiveStreamEvenFromNullFileNode() {
        assertThat(ReactiveTree.getObservableFileStream(null)).isInstanceOf(Observable.class);
    }
}
