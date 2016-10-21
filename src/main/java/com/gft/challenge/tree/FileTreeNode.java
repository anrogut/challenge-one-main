package com.gft.challenge.tree;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public final class FileTreeNode<T extends File> implements TreeNode<T>, Iterable {

    private T t;
    private List<TreeNode<T>> children;

    public FileTreeNode(T t) {
        this.t = t;
    }

    @Override
    public Iterator<TreeNode<T>> getChildrenCollection() {

        return null;
    }

    @Override
    public Iterator iterator() {

        return new FileTreeNodeIterator();
    }

    class FileTreeNodeIterator implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            return null;
        }
    }
}
