/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.Column;
import infovis.column.FilterColumn;
import infovis.column.NumberColumn;
import infovis.metadata.ValueCategory;
import infovis.panel.DynamicQuery;

import java.text.Format;

import javax.swing.*;

/**
 * Class CategoricalDynamicQuery
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class CategoricalDynamicQuery extends AbstractDynamicQuery {
    protected NumberColumn numberColumn;

    protected FilterColumn filter;

    protected JList list;

    protected JScrollPane scrollPane;

    public CategoricalDynamicQuery(NumberColumn col) {
        super(col);
    }

    public void update() {
        if (list == null)
            return;
        if (numberColumn.getMinIndex() == -1
                || numberColumn.getMaxIndex() == -1) {
            if (list != null) {
                list.setEnabled(false);
            }
            return;
        }
        DefaultListModel model = (DefaultListModel) list.getModel();
        int min = (int)numberColumn.getDoubleMin();
        int max = (int)numberColumn.getDoubleMax();
        if (model.getSize() == (max - min))
            return;
        Format format = numberColumn.getFormat();
        model.clear();
        for (int v = min; v <= max; v++) {
            String value = (format == null) ? "" + v : "" + v; // TODO

            model.addElement(value);
        }
    }

    public void setColumn(Column column) {
        numberColumn = (NumberColumn) column; // will throw exception if needed
        super.setColumn(column);
    }

    public boolean isFiltered(int row) {
        if (column.isValueUndefined(row))
            return false;
        int v = numberColumn.getIntAt(row) 
        - (int)numberColumn.getDoubleMin(); // make it robust
        return !list.getSelectionModel().isSelectedIndex(v);
    }

    public JComponent getComponent() {
        if (scrollPane == null) {
            list = new JList();
            list.setModel(new DefaultListModel());
            scrollPane = new JScrollPane(list,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            update();
        }
        return scrollPane;
    }
    
    /**
     * @infovis.factory DynamicQueryFactory Categorical
     */
    public static class Creator implements DynamicQueryFactory.Creator {
        public DynamicQuery create(Column c, String type) {
            int category = ValueCategory.findValueCategory(c);
            if (c instanceof NumberColumn
                    && category == ValueCategory.TYPE_CATEGORIAL) {
                NumberColumn number = (NumberColumn) c;
                return new CategoricalDynamicQuery(number);
            }
            return null;
        }
    }
}