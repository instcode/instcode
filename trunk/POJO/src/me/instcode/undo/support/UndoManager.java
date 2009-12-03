package me.instcode.undo.support;

import javax.swing.Action;

import me.instcode.undo.ui.ComboButton;
import me.instcode.undo.ui.JUndoList;

public class UndoManager {
	private ComboButton jUndoButton;
	private ComboButton jRedoButton;

	private UndoSupport support;
	
	public UndoManager(int nLimitUndoLevel) {
		support = new UndoSupport(this);
		support.getManager().setLimit(nLimitUndoLevel);
	}

	public ComboButton getUndoButton() {
		return jUndoButton;
	}

	public ComboButton getRedoButton() {
		return jRedoButton;
	}

	public void createActionButtons(Action undoAction, Action redoAction) {
		jUndoButton = new ComboButton(undoAction);
		jRedoButton = new ComboButton(redoAction);
	}

	public UndoSupport getUndoSupport() {
		return support;
	}
	
	/**
	 * Update Undo/Redo command function
	 */
	public void updateCmdUI() {
		JUndoList jUndoList = (JUndoList) getUndoButton().getPopupComponenet();
		JUndoList jRedoList = (JUndoList) getRedoButton().getPopupComponenet();
		jUndoList.clearSelection();
		if (jUndoList.getModel() != null && jUndoList.getModel().getSize() > 0) {
			jUndoList.setSelectedIndex(0);
		}

		jRedoList.clearSelection();
		if (jRedoList.getModel() != null && jRedoList.getModel().getSize() > 0) {
			jRedoList.setSelectedIndex(0);
		}

		jUndoList.setModel(support.getManager().getUndoList());
		jRedoList.setModel(support.getManager().getRedoList());

		setCmdUI(canUndo(), canRedo());
	}

	private void setCmdUI(boolean canUndo, boolean canRedo) {
		jUndoButton.setEnabled(canUndo);
		jRedoButton.setEnabled(canRedo);
	}

	/**
	 * Clean up
	 */
	public void clear() {
		support.clear();
		updateCmdUI();
	}

	public boolean canRedo() {
		return support.getManager().canRedo();
	}

	public boolean canUndo() {
		return support.getManager().canUndo();
	}

	/**
	 * Undo
	 */
	public void undo() {
		JUndoList jUndoList = (JUndoList) getUndoButton().getPopupComponenet();
		int nItemCount = jUndoList.getMaxSelectionIndex() + 1;
		if (nItemCount <= 0) {
			nItemCount = 1;
		}
		support.getManager().undo(nItemCount);
		setCmdUI(canUndo(), canRedo());
	}

	/**
	 * Redo
	 */
	public void redo() {
		JUndoList jRedoList = (JUndoList) getRedoButton().getPopupComponenet();
		int nItemCount = jRedoList.getMaxSelectionIndex() + 1;
		if (nItemCount <= 0) {
			nItemCount = 1;
		}
		support.getManager().redo(nItemCount);
		setCmdUI(canUndo(), canRedo());
	}
}