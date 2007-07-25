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
import infovis.column.filter.InternalFilter;

import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * Panel for configuring the labeling of a visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class LabelPanel extends JPanel {
    Table table;
    FilteredColumnListModel model;
    JComboBox combo;
	
    public LabelPanel(Table table) {
	this.table = table;
	model = new FilteredColumnListModel(table);
	model.setFilter(InternalFilter.sharedInstance());
	combo = new JComboBox(model);
	add(combo);
    }
	
    public Column getSelectedColumn() {
	return (Column)model.getSelectedItem();
    }
	
    public void setSelectedColumn(Column column) {
	model.setSelectedItem(column);
    }
}
