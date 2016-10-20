package com.gft.challenge.tree;

import java.util.Iterator;

public class SimpleTreeNode<T> extends AbstractTreeNode<T> {

    public SimpleTreeNode(T t) {
        super(t);
    }

    @Override
    public Iterator<T> iterator() {
        return new AbstractTreeNodeIterator();
    }

    @Override
    public T get() {
        return thisNode;
    }

    @Override
    public TreeNode<T> addChild(T t) {
        TreeNode<T> childNode = new SimpleTreeNode<>(t);
        children.add(childNode);
        return childNode;
    }

    @Override
    public Iterable<TreeNode<T>> getChildren() {
        return children;
    }

    @Override
    public boolean hasChild() {
        return !children.isEmpty();
    }
}
