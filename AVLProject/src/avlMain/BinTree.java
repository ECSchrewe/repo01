package avlMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class BinTree<V extends Comparable<V>> {

	protected BNode<V> root;
	private final BNodeFactory<V> factory;
	private BNodeFactory<V> specialFactory;

	public BinTree() {
		this(null);
	}

	public BinTree(V rootData) {
		factory = new BNodeFactory<>(this);
		root = factory.getNode(rootData);
	}

	public boolean hasSameValues(BinTree<V> other) {
		if (this.root == null && other.root == null)
			return true;
		if (this.root == null || other.root == null)
			return false;
		return this.root.getInOrderValues().equals(other.root.getInOrderValues());
	}

	protected void setFactory(BNodeFactory<V> factory) {
		if (specialFactory == null)
			specialFactory = factory;

	}

	protected BNodeFactory<V> getFactory() {
		return specialFactory == null ? factory : specialFactory;
	}

	public boolean hasSameStructure(BinTree<?> other) {
		if (!(other instanceof BinTree))
			return false;
		return this.getStructureCode().equals(other.getStructureCode());

	}

	public String getStructureCode() {
		if (root == null)
			return "<empty>";
		return root.structureCode().toString();
	}

	public String getParenthesisCode() {
		if (root == null)
			return "<empty>";
		return root.pCode().toString();
	}

	public String getParenthesisCodeAndValues() {
		if (root == null)
			return "<empty>";
		return root.pCodeVal().toString();
	}

	public String printLayers() {
		HashMap<Integer, StringBuilder> layerData = new HashMap<>();
		root.getLayers(layerData, -1);
		System.out.println(layerData);
		String s = "";
		for (Integer i = 0; i < layerData.size(); i++)
			s = s + layerData.get(i).toString() + "\n";
		return s;
	}

	protected LinkedList<BNode<V>> getAncestors(V data) {
		LinkedList<BNode<V>> list = new LinkedList<>();
		BNode<V> current = root;
		boolean loop = true;
		while (loop) {
			list.add(current);
			int comparison = data.compareTo(current.value);
			if (comparison == 0)
				loop = false;

			if (comparison < 0)
				if (current.left == null)
					loop = false;
				else
					current = current.left;

			if (comparison > 0)
				if (current.right == null)
					loop = false;
				else
					current = current.right;

		}
		return list;
	}

	protected BNode<V> getFatherNode(BNode<V> node) {
		return getFatherNode(node.value);
	}

	protected BNode<V> getFatherNode(V val) {
		try {
			LinkedList<BNode<V>> list = getAncestors(val);
			list.removeLast();
			return list.removeLast();
		} catch (RuntimeException e) {

		}
		return null;
	}

	protected BNode<V> getGrandFatherNode(V val) {
		try {
			LinkedList<BNode<V>> list = getAncestors(val);
			list.removeLast();
			list.removeLast();
			return list.removeLast();
		} catch (RuntimeException e) {

		}
		return null;
	}

	public BNode<V> insert(V data) {
		BNode<V> current = root;
		boolean loop = true;
		while (loop) {
			int comparison = data.compareTo(current.value);
			if (comparison == 0)
				loop = false;

			if (comparison < 0) {
				if (current.left == null) {
					current.left = getFactory().getNode(data);
					loop = false;
				} else
					current = current.left;

			}
			if (comparison > 0) {
				if (current.right == null) {
					current.right = getFactory().getNode(data);
					loop = false;
				} else
					current = current.right;
			}
		}
		return current;
	}

	public BNode<V> delete(V data) {
		BNode<V> current = root;
		boolean loop = true;
		while (loop) {
			int comparison = data.compareTo(current.value);
			if (comparison == 0)
				loop = false;
			if (comparison < 0)
				if (current.left == null)
					loop = false;
				else
					current = current.left;

			if (comparison > 0)
				if (current.right == null)
					loop = false;
				else
					current = current.right;
		}
		if (current.value.compareTo(data) != 0)
			return null;

		BNode<V> father = getFatherNode(data);
		if (current.left == null && current.right == null) {
			// node is leaf
			if (father == null) {
				root = null;
				return father;
			} else {
				if (current == father.left)
					father.left = null;
				else
					father.right = null;
				return father;
			}
		}

		// node has one left son
		if (current.left != null && current.right == null) {
			if (father == null) {
				root = current.left;
				return father;
			} else {
				if (current == father.left) {
					father.left = current.left;
					return father;
				} else {
					if (current == father.right) {
						father.right = current.left;
						return father;
					}
				}
			}
		}

		// node has one right son
		if (current.left == null && current.right != null) {
			if (father == null) {
				root = current.right;
				return father;
			} else {
				if (current == father.left) {
					father.left = current.right;
					return father;
				} else {
					if (current == father.right) {
						father.right = current.right;
						return father;
					}
				}
			}
		}
		// node has two sons ...

		List<BNode<V>> list = current.getInOrderNodes();
		int index = list.indexOf(current);

		BNode<V> inOrderNext = list.get(index + 1);
		BNode<V> inOrderNextFather = getFatherNode(inOrderNext);
		if (inOrderNextFather.left == inOrderNext)
			inOrderNextFather.left = null;
		else
			inOrderNextFather.right = null;

		current.value = inOrderNext.value;

		return inOrderNextFather;
	}

	public static void main(String[] args) {
		BinTree<Integer> bt = new BinTree<Integer>(20);
		bt.insert(10);
		bt.insert(15);

		System.out.println(bt.getParenthesisCodeAndValues());
		System.out.println(bt.printLayers());
		System.out.println("\n\n");

	}

	public static BinTree<Integer> getRnd(int n) {
		BinTree<Integer> tree = null;
		HashSet<Integer> set = new HashSet<>();
		int limit = n * 4;
		while (set.size() < n) {
			Integer t = (int) (Math.random() * limit);
			if (set.add(t))
				if (tree == null)
					tree = new BinTree<Integer>(t);
				else
					tree.insert(t);
		}
		return tree;
	}

	class BNode<U extends Comparable<U>> {
		U value;
		BNode<U> left, right;

		BNode(U v) {
			this.value = v;
		}

		int size() {
			int out = 1;
			if (left != null)
				out += left.size();
			if (right != null)
				out += right.size();
			return out;
		}

		BNode<U> find(U targetVal) {
			int comparison = targetVal.compareTo(value);
			if (comparison == 0)
				return this;
			if (comparison < 0 && left != null)
				return left.find(targetVal);
			if (comparison > 0 && right != null)
				return right.find(targetVal);
			return null;
		}

		void getLayers(HashMap<Integer, StringBuilder> inData, Integer parentLevel) {

			Integer myLevel = parentLevel + 1;
			StringBuilder myLevelLine = inData.get(myLevel);
			if (myLevelLine == null) {
				myLevelLine = new StringBuilder();
				inData.put(myLevel, myLevelLine);
			}

			myLevelLine = myLevelLine.append(value.toString() + " ");
			if (left != null)
				left.getLayers(inData, myLevel);
			if (right != null)
				right.getLayers(inData, myLevel);
		}

		List<BNode<U>> getPreOrderNodes() {
			// pre order list of nodes
			ArrayList<BNode<U>> list = new ArrayList<>();
			list.add(this);
			if (left != null)
				list.addAll(left.getPreOrderNodes());
			if (right != null)
				list.addAll(right.getPreOrderNodes());
			return list;
		}

		LinkedList<U> getInOrderValues() {
			LinkedList<U> out = new LinkedList<>();
			if (left != null)
				out.addAll(left.getInOrderValues());
			out.add(value);
			if (right != null)
				out.addAll(right.getInOrderValues());
			return out;
		}

		List<BNode<U>> getInOrderNodes() {
			// pre order list of elements
			ArrayList<BNode<U>> list = new ArrayList<>();
			if (left != null)
				list.addAll(left.getPreOrderNodes());
			list.add(this);
			if (right != null)
				list.addAll(right.getPreOrderNodes());
			return list;
		}

		@Override
		public String toString() {
			return value.toString();
		}

		void parenthesisCode() {
			System.out.print("(");
			if (left != null) {
				left.parenthesisCode();
			}
			System.out.print(" " + this + " ");
			if (right != null) {
				right.parenthesisCode();
			}
			System.out.print(")");
		}

		StringBuilder structureCode() {
			// root/left --> ()
			// right --> []

			return structureCode(new StringBuilder(),
					new String[][] { new String[] { "(", ")" }, new String[] { "[", "]" } }, 0);
		}

		private StringBuilder structureCode(StringBuilder in, String[][] symbols, int leftRightMarker) {
			in.append(symbols[leftRightMarker][0]);
			if (left != null)
				left.structureCode(in, symbols, 0);
			if (right != null)
				right.structureCode(in, symbols, 1);
			in.append(symbols[leftRightMarker][1]);
			return in;
		}

		StringBuilder pCode() {
			return pCode(new StringBuilder());
		}

		StringBuilder pCode(StringBuilder in) {
			in.append("<");
			if (left != null)
				left.pCode(in);
			if (right != null)
				right.pCode(in);
			in.append(">");
			return in;
		}

		StringBuilder pCodeVal() {
			return pCodeVal(new StringBuilder());
		}

		StringBuilder pCodeVal(StringBuilder in) {
			in.append("(");
			if (left != null)
				left.pCodeVal(in);
			in.append(" " + value.toString() + " ");
			if (right != null)
				right.pCodeVal(in);
			in.append(")");
			return in;
		}

	}

}

class BNodeFactory<E extends Comparable<E>> {
	final BinTree<E> tree;

	public BNodeFactory(BinTree<E> tree) {
		this.tree = tree;
	}

	BinTree<E>.BNode<E> getNode(E val) {
		return tree.new BNode<>(val);
	}
}
