package me.instcode.undo.ui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JList;
import javax.swing.event.MouseInputAdapter;

public class JUndoList extends JList {
    private ComboButton m_jParent = null;

    public JUndoList(ComboButton jParent) {
        m_jParent = jParent;
        this.addMouseMotionListener(new MouseHandler());
        this.addMouseListener(new MouseHandler());
        this.addPropertyChangeListener(new PropertyHandler());       
    }

    class PropertyHandler implements PropertyChangeListener {
        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (!evt.getPropertyName().equals("Activate popup"))
                return;
                
            clearSelection();    
            try {
                setSelectedIndex(0);
                scrollRectToVisible(new Rectangle(0, 0, 1, 1));
            }
			catch (Exception ex) {
				// Empty
            }
        }
    }
    
    class MouseHandler extends MouseInputAdapter {
        public void mouseMoved(MouseEvent e) {
            int index = locationToIndex(e.getPoint());
            setSelectionInterval(0, index);
        }

        public void mousePressed(MouseEvent e) {
            int index = locationToIndex(e.getPoint());
            setSelectionInterval(0, index);
            ActionEvent event = new ActionEvent(e.getSource(),
                    ActionEvent.ACTION_PERFORMED, null);
            m_jParent.executeAction(event);
        }
    }
}