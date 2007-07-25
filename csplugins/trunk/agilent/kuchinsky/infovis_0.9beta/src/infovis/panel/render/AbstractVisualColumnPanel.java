/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.*;
import infovis.Column;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.ColumnListCellRenderer;
import infovis.panel.FilteredColumnListModel;
import infovis.visualization.render.AbstractVisualColumn;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Base class for panels showing the configuration of visual column
 * descriptors 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public abstract class AbstractVisualColumnPanel extends Box 
    implements PropertyChangeListener, ListDataListener {
    protected String propName;
    protected AbstractVisualColumn vc;
    protected FilteredColumnListModel model;
    protected JComboBox combo;
    
    public AbstractVisualColumnPanel(AbstractVisualColumn vc) {
        super(BoxLayout.Y_AXIS);
        this.vc = vc;
        this.propName = Visualization.VC_DESCRIPTOR_PROPERTY_PREFIX+getName();
        getVisualization().addPropertyChangeListener(propName, this);
        String title = 
            getName().substring(0, 1).toUpperCase() + getName().substring(1);
        setTitleBorder(this, title);
    }
    
    protected void createCombo() {
        model = new FilteredColumnListModel(getTable(), getFilter());
        combo = createJCombo(model, null, null); // "Column");        
    }
    
    public JComponent getColumnSelector() {
        return combo;
    }

    public void update() {
        if (model != null) {
            model.setSelectedItem(vc.getColumn());
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(propName)) {
            update();
        }
    }
    public String getName() {
        return vc.getName();
    }
    
    public String getPropName() {
        return propName;
    }
    
    public AbstractVisualColumn getVc() {
        return vc;
    }
    
    public Visualization getVisualization() {
        return vc.getVisualization();
    }
    
    public Table getTable() {
        return getVisualization().getTable();
    }
    
    public ColumnFilter getFilter() {
        return vc;
    }
    
    protected JComboBox createJCombo(
            FilteredColumnListModel model, 
            Column c,
            String label) {
        model.setNullAdded(true);
        JComboBox combo = new JComboBox(model);
        model.setSelectedItem(c);
        combo.setRenderer(new ColumnListCellRenderer());
        return addJCombo(label, combo);
    }

    protected JComboBox addJCombo(
        String label,
        JComboBox combo) {
        setTitleBorder(combo, label);
        combo.setAlignmentX(LEFT_ALIGNMENT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                           (int)combo.getPreferredSize()
                                                     .getHeight()));
        add(combo);
        combo.getModel().addListDataListener(this);
        return combo;
    }

    public static void setTitleBorder(JComponent comp, String title) {
        if (title != null)
            comp.setBorder(BorderFactory.createTitledBorder(title));
    }

    /**
     * @see javax.swing.event.ListDataListener#contentsChanged(ListDataEvent)
     */
    public void contentsChanged(ListDataEvent e) {
        if (e.getSource() == model) {
            getVisualization().setVisualColumn(
                getName(), 
                (Column)combo.getSelectedItem());
       }
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
    
 }
