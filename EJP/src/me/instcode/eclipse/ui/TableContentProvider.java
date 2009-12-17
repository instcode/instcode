package me.instcode.eclipse.ui;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;
import me.instcode.model.RowBasedModel;
import me.instcode.model.RowDataChangeEvent;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;

public class TableContentProvider implements IStructuredContentProvider, ModifyListener {
	private TableViewer viewer;
	private RowBasedModel model;

	@Override
	public void dataModified(final ModifyEvent event) {
		Control control = viewer.getControl();
		if (control.isDisposed() || control.getDisplay().isDisposed()) {
			return;
		}
		
		control.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				switch (event.getType()) {
				case RowDataChangeEvent.ROW_DATA_ADDED_CHANGE:
					rowAdded(event);
					break;
					
				case RowDataChangeEvent.ROW_DATA_REMOVED_CHANGE:
					rowRemoved(event);
					break;
					
				case RowDataChangeEvent.ROW_DATA_MODIFIED_CHANGE:
					rowModified(event);
					break;
				
				case RowDataChangeEvent.ROWS_REMOVED_CHANGE:
					rowsRemoved(event);
					break;
				}
			}
		});
	}

	protected void rowAdded(ModifyEvent event) {
		if (event.getSource() != model) {
			return;
		}
		Object data = event.getData();
		viewer.add(data);
		viewer.setSelection(new StructuredSelection(new Object[] { data }), true);
	}

	protected void rowRemoved(ModifyEvent event) {
		if (event.getSource() != model) {
			return;
		}
		Object data = event.getData();
		// Try to transfer current selection to next item after
		// deleting the given node from the tree.
		TableItem[] selections = viewer.getTable().getSelection();
		Rectangle bounds = selections.length > 0 ? selections[0].getBounds() : new Rectangle(0, 0, 0, 0);
		viewer.remove(data);
		Item next = viewer.getTable().getItem(new Point(bounds.x, bounds.y));
		if (next != null) {
			viewer.setSelection(new StructuredSelection(new Object[] { next.getData() }), true);
		}
	}

	protected void rowModified(ModifyEvent event) {
		if (event.getSource() != model) {
			viewer.refresh();
		}
		else {
			viewer.refresh(event.getData());
		}
	}
	
	private void rowsRemoved(ModifyEvent event) {
		//viewer.remove(event.getData());
		System.out.println(event.getSource());
		viewer.refresh();
	}
	
	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;

		if (newInput != oldInput) {
			if (oldInput != null) {
				RowBasedModel model = (RowBasedModel) oldInput;
				model.unregister(this);
			}
			if (newInput != null) {
				RowBasedModel model = (RowBasedModel) newInput;
				model.register(this);
			}
		}
		
		model = (RowBasedModel) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return model.getAll();
	}
}
