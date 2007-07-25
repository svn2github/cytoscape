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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.event.ListDataEvent;

/**
 * Class SortPseudoColumnPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class SortPseudoColumnPanel extends AbstractVisualColumnPanel 
    implements ActionListener {
    protected JCheckBox              inverseOrder;
    protected JCheckBox              compose;
    protected JCheckBox              filter;
    protected SortPseudoVisualColumn vcd;
    protected transient boolean      inUpdate;

    public SortPseudoColumnPanel(SortPseudoVisualColumn vc) {
        super(vc);
        vcd = vc;
        createCombo();
        Box hbox = createHorizontalBox();
        inverseOrder = new JCheckBox("Inverse Order");
        inverseOrder.setAlignmentX(LEFT_ALIGNMENT);
        inverseOrder.addActionListener(this);
        hbox.add(inverseOrder);
        compose = new JCheckBox("compose");
        compose.setAlignmentX(LEFT_ALIGNMENT);
        compose.addActionListener(this);
        hbox.add(compose);
        hbox.setAlignmentX(LEFT_ALIGNMENT);
        filter = new JCheckBox("filter");
        filter.setAlignmentX(LEFT_ALIGNMENT);
        filter.addActionListener(this);
        hbox.add(filter);
        add(hbox);
        getVisualization().addPropertyChangeListener(getName(), this);
    }

    public void actionPerformed(ActionEvent e) {
        updateSort();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(getName())) {
            update();
        }
    }

    public void update() {
        // If the visualization changed because of us, we are up to date
        if (inUpdate)
            return;
        // Otherwise, we cannot guess the permutation so we don't show
        // anywhing meaningful
        inUpdate = true;
        try {
            model.setSelectedItem(null);
            inverseOrder.setSelected(false);
        } finally {
            inUpdate = false;
        }
    }
    
    public Permutation getPermutation() {
        Permutation perm = getVisualization().getPermutation();
        if (perm == null) {
            return new Permutation(iterator());
        }
        else {
            return perm;
        }
    }
    
    public RowIterator iterator() {
        return getVisualization().iterator();
    }
    
    public void setPermutation(Permutation perm) {
        getVisualization().setPermutation(perm);
    }

    public void updateSort() {
        if (inUpdate)
            return;
        inUpdate = true;
        try {
            Column col = (Column) combo.getSelectedItem();
            boolean shouldInverse = inverseOrder.isSelected();
            boolean shouldCompose = compose.isSelected();
            boolean shouldFilter = filter.isSelected();
            Permutation perm;
            if (col == null) {
                if (! shouldInverse && ! shouldCompose) {
                    setPermutation(null);
                    return;
                }
            }
            if (shouldCompose){
                perm = getPermutation();
            }
            else {
                perm = new Permutation(iterator());
            }
            if (col != null) {
                if (shouldFilter)
                    perm.filter(col);
                perm.sort(col);
            }
            if (shouldInverse) {
                perm.inverse();
            }
            
            setPermutation(perm);
        } finally {
            inUpdate = false;
        }
    }

    public void contentsChanged(ListDataEvent e) {
        if (e.getSource() == model) {
            updateSort(); // TODO avoid calling it
        }
        else
            super.contentsChanged(e);
    }
}
