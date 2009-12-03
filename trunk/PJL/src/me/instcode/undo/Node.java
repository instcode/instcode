package me.instcode.undo;

public interface Node {

	String getKey();
	
	Object getValue();
	
	void setValue(Object value);
	
	/**
	 * @param node
	 * @return
	 */
	boolean equals(Node node);
}
