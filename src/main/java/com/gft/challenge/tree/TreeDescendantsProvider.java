package com.gft.challenge.tree;

import java.util.Iterator;
import java.util.Stack;

class TreeDescendantsProvider {

    public static <T> Iterator<TreeNode<T>> getDescendants(TreeNode<T> root) {
        return new TreeNodeIterator<>(root);
    }

    private static class TreeNodeIterator<T> implements Iterator<TreeNode<T>> {

        private Stack<Iterator<TreeNode<T>>> childrenIterators = new Stack<>();
        private Iterator<TreeNode<T>> currentIterator;

        TreeNodeIterator(TreeNode<T> node) {
            childrenIterators.push(node.getChildren());
        }

        @Override
        public boolean hasNext() {
            for(Iterator<TreeNode<T>> it : childrenIterators) {
                if(it.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public TreeNode<T> next() {
            if((currentIterator == null || !currentIterator.hasNext())) {
                currentIterator = childrenIterators.peek();
            }
            if(currentIterator.hasNext()) {
                TreeNode<T> node = currentIterator.next();
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