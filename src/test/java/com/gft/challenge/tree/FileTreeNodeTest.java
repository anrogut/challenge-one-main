package com.gft.challenge.tree;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTreeNodeTest {

    private static final String ROOT_PATH = "src";

    private List<FileTreeNode<File>> correctList = Arrays.asList(
            new FileTreeNode<>(new File("src/main/java/com/gft/challenge/tree/TreeNode.java").getAbsoluteFile()),
            new FileTreeNode<>(new File("src/test/java/com/gft/challenge/tree/FileTreeNodeTest.java").getAbsoluteFile()),
            new FileTreeNode<>(new File("src/main/resources/application.yml").getAbsoluteFile())
    );

    @Test
    public void iteratorShouldNotReturnNull() {
        FileTreeNode<File> root = new FileTreeNode<>(new File(ROOT_PATH));

        assertThat(TreeDescendantsProvider.getDescendants(root)).isNotNull();
    }

    @Test
    public void shouldContainCorrectFiles() {
        Iterator<TreeNode<File>> it = TreeDescendantsProvider
                .getDescendants(new FileTreeNode<>(new File(ROOT_PATH).getAbsoluteFile()));

        assertThat(it).containsAll(correctList);
    }
}
