package me.instcode.eclipse.ui.widget.section;

import me.instcode.eclipse.ui.widget.section.SectionToolbarDecorator.SectionToolbar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

public abstract class SectionSashPart implements SectionToolbar {
	protected SashForm sashForm;

	protected Composite createPartContent(Section section) {
		sashForm = new SashForm(section, SWT.NULL);
		sashForm.setMenu(section.getMenu());
		section.setClient(sashForm);
		addParts(sashForm);
		createToolbar(section);
		return sashForm;
	}

	abstract protected void addParts(Composite parent);

	protected void createToolbar(Section section) {
		new SectionToolbarDecorator(section, this);
	}

	public void createToolBarActions(ToolBarManager toolBarManager) {
		Action hAction = createHorAction();
		Action vAction = createVerAction();
		toolBarManager.add(hAction);
		toolBarManager.add(vAction);
	}

	protected Action createVerAction() {
		Action vAction = new Action("[-]", IAction.AS_RADIO_BUTTON) {
			public void run() {
				sashForm.setOrientation(SWT.VERTICAL);
			}
		};
		vAction.setChecked(false);
		vAction.setToolTipText("Vertical");
//		vAction.setImageDescriptor();
		return vAction;
	}

	protected Action createHorAction() {
		Action hAction = new Action("[|]", IAction.AS_RADIO_BUTTON) {
			public void run() {
				sashForm.setOrientation(SWT.HORIZONTAL);
			}
		};
		hAction.setChecked(true);
		hAction.setToolTipText("Horizontal");
//		hAction.setImageDescriptor(*);
		return hAction;
	}
}
