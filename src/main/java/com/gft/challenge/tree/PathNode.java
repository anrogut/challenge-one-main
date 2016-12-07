package com.gft.challenge.tree;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PathNode implements Node<Path> {

    private Path path;

    public PathNode(Path path) {
        this.path = path;
    }

    @Override
    public Path get() {
        return path;
    }

    @NotNull
    @Override
    public Iterator<Node<Path>> getChildren() {
        List<Node<Path>> children = null;
        try {
            children = Files.walk(path, 1).map(PathNode::new).collect(Collectors.toList());
            // Children at least contains one path which is the root path. We need to remove it!
            children.remove(0);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyIterator();
        }
        return children.iterator();
    }
}
