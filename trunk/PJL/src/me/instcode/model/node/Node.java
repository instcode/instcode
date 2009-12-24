package me.instcode.model.node;

/**
 * Node, without children.
 * 
 * @author khoanguyen
 *
 */
public class Node {
	public static final Node[] NO_CHILDREN = new Node[0];
	private Node parent;

	public Node(Node parent) {
		this.parent = parent;
	}

	/**
	 * Get all direct children of this node.
	 * 
	 * @return
	 */
	public Node[] getChildren() {
		return NO_CHILDREN;
	}

	/**
	 * Add child node
	 * 
	 * @param child
	 */
	public void add(Node child) {
	}
	
	/**
	 * Remove child node
	 * 
	 * @param child
	 */
	public boolean remove(Node child) {
		return true;
	}
	
	/**
	 * Clear all children
	 */
	public void clear() {
	}
	
	/**
	 * @return parent node
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * @return true if this node has children
	 */
	public boolean hasChildren() {
		return false;
	}
}
