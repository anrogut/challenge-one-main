package com.gft.challenge;

import com.gft.challenge.tree.FileTreeNode;
import com.gft.challenge.tree.TreeNode;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTreeNodeTest {

    @Test
    public void iteratorShouldNotReturnNull() {
        FileTreeNode<File> root = new FileTreeNode<>(new File(""));

        assertThat(root.iterator()).isNotNull();
    }
}
