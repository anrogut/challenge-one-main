package com.gft.challenge.tree;

import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

class TreeDescendantsProvider {


    @Contract("null -> !null; !null -> !null")
    static <T> Iterator<Node<T>> getDescendants(Node<T> root) {
        if(root == null) {
            return Collections.emptyIterator();
        }
        return new TreeNodeIterator<>(root);
    }

    private static class TreeNodeIterator<T> implements Iterator<Node<T>> {

        private Stack<Iterator<Node<T>>> childrenIterators = new Stack<>();
        private Iterator<Node<T>> currentIterator;

        TreeNodeIterator(Node<T> node) {
            childrenIterators.push(node.getChildren());
        }

        @Override
        public boolean hasNext() {
            for(Iterator<Node<T>> it : childrenIterators)
                if (it.hasNext()) {
                    return true;
                }
            return false;
        }

        @Override
        public Node<T> next() {
            if((currentIterator == null || !currentIterator.hasNext())) {
                currentIterator = childrenIterators.peek();
            }
            if(currentIterator.hasNext()) {
                Node<T> node = currentIterator.next();
                if (!currentIterator.hasNext()) {
                    childrenIterators.remove(currentIterator);
                }
                if (node.getChildren().hasNext()) {
                    childrenIterators.push(node.getChildren());
                }
                return node;
            }
            return next();
        }

    }
}