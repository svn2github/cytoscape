/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Column;
import infovis.Table;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.render.SortPseudoColumnPanel;
import infovis.panel.render.VisualPanelFactory;
import infovis.visualization.VisualColumnDescriptor;
import infovis.visualization.render.AbstractVisualColumn;
import infovis.visualization.render.SortPseudoVisualColumn;
import infovis.visualization.render.VisualStatistics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Control panel for standard visual components such as size and label.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class DefaultVisualPanel extends AbstractControlPanel {
    protected ColumnFilter            filter;
    protected Table                   table;
    protected JCheckBox               displayStatistics;
    protected OrientationPanel        orientation;
    protected JCheckBox               usingGradient;
    protected JToggleButton           backgroundColor;

    /**
     * Creates a DefaultVisualPanel. 
     * @param visualization the visualization
     * @param filter the ColumnFilter for allowed columns.
     */
    public DefaultVisualPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization);
        this.filter = filter;
        table = getVisualization().getTable();
        createAll();
        add(Box.createVerticalGlue());
    }

    protected void createAll() {
        for (Iterator iter = getVisualization().getVisualColumnIterator();
            iter.hasNext(); ) {
            String name = (String)iter.next();
            VisualColumnDescriptor vcd = 
                getVisualization().getVisualColumnDescriptor(name);
            if (vcd instanceof AbstractVisualColumn) {
                AbstractVisualColumn vc = (AbstractVisualColumn) vcd;
                JComponent comp = getPanelFor(vc);
                if (comp != null) {
                    add(comp);
                }
            }
        }
        addBackground(getVisualization());
        addSort(getVisualization());
        addStats(getVisualization());
    }
    
    protected JComponent getPanelFor(AbstractVisualColumn vc) {
        return VisualPanelFactory.createVisualPanel(vc);
    }
    
    protected void addBackground(Visualization visualization) {
        JPanel backgroundBox = new JPanel();
        backgroundBox.setAlignmentX(LEFT_ALIGNMENT);
        setTitleBorder(backgroundBox, "Background");

        backgroundColor = new JToggleButton("Color");
        backgroundColor.setAlignmentX(CENTER_ALIGNMENT);
        backgroundColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color ret = JColorChooser.showDialog(
                        backgroundColor, 
                        "Background Color", 
                        backgroundColor.getBackground());
                if (ret != null) {
                    getVisualization().getParent().setBackground(ret);
                }
            }
        });
        backgroundBox.add(backgroundColor);        
        
        usingGradient = new JCheckBox("gradient");
        usingGradient.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JComponent comp = getVisualization().getComponent();
                if (comp != null && (comp instanceof VisualizationPanel)) {
                    final VisualizationPanel vp = (VisualizationPanel)comp;            
                    vp.setUsingGradient(usingGradient.isSelected());
                }
            }
        });
        backgroundBox.add(usingGradient);
        
        add(backgroundBox);
        
        if (visualization.getParent() == null) {
            visualization.addPropertyChangeListener(
                    Visualization.PROPERTY_PARENT, 
                    new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    attachComponent();
                }
            });
        }
        else {
            attachComponent();
        }
        
    }
    
    protected void attachComponent() {
        final JComponent comp = getVisualization().getComponent();
        if (comp == null || !(comp instanceof VisualizationPanel)) 
            return;
        VisualizationPanel vp = (VisualizationPanel)comp;    
        usingGradient.setSelected(vp.isUsingGradient());
        usingGradient.setEnabled(true);
        backgroundColor.setBackground(comp.getBackground());
        comp.addPropertyChangeListener("background", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                backgroundColor.setBackground(comp.getBackground());
            }
        });        
    }

    protected void addSort(Visualization visualization) {
        SortPseudoVisualColumn vc = new SortPseudoVisualColumn();
        vc.setVisualization(visualization);
        add(new SortPseudoColumnPanel(vc));
    }
        
    protected void addStats(Visualization visualization) {
        final VisualStatistics stats = VisualStatistics.get(visualization);
        if (stats != null) {
            displayStatistics = new JCheckBox("Display stats");
            displayStatistics.setSelected(stats.isDisplayingStatistics());
            displayStatistics.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    stats.setDisplayingStatistics(displayStatistics.isSelected());
                }
            });
            add(displayStatistics);
        }
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
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                           (int)combo.getPreferredSize()
                                                     .getHeight()));
        combo.setAlignmentX(LEFT_ALIGNMENT);
        add(combo);
        combo.getModel().addListDataListener(this);
        return combo;
    }
    
    /**
     * Adds a panel with orientation buttons.
     */
    public void addOrientationButtons() {
        orientation = new OrientationPanel() {
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                getVisualization().setOrientation(orientation);
                getVisualization().repaint();
            }
        };
        orientation.setAlignmentX(LEFT_ALIGNMENT);
        orientation.setOrientation(getVisualization().getOrientation());
        setTitleBorder(orientation, "Orientation");
        add(orientation);
    }

    /**
     * Returns the table.
     * @return Table
     */
    public Table getTable() {
        return table;
    }
}
