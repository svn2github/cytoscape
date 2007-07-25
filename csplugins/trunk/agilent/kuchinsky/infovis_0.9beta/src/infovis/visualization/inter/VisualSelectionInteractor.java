/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.column.BooleanColumn;
import infovis.visualization.ItemRenderer;
import infovis.visualization.render.VisualSelection;

import java.awt.event.MouseEvent;

import javax.swing.JComponent;

/**
 * Interactor class for the VisualSelection item renderer.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * 
 * @infovis.factory RendererInteractorFactory infovis.visualization.render.VisualSelection
 */
public class VisualSelectionInteractor extends BasicVisualizationInteractor {
    protected VisualSelection renderer;

    /**
     * Constructor.
     * 
     * @param renderer a VisualSelection
     */
    public VisualSelectionInteractor(ItemRenderer renderer) {
        this.renderer = (VisualSelection)renderer;
    }
    
    /**
     * {@inheritDoc}
     */
    public void install(JComponent comp) {
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void uninstall(JComponent comp) {
        comp.removeMouseListener(this);
        comp.removeMouseMotionListener(this);
    }
    
    /**
     * Sets the selected item.
     * @param sel the selected item.
     */
    public void setSelection(int sel) {
        BooleanColumn selection = renderer.getSelection();
        try {
            selection.disableNotify();
            selection.clear();
            addSelection(sel);
        }
        finally {
            selection.enableNotify();
        }
    }

    /**
     * Adds an item to the current selection.
     * 
     * @param sel the item.
     */
    public void addSelection(int sel) {
        BooleanColumn selection = renderer.getSelection();
        if (sel != -1) {
            selection.addSelectionInterval(sel, sel);
        }
    }
    
    /**
     * Toggle the selection of the specified item.
     * @param sel the item.
     */
    public void ToggleSelection(int sel) {
        BooleanColumn selection = renderer.getSelection();
        if (sel != -1) {
            if (selection.isSelectedIndex(sel)) {
                selection.removeSelectionInterval(sel, sel);
            }
            else {
                selection.addSelectionInterval(sel, sel);
            }
        }
    }

    // interface MouseListener
    
    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {
        mouseDragged(e);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {
        int sel = pickTop(e.getX(), e.getY(), getBounds());
        if ((e.getModifiers() & MouseEvent.SHIFT_MASK) == 0)
            setSelection(sel);
        else {
            ToggleSelection(sel);
        }
    }    
}
