package me.instcode.eclipse.ui;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;
import me.instcode.model.TableModel;
import me.instcode.model.RowModelChangeEvent;

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
	private TableModel<?> model;

	@Override
	public void dataModified(final ModifyEvent event) {
		Control control = viewer.getControl();
		if (control.isDisposed() || control.getDisplay().isDisposed()) {
			return;
		}
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				switch (event.getType()) {
				case RowModelChangeEvent.MODEL_RESET:
					modelReset(event);
					break;
					
				case RowModelChangeEvent.ROW_ADDED:
					rowAdded(event);
					break;
					
				case RowModelChangeEvent.ROW_REMOVED:
					rowRemoved(event);
					break;
					
				case RowModelChangeEvent.ROW_DATA_MODIFIED:
					rowModified(event);
					break;
				
				case RowModelChangeEvent.ROWS_REMOVED:
					rowsRemoved(event);
					break;
				}
			}
		});
	}

	protected void modelReset(ModifyEvent event) {
		viewer.refresh();
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
		viewer.remove((Object[])event.getData());
	}
	
	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;

		if (newInput != oldInput) {
			if (oldInput != null) {
				TableModel<?> model = (TableModel<?>) oldInput;
				model.unregister(this);
			}
			if (newInput != null) {
				TableModel<?> model = (TableModel<?>) newInput;
				model.register(this);
			}
		}
		
		model = (TableModel<?>) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return model.getAll();
	}
}
