package com.gft.challenge.tree;

import java.util.Iterator;

interface TreeNode<T> {

    Iterator<TreeNode<T>> getChildrenCollection();
}
