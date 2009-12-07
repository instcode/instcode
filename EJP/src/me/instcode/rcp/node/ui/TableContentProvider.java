package me.instcode.rcp.node.ui;

import me.instcode.rcp.node.model.RowBasedModel;
import me.instcode.rcp.node.model.RowDataChangeEvent;
import me.instcode.rcp.node.model.RowDataChangeListener;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;

public class TableContentProvider implements IStructuredContentProvider, RowDataChangeListener {
	private TableViewer viewer;
	private RowBasedModel model;
	
	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;

		if (newInput != oldInput) {
			if (oldInput != null) {
				RowBasedModel model = (RowBasedModel) oldInput;
				model.removeRowDataChangeListener(this);
			}
			if (newInput != null) {
				RowBasedModel model = (RowBasedModel) newInput;
				model.addRowDataChangeListener(this);
			}
		}
		
		model = (RowBasedModel) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return model.getAll();
	}

	@Override
	public void rowDataChanged(final RowDataChangeEvent event) {
		Control control = viewer.getControl();
		if (control.isDisposed() || control.getDisplay().isDisposed()) {
			return;
		}
		
		control.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				switch (event.getType()) {
				case RowDataChangeEvent.ROW_DATA_ADDED_CHANGE:
					rowAdded(event.getData());
					break;
					
				case RowDataChangeEvent.ROW_DATA_REMOVED_CHANGE:
					rowRemoved(event.getData());
					break;
					
				case RowDataChangeEvent.ROW_DATA_MODIFIED_CHANGE:
					rowModified(event.getData());
					break;
				}
			}
		});
	}

	protected void rowAdded(Object data) {
		viewer.add(data);
		viewer.refresh(data);
		viewer.setSelection(new StructuredSelection(new Object[] { data }), true);
	}

	protected void rowRemoved(Object data) {
		// Try to transfer current selection to next item after
		// deleting the given node from the tree.
		TableItem[] selections = viewer.getTable().getSelection();
		Rectangle bounds = selections.length > 0 ? selections[0].getBounds() : new Rectangle(0, 0, 0, 0);
		viewer.remove(data);
		Item next = viewer.getTable().getItem(new Point(bounds.x, bounds.y));
		if (next != null) {
			viewer.setSelection(new StructuredSelection(new Object[] { next.getData() }), true);
		}
		viewer.refresh();
	}

	protected void rowModified(Object data) {
		viewer.refresh();
	}
}
