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
import infovis.column.StringColumn;
import infovis.panel.DynamicQuery;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Class StringSearchDynamicQuery
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class StringSearchDynamicQuery extends JTextField
    implements DynamicQuery {
    protected Column column;
    protected FilterColumn filter;
    protected transient Pattern pattern; 
    
    public StringSearchDynamicQuery(Column col) {
        this.column = col;
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String regexp = e.getActionCommand();
                if (regexp.length() == 0)
                    pattern = null;
                else
                    pattern = Pattern.compile(regexp);
                apply();
            }
        });
    }

    public Column getColumn() {
        return column;
    }
    
    public void setFilterColumn(FilterColumn filter) {
        if (this.filter != null) {
            this.filter.removeDynamicQuery(this);
        }
        this.filter = filter;
        if (this.filter != null) {
            this.filter.addDynamicQuery(this);      
        }
    }

    public FilterColumn getFilterColumn() {
        return filter;
    }

    public void apply() {
        if (filter != null)
            filter.applyDynamicQuery(this, column.iterator());
    }


    public boolean isFiltered(int row) {
        if (pattern == null || column.isValueUndefined(row))
            return false;
        String v = column.getValueAt(row);
        Matcher m = pattern.matcher(v);
        return !m.matches();
    }

    

    public JComponent getComponent() {
        return this;
    }
    
    /**
     * @infovis.factory DynamicQueryFactory "String"
     */
    public static class Creator implements DynamicQueryFactory.Creator {
        public DynamicQuery create(Column c, String type) {
            if (type == DynamicQueryFactory.QUERY_TYPE_SEARCH
                    || c instanceof StringColumn) {
                return new StringSearchDynamicQuery(c);
            }
            return null;
        }
    }

}

