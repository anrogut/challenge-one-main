package com.gft.challenge.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("path")
    public Path get() {
        return path;
    }

    @NotNull
    @Override
    public Iterator<Node<Path>> getChildren() {
        if(Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.list(path)){
                List<Node<Path>> children = stream.map(PathNode::new).collect(Collectors.toList());
                return children.iterator();
            } catch (IOException e) {
                LOG.warn(e.getMessage());
                LOG.trace("",e);
                return Collections.emptyIterator();
            }
        }
        return Collections.emptyIterator();
    }
}
