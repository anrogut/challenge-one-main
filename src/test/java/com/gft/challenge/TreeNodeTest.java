package com.gft.challenge;

import com.gft.challenge.tree.SimpleTreeNode;
import com.gft.challenge.tree.TreeNode;
import com.gft.challenge.tree.TreePosterityProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeNodeTest {

    @Before
    public void setUp() {
    }

    @Test
    public void getPosterityShouldReturnNotNull() {
        TreeNode<Integer> root = new SimpleTreeNode<>(1);
        TreeNode<Integer> child = root.addChild(2);
        root.addChild(3);
        child.addChild(4);

        assertThat(TreePosterityProvider.getPosterity(root)).isNotNull();
    }

    @Test
    public void iteratorShouldIterateThroughThreeNodes() {
        TreeNode<Integer> root = new SimpleTreeNode<>(1);
        TreeNode<Integer> child = root.addChild(2);
        root.addChild(3);
        child.addChild(4);
    }
}
