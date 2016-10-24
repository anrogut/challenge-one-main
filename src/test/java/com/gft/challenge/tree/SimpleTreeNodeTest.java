package com.gft.challenge.tree;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleTreeNodeTest {

    private List<SimpleTreeNode<Integer>> correctIntegerList = Arrays.asList(
            new SimpleTreeNode<>(1),
            new SimpleTreeNode<>(2)
    );
    private List<SimpleTreeNode<String>> correctStringList = Arrays.asList(
            new SimpleTreeNode<>("child1"),
            new SimpleTreeNode<>("child11"),
            new SimpleTreeNode<>("child111"),
            new SimpleTreeNode<>("child2"),
            new SimpleTreeNode<>("child21"),
            new SimpleTreeNode<>("child22"),
            new SimpleTreeNode<>("child23")
    );

    @Test
    public void getPosterityShouldNotReturnNull() {
        SimpleTreeNode<Integer> root = new SimpleTreeNode<>(1);

        assertThat(TreeDescendantsProvider.getDescendants(root)).isNotNull();
        assertThat(TreeDescendantsProvider.getDescendants(root)).isInstanceOf(Iterator.class);
    }

    @Test
    public void shouldWorkForSimpleOneLevelTree() {
        SimpleTreeNode<Integer> root = new SimpleTreeNode<>(
                0, Arrays.asList(new SimpleTreeNode<>(1),new SimpleTreeNode<>(2)));

        assertThat(TreeDescendantsProvider.getDescendants(root).next()).isInstanceOf(SimpleTreeNode.class);
        assertThat(TreeDescendantsProvider.getDescendants(root)).containsOnlyElementsOf(correctIntegerList);
    }

    @Test
    public void iteratorShouldIterateThroughFourStringNodes() {
        SimpleTreeNode<String> root = new SimpleTreeNode<>("root");
        root.addChild("child1").addChild("child11").addChild("child111");
        SimpleTreeNode<String> child = root.addChild("child2");
        child.addChild("child21");
        child.addChild("child22");
        child.addChild("child23");

        assertThat(TreeDescendantsProvider.getDescendants(root)).containsAll(correctStringList);
    }
}
