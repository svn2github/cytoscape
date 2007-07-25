/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.Column;
import infovis.utils.*;
import infovis.visualization.render.SortPseudoVisualColumn;

import java.beans.PropertyChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.event.*;

/**
 * Class SortPseudoColumnPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class SortPseudoColumnPanel extends AbstractVisualColumnPanel {
    protected JCheckBox               inverseOrder;
    protected SortPseudoVisualColumn  vcd;
    protected transient boolean       inUpdate;
    
    public SortPseudoColumnPanel(SortPseudoVisualColumn vc) {
        super(vc);
        vcd = vc;
        createCombo();
        inverseOrder = new JCheckBox("Inverse Order");
        inverseOrder.setAlignmentX(LEFT_ALIGNMENT);
        inverseOrder.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateSort();
            }
        });
        add(inverseOrder);    
        getVisualization().addPropertyChangeListener(getName(), this);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(getName())) {
            update();
        }
    }
    
    public void update() {
        // If the visualization changed because of us, we are up to date
        if (inUpdate) return;
        // Otherwise, we cannot guess the permutation so we don't show
        // anywhing meaningful
        inUpdate = true;
        try {
            model.setSelectedItem(null);
            inverseOrder.setSelected(false);
        }
        finally {
            inUpdate = false;
        }
    }

    public void updateSort() {
        if (inUpdate) return;
        inUpdate = true;
        try {
        Column col = (Column)combo.getSelectedItem();
        if (col == null) {
            if (inverseOrder.isSelected()) {
                getVisualization().setPermutation(
                        new InverseComparator(IdRowComparator.getInstance()));
            }
            else {
                getVisualization().setPermutation(null);
            }
            return;
        }
        RowComparator comp = densifyColumn(col);
        if (inverseOrder.isSelected()) {
            getVisualization().setPermutation(new InverseComparator(comp));
        }
        else {
            getVisualization().setPermutation(comp);
        }
        }
        finally {
            inUpdate = false;
        }
    }

    
    public RowComparator densifyColumn(Column col) {
        //return new DenseColumnComparator(col); // comparison should be defined
        return col;
    }
    
    public void contentsChanged(ListDataEvent e) {
        if (e.getSource() == model) {
            updateSort();
        }
        else
            super.contentsChanged(e);
    }    
}
