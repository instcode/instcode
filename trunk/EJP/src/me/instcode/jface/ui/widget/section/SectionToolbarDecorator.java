package me.instcode.jface.ui.widget.section;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Section;

/**
 * This implementation basically supports adding a toolbar
 * to the specified section <b>only if</b> that section implements
 * {@link #SectionToolbar} interface. The section then is able
 * to add toolbar items in #createToolBarActions method. Be noted
 * that the toolbar will be put in the text area of the section.
 * 
 * @author dcsnxk
 *
 */
public class SectionToolbarDecorator {
	public static interface SectionToolbar {
		void createToolBarActions(ToolBarManager toolBarManager);
	}
	
	/**
	 * Add a toolbar which is described in the given {@link #SectionToolbar}
	 * to the specified section.
	 * 
	 * @param section
	 * @param st
	 */
	public SectionToolbarDecorator(Section section, SectionToolbar st) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);
		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		
		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (handCursor != null && !handCursor.isDisposed()) {
					handCursor.dispose();
				}
			}
		});

		st.createToolBarActions(toolBarManager);
		toolBarManager.update(true);
		section.setTextClient(toolbar);
	}
}
