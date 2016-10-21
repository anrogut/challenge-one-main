package com.gft.challenge.tree;

import java.util.Iterator;

public class TreePosterityProvider {

    public static <T> Iterator<T> getPosterity(TreeNode<T> root) {
        return root.iterator();
    }
}
