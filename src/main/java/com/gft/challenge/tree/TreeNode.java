package com.gft.challenge.tree;

import java.util.Iterator;

public interface TreeNode<T> {

    Iterator<TreeNode<T>> getChildrenCollection();
}
