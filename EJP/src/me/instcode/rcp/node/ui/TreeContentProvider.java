package me.instcode.rcp.node.ui;

import me.instcode.rcp.node.model.Node;
import me.instcode.rcp.node.model.NodeModel;
import me.instcode.rcp.node.model.RowBasedModel;
import me.instcode.rcp.node.model.RowDataChangeEvent;
import me.instcode.rcp.node.model.RowDataChangeListener;

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
public class TreeContentProvider implements ITreeContentProvider, RowDataChangeListener {
	private TreeViewer viewer;
	private Node root;

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
					nodeAdded((Node) event.getData());
					break;
					
				case RowDataChangeEvent.ROW_DATA_REMOVED_CHANGE:
					nodeRemoved((Node) event.getData());			
					break;
					
				case RowDataChangeEvent.ROW_DATA_MODIFIED_CHANGE:
					viewer.refresh(((Node) event.getData()).getParent());
					break;
				}
			}
		});
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;

		if (newInput != oldInput) {
			if (oldInput != null) {
				NodeModel model = (NodeModel) oldInput;
				model.removeRowDataChangeListener(this);
				root = null;
			}
			if (newInput != null) {
				NodeModel model = (NodeModel) newInput;
				model.addRowDataChangeListener(this);
				root = model.getRoot();
			}
		}
	}

	private void nodeRemoved(Node node) {
		// Try to transfer current selection to next item after
		// deleting the given node from the tree.
		Rectangle bounds = viewer.getTree().getSelection()[0].getBounds();
		Node parent = node.getParent();
		viewer.remove(parent, new Node[] { node });
		TreeItem next = viewer.getTree().getItem(new Point(bounds.x, bounds.y));
		if (next != null) {
			viewer.setSelection(new StructuredSelection(new Object[] { next.getData() }), true);
		}
		refresh(parent);
	}
	
	private void nodeAdded(Node node) {
		Node parent = node.getParent();
		viewer.add(parent, node);
		viewer.setSelection(new StructuredSelection(new Object[] { node }), true);
		refresh(parent);
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
		if (parent == root) {
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
		return root != null ? root.getChildren() : Node.NO_CHILDREN;
	}

	public void dispose() {
		root = null;
	}
}