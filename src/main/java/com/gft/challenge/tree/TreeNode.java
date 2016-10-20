package com.gft.challenge.tree;

public interface TreeNode<T> extends Iterable<T> {

    T get();
    TreeNode<T> addChild(T t);
    Iterable<TreeNode<T>> getChildren();
    boolean hasChild();
}
