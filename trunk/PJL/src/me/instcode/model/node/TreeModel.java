package me.instcode.model.node;

import me.instcode.event.ModifyTracker;
import me.instcode.model.RowModelChangeEvent;
import me.instcode.model.StructuredModel;

/**
 * This is an implementation of a common hierarchical data
 * structure which stores data in a tree. The implementation
 * also supports notifying modification via event/listeners
 * mechanism.
 *
 * @author khoanguyen
 *
 */
public abstract class TreeModel extends StructuredModel<Node> {
	private Node root;

	public TreeModel(ModifyTracker tracker) {
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
	 * Set root node for this tree.
	 * 
	 * @param root
	 */
	public void setRoot(Node root) {
		this.root = root;
	}
	
	/**
	 * {@inheritDoc} <br>
	 * Client should provide parent node of the specified node before calling
	 * this method. Otherwise, the root node will be used.
	 * 
	 * @param node
	 *            Node to be added to the tree
	 */
	@Override
	public void add(Node node) {
		Node parent = node.getParent();
		if (parent == null) {
			parent = root;
			node.setParent(parent);
		}
		parent.add(node);
		fireChanged(node, RowModelChangeEvent.ROW_ADDED);
	}
	
	/**
	 * {@inheritDoc}
	 * <br>
	 * @note Client should provide parent node of
	 * the specified node before calling this method.
	 * 
	 * @param node Node to be added to the tree
	 */
	@Override
	public void remove(Node node) {
		Node parent = node.getParent();
		parent.remove(node);
		fireChanged(node, RowModelChangeEvent.ROW_REMOVED);
	}
	
	@Override
	public void clear() {
		root.clear();
		fireChanged(this, RowModelChangeEvent.MODEL_RESET);
	}
}
