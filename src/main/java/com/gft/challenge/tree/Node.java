package com.gft.challenge.tree;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Iterator;

public interface Node<T> {

    T get();

    @NotNull
    Iterator<Node<T>> getChildren();
}
