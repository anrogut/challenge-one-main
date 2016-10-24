package com.gft.challenge.tree;

import java.util.*;

final class SimpleTreeNode<T> implements TreeNode<T>{

    private T t;
    private List<TreeNode<T>> children;

    SimpleTreeNode(T t) {
        this.t = t;
        children = new ArrayList<>();
    }

    SimpleTreeNode(T t, List<TreeNode<T>> children) {
        this.t = t;
        this.children = children;
    }

    SimpleTreeNode<T> addChild(T t) {
        SimpleTreeNode<T> childNode = new SimpleTreeNode<>(t);
        children.add(childNode);
        return childNode;
    }

    @Override
    public Iterator<TreeNode<T>> getChildrenCollection() {
        return children.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTreeNode<?> that = (SimpleTreeNode<?>) o;

        return t != null ? t.equals(that.t) : that.t == null;

    }

    @Override
    public int hashCode() {
        return t != null ? t.hashCode() : 0;
    }
}
