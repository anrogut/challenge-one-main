package com.gft.challenge.tree;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class FileNode implements Node<File> {

    private File t;

    FileNode(File t) {
        this.t = t;
    }

    @Contract(pure = true)
    @Override
    public File get() {
        return t;
    }

    @NotNull
    @Override
    public Iterator<Node<File>> getChildren() {
        List<Node<File>> children = new ArrayList<>();
        File[] fileArray = t.listFiles();
        if (fileArray != null) {
            for(File f : fileArray) {
                children.add(new FileNode(f));
            }
        }
        return children.iterator();
    }
}
