package com.github.eschrewe.treeproject;

import java.util.ArrayList;

public class AVLTree<T extends Comparable<T>> extends BinTree<T> {

	final int maxDiff = 2;
	boolean verbose = true;

	AVLTree(T rootData) {
		super(rootData);
		setFactory(new AVLNodeFactory<>(this));
		root = getFactory().getNode(rootData);
	}

	@Override
	public
	AVLNode<T> insert(T data) {
		AVLNode<T> node = (AVLTree<T>.AVLNode<T>) super.insert(data);
		rebalance(node);
		return node;
	}

	@Override
	public
	AVLNode<T> delete(T data) {
		AVLNode<T> node = (AVLTree<T>.AVLNode<T>) super.delete(data);
		rebalance(node);
		return node;
	}

	protected void rebalance(AVLNode<T> node) {
		int bFactor = node.balanceFactor();
		AVLNode<T> father = (AVLTree<T>.AVLNode<T>) getFatherNode(node);
		if (Math.abs(bFactor) <= maxDiff) {
			if (father != null)
				rebalance(father);
			return;
		}
		if (verbose)
			System.out.println("rebalancing " + node.value.toString());
		AVLNode<T> next = null;
		if (bFactor < 0) {
			// right side deeper
			AVLNode<T> son = (AVLTree<T>.AVLNode<T>) node.right;
			if (son.balanceFactor() > 0) {
				// right-left double rotation needed
				AVLNode<T> grandSon = (AVLTree<T>.AVLNode<T>) son.left;
				son.left = grandSon.right;
				if (node == root) {
					root = grandSon;
				} else {
					if (node == father.left) {
						father.left = grandSon;
					} else {
						father.right = grandSon;
					}
				}
				node.right = grandSon.left;
				grandSon.right = son;
				grandSon.left = node;
				next = grandSon;
			} else {
				// simple right rotation
				if (node == root) {
					root = son;
				} else {
					if (node == father.left) {
						father.left = son;
					} else {
						father.right = son;
					}
				}
				node.right = son.left;
				son.left = node;
				next = son;
			}
		} else {
			// left side deeper
			AVLNode<T> son = (AVLTree<T>.AVLNode<T>) node.left;
			if (son.balanceFactor() < 0) {
				// double rotation needed
				AVLNode<T> grandSon = (AVLTree<T>.AVLNode<T>) son.right;
				son.right = grandSon.left;
				if (node == root) {
					root = grandSon;
				} else {
					if (node == father.left) {
						father.left = grandSon;
					} else {
						father.right = grandSon;
					}
				}
				node.left = grandSon.right;
				grandSon.left = son;
				grandSon.right = node;
				next = grandSon;
			} else {
				// simple left rotation
				if (node == root) {
					root = son;
				} else {
					if (node == father.left)
						father.left = son;
					else
						father.right = son;
				}
				node.left = son.right;
				son.right = node;
				next = son;
			}
		}
		rebalance(next);

	}

	public static void main(String[] args) {

//		BinTree<Integer> b = new BinTree<Integer>(0);
//		b.insert(1);
//		
//		System.out.println(b.printLayers());
//		System.out.println(b.getStructureCode());
//		
//		System.out.println(b.hasSameStructure(t));
//		System.out.println(t.hasSameStructure(b));

		AVLTree<Integer> t = getRandom(10);
		System.out.println(t.getStructureCode());
	}

	public static AVLTree<Integer> getRandom(int n) {
		ArrayList<Integer> list = new ArrayList<>();
		Integer i = (int) (Math.random() * n * 4);
		AVLTree<Integer> out = new AVLTree<>(i);
		out.verbose = false;
		list.add(i);
		while (list.size() < n) {
			i = (int) (Math.random() * n * 4);
			if (!list.contains(i)) {
				out.insert(i);
				list.add(i);
			}
		}
		System.out.println("insert order of generated tree: ");
		System.out.println(list);
		return out;
	}

	class AVLNode<U extends Comparable<U>> extends BinTree<U>.BNode<U> {

		AVLNode(U v) {
			super(v);
		}

		int balanceFactor() {
			if (left == null && right == null)
				return 0;
			int leftBalance = 0, rightBalance = 0;
			if (left != null)
				leftBalance = ((AVLTree<U>.AVLNode<U>) left).getHeight();
			if (right != null)
				rightBalance = ((AVLTree<U>.AVLNode<U>) right).getHeight();
			return leftBalance - rightBalance;

		}

		int getHeight() {
			if (left == null && right == null)
				return 1;
			int leftHeight = 1, rightHeight = 1;
			if (left != null)
				leftHeight = ((AVLTree<U>.AVLNode<U>) left).getHeight();
			if (right != null)
				rightHeight = ((AVLTree<U>.AVLNode<U>) right).getHeight();
			return 1 + Math.max(leftHeight, rightHeight);
		}

	}

}

class AVLNodeFactory<A extends Comparable<A>> extends BNodeFactory<A> {

	AVLNodeFactory(BinTree<A> tree) {
		super(tree);
	}

	@Override
	AVLTree<A>.AVLNode<A> getNode(A val) {
		return ((AVLTree<A>) tree).new AVLNode<>(val);
	}

}
