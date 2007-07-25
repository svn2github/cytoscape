/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.event.*;
import javax.swing.event.ListSelectionListener;

import infovis.*;
import infovis.Table;
import infovis.column.ColumnFilter;
import infovis.column.NumberColumn;
import infovis.panel.DefaultVisualPanel;

/**
 * Class TimeSeriesVisualPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class TimeSeriesVisualPanel extends DefaultVisualPanel
    implements ListSelectionListener {
    protected JList fromList;
    protected JList toList;
    protected JButton add;
    protected JButton remove;
    protected JButton up;
    protected JButton down;
    
    public TimeSeriesVisualPanel(
        TimeSeriesVisualization visualization,
        ColumnFilter filter) {
        super(visualization, filter);
    }
    
    public TimeSeriesVisualization getTimeSeries() {
        return (TimeSeriesVisualization)getVisualization()
            .findVisualization(TimeSeriesVisualization.class);
    }

    protected void createAll() {
        super.createAll();
        addColumnList();
    }

    public void updateColumnList() {
        Table t = getTimeSeries().getTable();
        DefaultListModel model = (DefaultListModel)fromList.getModel();
        for (int i = 0; i < t.getColumnCount(); i++) {
            Column c = t.getColumnAt(i);
            if (c == null
                || c.isInternal()
                || !(c instanceof NumberColumn)) {
                continue;
            }
            model.addElement(c);
        }
    }
    
    protected void addColumnList() {
        Box box = Box.createHorizontalBox();
        box.setAlignmentX(LEFT_ALIGNMENT);
        DefaultListModel model = new DefaultListModel();
        fromList = new JList(model);
        updateColumnList();
        JScrollPane fromSP = new JScrollPane(fromList);
        setTitleBorder(fromSP, "All");
        box.add(fromSP);
        fromList.getSelectionModel().addListSelectionListener(this);
        fromList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        Box vbox = Box.createVerticalBox();
        up = new JButton("Up");
        up.setEnabled(false);
        vbox.add(up);
        
        add = new JButton("Add");
        add.setEnabled(false);
        add.addActionListener(this);
        vbox.add(add);
        
        remove = new JButton("Remove");
        remove.setEnabled(false);
        remove.addActionListener(this);
        vbox.add(remove);
        
        down = new JButton("Down");
        down.setEnabled(false);
        vbox.add(down);
        box.add(vbox);
        
        toList = new JList(getTimeSeries().getDataColumnList());
        JScrollPane toSP = new JScrollPane(toList);
        setTitleBorder(toSP, "Visible");
        toList.getSelectionModel().addListSelectionListener(this);
        toList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        box.add(toSP);
        setTitleBorder(box, "Columns");
        add(box);
    }
    
    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == add) {
//          TODO            
//        }
        if (e.getSource() == remove) {
            DefaultListModel lm = (DefaultListModel)toList.getModel();
            for (int i = lm.getSize()-1; i >= 0; i--) {
                 if (toList.isSelectedIndex(i)) {
                     lm.remove(i);
                 }
            }
        }
        else if (e.getSource() == up) {
            DefaultListModel lm = (DefaultListModel)toList.getModel();
            for (int i = lm.getSize()-1; i >= 0; i--) {
                 if (toList.isSelectedIndex(i)) {
                     if (i == lm.getSize()-1)
                        continue;
                    Object up = lm.get(i+1);
                    Object dn = lm.get(i);
                    lm.set(i+1, dn);
                    lm.set(i, up);
                 }
            }
        }
//        else if (e.getSource() == down) {
////          TODO            
//        }
        else
            super.actionPerformed(e);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        if (e.getSource() == fromList.getSelectionModel()) {
            if (! fromList.isSelectionEmpty()) {
                toList.clearSelection();
            }
        }
        else if (e.getSource() == toList.getSelectionModel()) {
            if (! toList.isSelectionEmpty()) {
                fromList.clearSelection();
            }
//          TODO            
        }
        add.setEnabled(!fromList.isSelectionEmpty());
        remove.setEnabled(!toList.isSelectionEmpty());
        boolean move =  !toList.isSelectionEmpty();
        up.setEnabled(move);  
        down.setEnabled(move);  
    }


}
