package me.instcode.rcp.node.model;

import me.instcode.event.ModifyTracker;

/**
 * This is an implementation of a common hierarchical data
 * structure which stores data in a tree. The implementation
 * also supports notifying modification via event/listeners
 * mechanism.
 *
 * @author dcsnxk
 *
 */
public abstract class NodeModel extends RowBasedModel<Node> {
	private Node root;

	public NodeModel(ModifyTracker tracker) {
		super(tracker);
	}
	
	/**
	 * Get root node.
	 * 
	 * @return
	 */
	public Node getRoot() {
		return root;
	}
	
	/**
	 * Set root node.
	 * 
	 * @param root
	 */
	public void setRoot(Node root) {
		this.root = root;
	}
	
	/**
	 * Add a node to the hierarchical tree.<br>
	 * <br>
	 * Note: Client should provide parent node of
	 * the specified node before calling this method.
	 * 
	 * @param node Node to be added to the tree 	
	 */
	@Override
	public void add(Node node) {
		Node parent = node.getParent();
		parent.add(node);
		fireRowDataAdded(node);
	}
	
	/**
	 * Remove the specified node from the current tree<br>
	 * <br>
	 * Note: Client should provide parent node of
	 * the specified node before calling this method.
	 * 
	 * @param node Node to be added to the tree 	
	 */
	@Override
	public void remove(Node node) {
		Node parent = node.getParent();
		parent.remove(node);
		fireRowDataRemoved(node);
	}
	
	@Override
	public Node[] getAll() {
		return root != null ? root.getChildren() : Node.NO_CHILDREN;
	}
}
