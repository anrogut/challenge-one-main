package com.gft.challenge.tree;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileNode implements Node<File> {

    private File file;

    public FileNode(File file) {
        this.file = file;
    }

    @Override
    public File get() {
        return file;
    }

    @NotNull
    @Override
    public Iterator<Node<File>> getChildren() {
        List<Node<File>> children = new ArrayList<>();
        File[] fileArray = file.listFiles();
        if (fileArray != null) {
            for (File f : fileArray) {
                children.add(new FileNode(f));
            }
        }
        return children.iterator();
    }
}
