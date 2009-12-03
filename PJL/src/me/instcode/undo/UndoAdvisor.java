package me.instcode.undo;

public interface UndoAdvisor<T extends Node> {
	/**
	 * Restore the given value to the given node
	 * 
	 * @param node
	 * @param value
	 */
	void restore(T node);
}
