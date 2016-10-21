package com.gft.challenge.tree;

import java.util.*;

public final class SimpleTreeNode<T> implements TreeNode<T>, Iterable<TreeNode<T>>{

    private T t;
    private List<TreeNode<T>> children;

    public SimpleTreeNode(T t) {
        this.t = t;
        children = new ArrayList<>();
    }

    public SimpleTreeNode(T t, List<TreeNode<T>> children) {
        this.t = t;
        this.children = children;
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        return new SimpleTreeNodeIterator(this);
    }

    public SimpleTreeNode<T> addChild(T t) {
        SimpleTreeNode<T> childNode = new SimpleTreeNode<>(t);
        children.add(childNode);
        return childNode;
    }

    @Override
    public Iterator<TreeNode<T>> getChildrenCollection() {
        return children.iterator();
    }

    private class SimpleTreeNodeIterator implements Iterator<TreeNode<T>> {

        Stack<Iterator<TreeNode<T>>> childrenIterators = new Stack<>();
        Iterator<TreeNode<T>> currentIterator;

        SimpleTreeNodeIterator(TreeNode<T> node) {
            childrenIterators.push(node.getChildrenCollection());
        }

        @Override
        public boolean hasNext() {
            for(Iterator<TreeNode<T>> it : childrenIterators) {
                if(it.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public TreeNode<T> next() {
            if((currentIterator == null || !currentIterator.hasNext())) {
                currentIterator = childrenIterators.peek();
            }
            if(currentIterator.hasNext()) {
                TreeNode<T> node = currentIterator.next();
                if (!currentIterator.hasNext()) {
                    childrenIterators.remove(currentIterator);
                }
                if (node.getChildrenCollection().hasNext()) {
                    childrenIterators.push(node.getChildrenCollection());
                }
                return node;
            }
            return next();
        }

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

    @Override
    public String toString() {
        return "SimpleTreeNode{" +
                "t=" + t +
                '}';
    }
}
