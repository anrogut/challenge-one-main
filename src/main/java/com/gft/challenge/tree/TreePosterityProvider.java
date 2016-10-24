package com.gft.challenge.tree;

import java.util.Iterator;
import java.util.Stack;

class TreePosterityProvider {

    static <T> Iterator<TreeNode<T>> getDescendants(TreeNode<T> root) {
        return new TreeNodeIterator<>(root);
    }

    private static class TreeNodeIterator<T> implements Iterator<TreeNode<T>> {

        private Stack<Iterator<TreeNode<T>>> childrenIterators = new Stack<>();
        private Iterator<TreeNode<T>> currentIterator;

        TreeNodeIterator(TreeNode<T> node) {
            childrenIterators.push(node.getChildrenCollection());
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
                if (node.getChildrenCollection().hasNext()) {
                    childrenIterators.push(node.getChildrenCollection());
                }
                return node;
            }
            return next();
        }

    }
}