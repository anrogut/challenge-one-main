package com.gft.challenge;

import com.gft.challenge.tree.SimpleTreeNode;
import com.gft.challenge.tree.TreeNode;
import com.gft.challenge.tree.TreePosterityProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeNodeTest {

    private List<Integer> correctList = Arrays.asList(1,12,13,24);

    @Test
    public void getPosterityShouldReturnNotNull() {
        TreeNode<Integer> root = new SimpleTreeNode<>(1);

        assertThat(TreePosterityProvider.getPosterity(root)).isNotNull();
        assertThat(TreePosterityProvider.getPosterity(root)).isInstanceOf(Iterator.class);
        assertThat(TreePosterityProvider.getPosterity(root)).containsOnly(1);
    }

    @Test
    public void iteratorShouldIterateThroughThreeNodes() {
        TreeNode<Integer> root = new SimpleTreeNode<>(1);
        TreeNode<Integer> child = root.addChild(12);
        root.addChild(13);
        child.addChild(24);

        assertThat(root).containsAll(correctList);
    }
}
