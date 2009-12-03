package me.instcode.undo.support;

import me.instcode.undo.Node;


public class UndoEvent {

	private Node node;
	
	public UndoEvent(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
}
