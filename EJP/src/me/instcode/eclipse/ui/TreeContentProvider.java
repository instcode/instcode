package me.instcode.eclipse.ui;

import me.instcode.event.ModifyEvent;
import me.instcode.event.ModifyListener;
import me.instcode.model.StructuredModel;
import me.instcode.model.RowModelChangeEvent;
import me.instcode.model.node.Node;
import me.instcode.model.node.TreeModel;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This is a UI model to connect a {@link StructuredModel} with a
 * {@link TreeViewer}.
 * 
 * @author khoanguyen
 *
 */
public class TreeContentProvider implements ITreeContentProvider, ModifyListener {
	private TreeViewer viewer;
	private TreeModel model;

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
					break;
				}
			}
		});
	}

	protected void modelReset(ModifyEvent event) {
		viewer.refresh();
	}

	protected void rowRemoved(ModifyEvent event) {
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
		refreshIfNeeds(parent);
		TreeItem next = viewer.getTree().getItem(new Point(bounds.x, bounds.y));
		if (next != null) {
			viewer.setSelection(new StructuredSelection(new Object[] { next.getData() }), true);
		}
	}
	
	protected void rowAdded(ModifyEvent event) {
		if (event.getSource() != model) {
			return;
		}
		Node node = (Node) event.getData();
		Node parent = node.getParent();
		viewer.add(parent, node);
		refreshIfNeeds(parent);
		viewer.setSelection(new StructuredSelection(new Object[] { node }), true);
	}

	/**
	 * Update tree view if any row has been modified.<br>
	 * If the modifying event is from an external source (i.e, this tree depends
	 * on that modification), the whole tree will be refreshed. Otherwise, this
	 * will only update the given modified node.
	 * 
	 * @param event
	 */
	protected void rowModified(ModifyEvent event) {
		if (event.getSource() != model) {
			viewer.refresh();
		}
		else {
			viewer.refresh(event.getData());
		}
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;

		if (newInput != oldInput) {
			if (oldInput != null) {
				TreeModel model = (TreeModel) oldInput;
				model.unregister(this);
			}
			if (newInput != null) {
				TreeModel model = (TreeModel) newInput;
				model.register(this);
			}
		}
		
		model = (TreeModel) newInput;
	}
	
	/**
	 * This method does refresh tree for the updates <b>ONLY IF</b>
	 * the given node is the invisible root node.
	 * 
	 * @param node
	 */
	private void refreshIfNeeds(Node node) {
		if (node == model.getRoot()) {
			viewer.refresh();
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
		model = null;
	}
}