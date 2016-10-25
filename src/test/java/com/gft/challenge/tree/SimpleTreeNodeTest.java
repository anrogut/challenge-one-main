package com.gft.challenge.tree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleTreeNodeTest {

    @Test
    public void shouldReturnEmptyIteratorWhenGivenRootIsNull() {
        assertThat(TreeDescendantsProvider.getDescendants(null)).isNotNull();
        assertThat(TreeDescendantsProvider.getDescendants(null).hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementExceptionWhenGettingNextFromEmptyIterator() {
        TreeDescendantsProvider.getDescendants(null).next();
    }

    @Test
    public void shouldWorkForSimpleOneLevelTree() {
        SimpleNode<Integer> root = new SimpleNode<>(
                0, Arrays.asList(new SimpleNode<>(1),new SimpleNode<>(2)));
        List<Integer> correctIntegerList = Arrays.asList(1, 2);
        List<Integer> rawNodesFromIterator = new ArrayList<>();
        TreeDescendantsProvider.getDescendants(root).forEachRemaining((e) -> rawNodesFromIterator.add(e.get()));

        assertThat(rawNodesFromIterator).containsOnlyElementsOf(correctIntegerList);
    }

    @Test
    public void iteratorShouldIterateThroughStringNodesCorrectly() {
        SimpleNode<String> root = new SimpleNode<>("root");
        root.addChild("child1").addChild("child11").addChild("child111");
        SimpleNode<String> child = root.addChild("child2");
        child.addChild("child21");
        child.addChild("child22");
        child.addChild("child23");
        List<String> correctStringList = Arrays.asList(
                "child1",
                "child11",
                "child111",
                "child2",
                "child21",
                "child22",
                "child23"
        );
        List<String> rawNodesFromIterator = new ArrayList<>();
        TreeDescendantsProvider.getDescendants(root).forEachRemaining((e) -> rawNodesFromIterator.add(e.get()));

        assertThat(rawNodesFromIterator).containsAll(correctStringList);
    }
}
