package com.gft.challenge.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTreeNode<T> implements TreeNode<T>, Iterable<T>{

    protected T thisNode;
    protected List<TreeNode<T>> children;

    protected AbstractTreeNode(T t) {
        thisNode = t;
        children = new LinkedList<>();
    }

    class AbstractTreeNodeIterator implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return children.iterator().hasNext();
        }

        @Override
        public T next() {
            return children.iterator().next().get();
        }
    }

    @Override
    public String toString() {
        return "AbstractTreeNode{" +
                "thisNode=" + thisNode +
                '}';
    }
}
