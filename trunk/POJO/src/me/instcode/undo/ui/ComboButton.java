package me.instcode.undo.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class ComboButton extends JComponent {
	private JActionButton m_arrowBtn = null;
	private JActionButton m_actionBtn = null;

	private ArrowIcon m_arrowIcon = new ArrowIcon(SwingConstants.SOUTH);
	private Action m_action = null;
	private JComponent m_jPopup = null;
	private JPopupMenu m_jPopupMenu = null;

	public ComboButton(Action action) {
		this(null, null);
		setAction(action);
	}

	/**
	 * Constructor
	 * 
	 * @param label
	 * @param icon
	 */
	public ComboButton(String label, Icon icon) {
		setLayout(new BorderLayout());
		MouseHandler adapter = new MouseHandler();
		m_jPopup = new JUndoList(this);
		m_actionBtn = new JActionButton(label, icon);
		m_arrowBtn = new JActionButton(null, m_arrowIcon);
		setArrowSize(ArrowIcon.DEFAULT_SIZE);
		m_actionBtn.addMouseListener(adapter);
		m_arrowBtn.addMouseListener(adapter);
		m_arrowBtn.setModel(m_actionBtn.getModel());
		m_action = m_actionBtn.getAction();
		m_actionBtn.removeActionListener(m_action);
		add(m_actionBtn, BorderLayout.CENTER);
		add(m_arrowBtn, BorderLayout.EAST);
		setFocusable(false);
		updateMaximumSize();
	}

	/**
	 * Update maximum size of the control Use for updating layout
	 */
	public void updateMaximumSize() {
		int nWidth = m_actionBtn.getPreferredSize().width + m_arrowBtn.getPreferredSize().width;
		int nHeight = m_actionBtn.getPreferredSize().height;
		setMaximumSize(new Dimension(nWidth, nHeight));
	}

	/**
	 * Set arrow icon size
	 * 
	 * @param nSize
	 */
	public void setArrowSize(int nSize) {
		m_arrowIcon.setSize(nSize);
		m_arrowBtn.setPreferredSize(new Dimension(4 * nSize, 0));
		updateMaximumSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension)
	 */
	public void setPreferredSize(Dimension dimension) {
		setMaximumSize(new Dimension(dimension));
		dimension.width -= m_arrowBtn.getPreferredSize().width;
		m_actionBtn.setPreferredSize(dimension);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setToolTipText(java.lang.String)
	 */
	public void setToolTipText(String strTooltip) {
		super.setToolTipText(strTooltip);
		m_arrowBtn.setToolTipText(strTooltip);
		m_actionBtn.setToolTipText(strTooltip);
	}

	/**
	 * Set this action for Action-Button
	 * 
	 * @param action
	 */
	public void setAction(Action action) {
		if (action.equals(m_action))
			return;
		m_action = action;
		m_action.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setEnabled(m_action.isEnabled());
			}
		});
		setEnabled(m_action.isEnabled());
		Icon icon = (Icon) m_action.getValue(Action.SMALL_ICON);
		String label = (String) m_action.getValue(Action.NAME);
		// We use label for tooltip if this action's had a valid icon
		if (icon != null) {
			m_actionBtn.setIcon(icon);
			setToolTipText(label);
		}
		else {
			m_actionBtn.setText(label);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean b) {
		m_arrowBtn.setEnabled(b);
		m_actionBtn.setEnabled(b);
	}

	/**
	 * Perform action after user selected items from popup menu
	 * 
	 * @param event
	 */
	public void executeAction(ActionEvent event) {
		if (m_jPopupMenu != null) {
			m_jPopupMenu.setVisible(false);
		}
		m_action.actionPerformed(event);

		/*
		 * The code below enable this control to transfer focus to next
		 * component after it performs an action because by default, it's not
		 * focusable, so it cannot transfer focus to other controls
		 */
		this.setFocusable(true);
		this.transferFocus();
		this.setFocusable(false);
	}

	/**
	 * Setting up a popup component
	 * 
	 * @param jComp
	 */
	public void setPopupComponent(JComponent jComp) {
		m_jPopup = jComp;
	}

	/**
	 * @return internal popup component
	 */
	public JComponent getPopupComponenet() {
		return m_jPopup;
	}

	/**
	 * Show the popup by using JMenuPopup
	 */
	public void showPopup() {
		if (m_jPopup != null) {
			m_jPopupMenu = new JPopupMenu();
			m_jPopupMenu.setFocusable(false);
			JScrollPane jScrollPane = new JScrollPane(m_jPopup);
			jScrollPane.setPreferredSize(new Dimension(250, 150));
			m_jPopupMenu.add(jScrollPane);
			m_jPopupMenu.show(m_actionBtn, 0, m_actionBtn.getHeight());
			m_jPopup.firePropertyChange("Activate popup", true, false);
		}
	}

	class JActionButton extends JButton {
		/**
		 * This button have some special characteristics
		 * 
		 * @param label
		 * @param icon
		 */
		public JActionButton(String label, Icon icon) {
			super(label, icon);
			setFocusable(false);
			setRolloverEnabled(true);
			setContentAreaFilled(false);
		}
	}

	protected class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			JButton jButton = (JButton) e.getSource();
			if (!jButton.isEnabled())
				return;
			if (jButton.equals(m_actionBtn))
				executeAction(null);
			else
				showPopup();
		}
	}
}