package com.gft.challenge.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FileTreeNode<T extends File> implements TreeNode<T> {

    private File t;

    public FileTreeNode(File t) {
        this.t = t;
    }

    @Override
    public Iterator<TreeNode<T>> getChildrenCollection() {
        List<TreeNode<T>> children = new ArrayList<>();
        File[] fileArray = t.listFiles();
        if (fileArray != null) {
            for(File f : fileArray) {
                children.add(new FileTreeNode<>(f));
            }
        }
        return children.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileTreeNode<?> that = (FileTreeNode<?>) o;

        return t != null ? t.equals(that.t) : that.t == null;
    }

    @Override
    public int hashCode() {
        return t != null ? t.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FileTreeNode{" +
                "t=" + t.getAbsolutePath() +
                '}';
    }
}
