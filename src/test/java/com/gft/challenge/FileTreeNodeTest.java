package com.gft.challenge;

import com.gft.challenge.tree.FileTreeNode;
import com.gft.challenge.tree.TreeNode;
import com.gft.challenge.tree.TreePosterityProvider;
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
            new FileTreeNode<>(new File("src/test/java/com/gft/challenge/FileTreeNodeTest.java").getAbsoluteFile())
    );

    @Test
    public void iteratorShouldNotReturnNull() {
        FileTreeNode<File> root = new FileTreeNode<>(new File(ROOT_PATH));

        assertThat(TreePosterityProvider.getPosterity(root)).isNotNull();
    }

    @Test
    public void shouldContainCorrectFiles() {
        Iterator<TreeNode<File>> it = TreePosterityProvider
                .getPosterity(new FileTreeNode<>(new File(ROOT_PATH).getAbsoluteFile()));

        assertThat(it).containsAll(correctList);
    }
}
