package com.gft.challenge.tree;

import org.jetbrains.annotations.Contract;

import java.util.*;

public final class TreeDescendantsProvider {

    private TreeDescendantsProvider(){}

    @Contract("null -> !null; !null -> !null")
    public static <T> Iterator<Node<T>> getDescendants(Node<T> root) {
        if (root == null) {
            return Collections.emptyIterator();
        }
        return new TreeNodeIterator<>(root);
    }

    private static class TreeNodeIterator<T> implements Iterator<Node<T>> {

        private Deque<Iterator<Node<T>>> childrenIterators = new LinkedList<>();

        TreeNodeIterator(Node<T> node) {
            if (node.getChildren().hasNext()) {
                childrenIterators.push(node.getChildren());
            }
        }

        @Override
        public boolean hasNext() {
            return !childrenIterators.isEmpty();
        }

        @Override
        public Node<T> next() {
            if (childrenIterators.isEmpty()) {
                throw new NoSuchElementException();
            }
            Node<T> node = childrenIterators.peek().next();
            if (!childrenIterators.peek().hasNext()) {
                childrenIterators.pop();
            }
            if (node.getChildren().hasNext()) {
                childrenIterators.push(node.getChildren());
            }
            return node;
        }
    }
}