package com.gft.challenge.tree;

import java.util.Iterator;

interface Node<T> {

    T get();
    Iterator<Node<T>> getChildren();
}
