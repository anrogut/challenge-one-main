package com.gft.challenge.tree;

import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleNodeTest {

    @Test
    public void shouldReturnEmptyIteratorWhenGivenRootIsNull() {
        assertThat(TreeDescendantsProvider.getDescendants(null)).isNotNull();
        assertThat(TreeDescendantsProvider.getDescendants(null)).isEmpty();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementExceptionWhenGettingNextFromEmptyIterator() {
        TreeDescendantsProvider.getDescendants(null).next();
    }

    @Test
    public void shouldReturnEmptyIteratorWhenRootHasNoChildren() {
        SimpleNode<Integer> root = new SimpleNode<>(0, Collections.emptyList());

        assertThat(TreeDescendantsProvider.getDescendants(root)).isEmpty();
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
