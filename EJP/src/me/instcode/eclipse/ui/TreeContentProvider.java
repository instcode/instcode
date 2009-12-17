package me.instcode.eclipse.ui;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;
import me.instcode.model.RowBasedModel;
import me.instcode.model.RowDataChangeEvent;
import me.instcode.model.node.Node;
import me.instcode.model.node.NodeModel;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This is a middle layer to connect a {@link RowBasedModel} with a
 * {@link TreeViewer}.
 * 
 * @author dcsnxk
 *
 */
public class TreeContentProvider implements ITreeContentProvider, ModifyListener {
	private TreeViewer viewer;
	private NodeModel model;

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

	private void rowRemoved(ModifyEvent event) {
		if (event.getSource() != model) {
			return;
		}
		Node node = (Node) event.getData();
		// Try to transfer current selection to next item after
		// deleting the given node from the tree.
		TreeItem[] selections = viewer.getTree().getSelection();
		Rectangle bounds = selections.length > 0 ? selections[0].getBounds() : new Rectangle(0, 0, 0, 0);
		Node parent = node.getParent();
		viewer.remove(parent, new Node[] { node });
		TreeItem next = viewer.getTree().getItem(new Point(bounds.x, bounds.y));
		if (next != null) {
			viewer.setSelection(new StructuredSelection(new Object[] { next.getData() }), true);
		}
	}
	
	private void rowAdded(ModifyEvent event) {
		if (event.getSource() != model) {
			return;
		}
		Node node = (Node) event.getData();
		Node parent = node.getParent();
		viewer.add(parent, node);
		viewer.setSelection(new StructuredSelection(new Object[] { node }), true);
	}

	private void rowModified(ModifyEvent event) {
		if (event.getSource() != model) {
			refresh(model.getRoot());
		}
		else {
			refresh((Node) event.getData());
		}
	}

	private void rowsRemoved(ModifyEvent event) {
		refresh(model.getRoot());
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;

		if (newInput != oldInput) {
			if (oldInput != null) {
				NodeModel model = (NodeModel) oldInput;
				model.unregister(this);
			}
			if (newInput != null) {
				NodeModel model = (NodeModel) newInput;
				model.register(this);
			}
		}
		
		model = (NodeModel) newInput;
	}
	
	/**
	 * Refresh tree for the updates. <br>
	 * 
	 * Because root node is invisible, we have to refresh
	 * all the tree if the specified node is root.
	 * 
	 * @param parent
	 */
	private void refresh(Node parent) {
		if (parent == model.getRoot()) {
			viewer.refresh();
		}
		else {
			viewer.refresh(parent);
		}
	}

	public Object[] getChildren(Object parentElement) {
		return ((Node)parentElement).getChildren();
	}

	public Object getParent(Object element) {
		return ((Node)element).getParent();
	}

	public boolean hasChildren(Object element) {
		return ((Node)element).hasChildren();
	}

	public Object[] getElements(Object inputElement) {
		Node root = model.getRoot();
		return root != null ? root.getChildren() : Node.NO_CHILDREN;
	}

	public void dispose() {
	}
}