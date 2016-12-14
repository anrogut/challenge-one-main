package com.gft.challenge.tree;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class SimpleNode<T> implements Node<T> {

    private T t;
    private List<Node<T>> children;

    SimpleNode(T t) {
        this(t, new ArrayList<>());
    }

    SimpleNode(T t, List<Node<T>> children) {
        this.t = t;
        this.children = children;
    }

    SimpleNode<T> addChild(T t) {
        SimpleNode<T> childNode = new SimpleNode<>(t);
        children.add(childNode);
        return childNode;
    }

    @Override
    public T get() {
        return t;
    }

    @NotNull
    @Override
    public Iterator<Node<T>> getChildren() {
        return children.iterator();
    }
}
