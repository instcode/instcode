package me.instcode.undo.support;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import me.instcode.undo.Node;

public class EditUndoManager extends UndoManager {
	private UndoableEditSupport undoSupport = new UndoableEditSupport();
	private UndoSupport support;

	private UndoListModel<String> undoList = new UndoListModel<String>();
	private UndoListModel<String> redoList = new UndoListModel<String>();

	public EditUndoManager(UndoSupport support) {
		this.support = support;
		undoSupport.addUndoableEditListener(this);
	}

	public UndoListModel<String> getUndoList() {
		return undoList;
	}
	
	public UndoListModel<String> getRedoList() {
		return redoList;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoManager#setLimit(int)
	 */
	public void setLimit(int nLimit) {
		super.setLimit(nLimit);
		undoList.setLimit(nLimit);
		redoList.setLimit(nLimit);
	}

	public void postEdit(Node node, String undoName, String redoName) {
		UndoableEdit editUndo = new UndoableEdit(support, node, undoName, redoName);
		if (editUndo.isSignificant()) {
			// Remove all items from redo list
			if (canRedo()) {
				redoList.removeAll();
			}
			undoSupport.postEdit(editUndo);
			undoList.addFirst(editUndo.getUndoPresentationName());
		}
	}

	/**
	 * Undo up to specified steps
	 * 
	 * @param steps
	 */
	public void undo(int steps) {
		for (int i = 0; i < steps; i++) {
			redoList.addFirst(editToBeUndone().getRedoPresentationName());
			undo();
		}
		undoList.removeTo(steps);
	}

	/**
	 * Redo up to specified steps
	 * 
	 * @param steps
	 */
	public void redo(int steps) {
		for (int i = 0; i < steps; i++) {
			undoList.addFirst(editToBeRedone().getUndoPresentationName());
			redo();
		}
		redoList.removeTo(steps);
	}
	
	public void clear() {
		discardAllEdits();
		undoList.removeAll();
		redoList.removeAll();
	}
}