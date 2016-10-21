package com.gft.challenge.tree;

import java.util.Collection;

public interface TreeNode<T> extends Iterable<T> {

    T get();
    TreeNode<T> addChild(T t);
    Collection<TreeNode<T>> getChildrenCollection();
}
