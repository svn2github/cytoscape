/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Visualization;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Base class for creating Visual Panels
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public abstract class AbstractControlPanel extends Box
    implements ListDataListener, ActionListener, ChangeListener {
    private Visualization visualization;
    public static final Dimension MAX_SLIDER_DIMENSION = new Dimension(Integer.MAX_VALUE, 40);

    /**
     * Constructor for AbstractControlPanel.
     * @param vis the visualization to control
     */
    public AbstractControlPanel(Visualization vis) {
        super(BoxLayout.Y_AXIS);
        this.visualization = vis;
    }
    
    /**
     * Creates a title border around a specified component
     * @param comp the JComponent to decorate
     * @param title the title
     */
    public static void setTitleBorder(JComponent comp, String title) {
        comp.setBorder(BorderFactory.createTitledBorder(title));
    }
    
    /**
     * Sets the maximum size of a specified component (slider like)
     * @param rs the component
     */
    public static void setMaximumSize(JComponent rs) {
        rs.setMaximumSize(MAX_SLIDER_DIMENSION);
    }

    /**
     * @see javax.swing.event.ListDataListener#contentsChanged(ListDataEvent)
     */
    public void contentsChanged(ListDataEvent e) {
    }

    /**
     * @see javax.swing.event.ListDataListener#intervalAdded(ListDataEvent)
     */
    public void intervalAdded(ListDataEvent e) {
    }

    /**
     * @see javax.swing.event.ListDataListener#intervalRemoved(ListDataEvent)
     */
    public void intervalRemoved(ListDataEvent e) {
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
    }

    /**
     * Returns the visualization.
     * @return the Visualization
     */
    public Visualization getVisualization() {
        return visualization;
    }

}
