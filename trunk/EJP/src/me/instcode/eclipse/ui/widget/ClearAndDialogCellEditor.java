package me.instcode.eclipse.ui.widget;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

public abstract class ClearAndDialogCellEditor extends CellEditor {
	private FocusListener listener;
	private Label label;
	private Object value;
	private Button clearButton, browseButton;

	public ClearAndDialogCellEditor(Composite parent) {
		super(parent);
	}

	@Override
	protected boolean dependsOnExternalFocusListener() {
		return false;
	}

	@Override
	protected void doSetFocus() {
		getControl().forceFocus();
		getControl().removeFocusListener(listener);
		getControl().addFocusListener(listener);
	}

	/**
	 * Pass a 'null' to clear the cell value 
	 * 
	 * @param newValue
	 */
	protected void modify(Object newValue) {
		doSetValue(newValue);
		setValueValid(true);
		markDirty();
		fireApplyEditorValue();
	}

	protected abstract String getText(Object object);
	protected abstract SelectionStatusDialog getDialog();

	@Override
	protected Control createControl(Composite parent) {
		listener = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				ClearAndDialogCellEditor.this.focusLost();
			}
		};

		MouseTrackListener mouseListener = new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				clearButton.removeFocusListener(listener);
				browseButton.removeFocusListener(listener);
				getControl().removeFocusListener(listener);
				((Button) e.getSource()).setFocus();
			}

			@Override
			public void mouseExit(MouseEvent e) {
				((Button) e.getSource()).removeFocusListener(listener);
				((Button) e.getSource()).addFocusListener(listener);
			}
		};

		Font font = parent.getFont();
		Color bg = parent.getBackground();

		Composite editor = new Composite(parent, getStyle());
		editor.setLayoutData(new GridData(GridData.FILL_BOTH));
		editor.setFont(font);
		editor.setBackground(bg);

		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;

		editor.setLayout(gridLayout);
		{
			label = new Label(editor, SWT.LEFT);
			label.setFont(font);
			label.setBackground(bg);
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}
		{
			final Button button = new Button(editor, SWT.DOWN);
			button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
			button.setText("x");
			button.addMouseTrackListener(mouseListener);
			button.setFont(font);
			button.setToolTipText("Clear");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					modify(null);
				}
			});
			clearButton = button;
		}
		{
			final Button button = new Button(editor, SWT.DOWN);
			button.addMouseTrackListener(mouseListener);
			button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
			button.setText("...");
			button.setFont(font);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SelectionStatusDialog dialog = getDialog();
					if (dialog.open() == IDialogConstants.OK_ID) {
						modify(dialog.getFirstResult());
					}
				}
			});
			browseButton = button;
		}
		return editor;
	}

	@Override
	protected Object doGetValue() {
		return value;
	}

	@Override
	protected void doSetValue(Object value) {
		this.value = value;
		label.setText(getText(value));
	}
}