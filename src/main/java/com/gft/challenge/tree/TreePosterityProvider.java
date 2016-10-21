package com.gft.challenge.tree;

import java.util.Iterator;

public class TreePosterityProvider {

    public static <T> Iterator<TreeNode<T>> getPosterity(SimpleTreeNode<T> root) {
        return root.iterator();
    }
}