package com.gft.challenge.tree;

import java.util.Collection;
import java.util.Iterator;

public class SimpleTreeNode<T> extends AbstractTreeNode<T> {

    public SimpleTreeNode(T t) {
        super(t);
    }

    @Override
    public Iterator<T> iterator() {
        return new AbstractTreeNodeIterator(this);
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
    public Collection<TreeNode<T>> getChildrenCollection() {
        return children;
    }
}
