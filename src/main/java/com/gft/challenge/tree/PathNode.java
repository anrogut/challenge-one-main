package com.gft.challenge.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathNode implements Node<Path> {

    private static final Logger LOG = LoggerFactory.getLogger(PathNode.class);
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
        try (Stream<Path> stream = Files.walk(path,1)){
            children = stream.map(PathNode::new).collect(Collectors.toList());
            // Children at least contains one path which is the root path. We need to remove it!
            children.remove(0);
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            LOG.trace("",e);
            return Collections.emptyIterator();
        }
        return children.iterator();
    }
}
