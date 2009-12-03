package me.instcode.undo;

import me.instcode.undo.support.UndoEvent;

public interface UndoEventListener {

	void undoExecuted(UndoEvent undoEvent);
}
