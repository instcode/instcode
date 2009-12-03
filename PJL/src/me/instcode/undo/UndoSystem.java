package me.instcode.undo;

import java.util.HashMap;
import java.util.Map;

import me.instcode.undo.support.UndoManager;


/**
 * <pre>
 *	Action undoAction = new AbstractAction("Undo", undoIcon) {
 *		public void actionPerformed(ActionEvent e) {
 *			// ...
 *			// Request focus
 *			// ...
 *			undoSystem.executeUndo();
 *		}
 *	};
 *	Action redoAction = new AbstractAction("Redo", redoIcon) {
 *		public void actionPerformed(ActionEvent e) {
 *			// ...
 *			// Request focus
 *			// ...
 *			undoSystem.executeRedo();
 *		}
 *	};
 *
 *	UndoManager manager = new UndoManager(1000);
 *	manager.createActionButtons(undoAction, redoAction);
 *	manager.getUndoButton().setPreferredSize(new Dimension(40, 24));
 *	manager.getRedoButton().setPreferredSize(new Dimension(40, 24));
 *	
 *	toolbar.add(manager.getUndoButton());
 *	toolbar.add(manager.getRedoButton());
 *		
 *	UndoSupport undoSupport = manager.getUndoSupport();
 *	UndoAdvisorProvider provider = new UndoAdvisorProvider();
 *
 *	MyModel model = new MyModel();
 *	MyUndoAdvisor advisor = new MyUndoAdvisor();
 *
 *	provider.registerAdvisor(MyUndoableNode.class, advisor);
 *	undoSupport.addUndoEventListener(advisor);
 *	undoSupport.setUndoAdvisorProvider(provider);
 *
 *	model.attachUndoable(undoSupport);
 *
 *	manager.updateCmdUI();
 *	undoSystem.registerUndoManager("myModel", manager);
 *	undoSystem.setActiveUndoManager("myModel");
 * </pre>
 *
 */
public class UndoSystem {
	private Map<String, UndoManager> managers = new HashMap<String, UndoManager>();
	private String activeUndoManager = null;

	public void setActiveUndoManager(String id) {
		this.activeUndoManager = id;
	}
	
	public UndoManager unregisterUndoManager(String id) {
		return managers.remove(id);
	}

	public void registerUndoManager(String id, UndoManager manager) {
		managers.put(id, manager);
	}

	public UndoManager getUndoManager(String id) {
		return (id == null) ? null : managers.get(id);
	}

	public UndoManager getActiveUndoManager() {
		return getUndoManager(activeUndoManager);
	}

	/**
	 * Perform an undo action
	 */
	public void executeUndo() {
		UndoManager manager = getActiveUndoManager();
		if (manager != null) {
			manager.undo();
		}
	}

	/**
	 * Perform an redo action
	 */
	public void executeRedo() {
		UndoManager manager = getActiveUndoManager();
		if (manager != null) {
			manager.redo();
		}
	}
}