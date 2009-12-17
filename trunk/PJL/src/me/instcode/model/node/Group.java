package me.instcode.model.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Supports node with children. 
 * 
 * @author dcsnxk
 *
 */
public class Group extends Node {
	private List<Node> nodes;
	
	public Group(Node parent) {
		super(parent);
		nodes = new ArrayList<Node>(5);
	}
	
	public Group() {
		this(null);
	}
	
	@Override
	public void add(Node child) {
		nodes.add(child);
	}
	
	@Override
	public boolean remove(Node child) {
		return nodes.remove(child);
	}
	
	@Override
	public Node[] getChildren() {
		return nodes.toArray(new Node[nodes.size()]);
	}
	
	@Override
	public boolean hasChildren() {
		return nodes.size() > 0;
	}
}
