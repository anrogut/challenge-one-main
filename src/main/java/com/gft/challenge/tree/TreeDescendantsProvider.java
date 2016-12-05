package com.gft.challenge.tree;

import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

final class TreeDescendantsProvider {

    @Contract("null -> !null; !null -> !null")
    static <T> Iterator<Node<T>> getDescendants(Node<T> root) {
        if(root == null) {
            return Collections.emptyIterator();
        }
        return new TreeNodeIterator<>(root);
    }

    private static class TreeNodeIterator<T> implements Iterator<Node<T>> {

        private Stack<Iterator<Node<T>>> childrenIterators = new Stack<>();

        TreeNodeIterator(Node<T> node) {
            if(node.getChildren().hasNext()) {
                childrenIterators.push(node.getChildren());
            }
        }

        @Override
        public boolean hasNext() {
            return !childrenIterators.isEmpty();
        }

        @Override
        public Node<T> next() {
            if(childrenIterators.isEmpty()) {
                throw new NoSuchElementException();
            }
            Node<T> node = childrenIterators.peek().next();
            if(!childrenIterators.peek().hasNext()) {
                childrenIterators.pop();
            }
            if(node.getChildren().hasNext()) {
                childrenIterators.push(node.getChildren());
            }
            return node;
        }
    }
}