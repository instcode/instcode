package me.instcode.undo.support;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import me.instcode.undo.Node;

public class UndoableEdit extends AbstractUndoableEdit {
	private String undo;
	private String redo;
	
	private UndoSupport support;
	private Node node;

	public String getUndoPresentationName() {
		return undo;
	}
	
	public String getRedoPresentationName() {
		return redo;
	}
	
	public String getPresentationName() {
		return "UndoableEdit";
	}

	public UndoableEdit(UndoSupport support, Node node, String undoName, String redoName) {
		this.support = support;
		this.node = node;
		this.undo = undoName;
		this.redo = redoName;
	}

	/* (non-Javadoc)
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		try {
			support.restore(node);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CannotUndoException();
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		try {
			support.restore(node);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CannotRedoException();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.undo.UndoableEdit#isSignificant()
	 */
	public boolean isSignificant() {
		return node != null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.undo.UndoableEdit#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.undo.UndoableEdit#canRedo()
	 */
	public boolean canRedo() {
		return true;
	}
}