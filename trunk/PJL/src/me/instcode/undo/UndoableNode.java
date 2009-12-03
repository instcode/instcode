package me.instcode.undo;

import me.instcode.undo.Node;

public class UndoableNode implements Node {
	private String key;
	private Object value;
	
	public UndoableNode() {
	}
	
	public UndoableNode(String key, Object value) {
		this.key = key;
		this.value = value; 
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public boolean equals(Node node) {
		return (node instanceof UndoableNode) && (getKey().equals(node.getKey()) &&
				(value.equals(((UndoableNode)node).value)));
	}
	
	@Override
	public String toString() {
		return value == null ? "" : getValue().toString();
	}
}
