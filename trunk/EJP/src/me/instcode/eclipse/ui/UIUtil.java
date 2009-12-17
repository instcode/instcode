package me.instcode.eclipse.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

public class UIUtil {
	
	/**
	 * Setup double-click strategy to activate the appropriate editor
	 * for a table cell
	 * 
	 * @param viewer
	 */
	public static void setDoubleClickToEditStrategy(TableViewer viewer) {
		TableViewerEditor.create(viewer,
				new TableViewerFocusCellManager(viewer, new FocusCellHighlighter(viewer) {}),
				new ColumnViewerEditorActivationStrategy(viewer) {
					protected boolean isEditorActivationEvent(
							ColumnViewerEditorActivationEvent event) {
						return event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION;
					}
				}, ColumnViewerEditor.DEFAULT);
	}

	public static MenuManager createPopupMenuManager(final Viewer viewer, String menuId) {
		final MenuManager menuManager = new MenuManager("popup", menuId);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				createSelectionMenu(manager, viewer);
				// Other plug-ins can contribute there actions here
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				//manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		return menuManager;
	}
	
	/**
	 * Creates and assigns the popup menu for the component.
	 */
	private static void createSelectionMenu(IMenuManager menu, final Viewer viewer) {
		if (!(viewer instanceof CheckboxTableViewer)) {
			return;
		}
		
		Action selectAllAction = new Action("Select All", Action.AS_PUSH_BUTTON) {
			public void run() {
				((CheckboxTableViewer)viewer).setAllChecked(true);
			}
		};
		menu.add(selectAllAction);
		
		Action deselectAllAction = new Action("Deselect All", Action.AS_PUSH_BUTTON) {
			public void run() {
				((CheckboxTableViewer)viewer).setAllChecked(false);
			}
		};
		menu.add(deselectAllAction);
		
		Action invertSelectionAction = new Action("Inverse Selection", Action.AS_PUSH_BUTTON) {
			public void run() {
				Table table = ((CheckboxTableViewer)viewer).getTable();
				int itemCount = table.getItemCount();
				for(int i = 0; i < itemCount; i++){
	                TableItem item = table.getItem(i);
	                item.setChecked(!item.getChecked());
	            }
			}
		};
		menu.add(invertSelectionAction);
	}
	
	public static void applyCommonEditingFeature(IWorkbenchPartSite site, TableViewer viewer, String popupId) {
		UIUtil.setDoubleClickToEditStrategy(viewer);
		MenuManager manager = UIUtil.createPopupMenuManager(viewer, popupId);
		UIUtil.createSelectionMenu(manager, viewer);
		// This enables other plugins to contribute more actions
		// to this context menu.
		site.registerContextMenu(popupId, manager, viewer);
	}
}
