/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.Column;
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
 * Dynamic query for string columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class StringSearchDynamicQuery extends AbstractDynamicQuery {
    protected transient Pattern pattern;
    protected JTextField        textField;

    /**
     * Constructor.
     * @param col the column
     */
    public StringSearchDynamicQuery(Column col) {
        super(col);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        if (pattern == null || column.isValueUndefined(row))
            return false;
        String v = column.getValueAt(row);
        Matcher m = pattern.matcher(v);
        return !m.matches();
    }
    
    /**
     * {@inheritDoc}
     */
    public void update() {
        apply();
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        if (textField == null) {
            textField = new JTextField();
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            textField.addActionListener(new ActionListener() {
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
        return textField;
    }

    /**
     * Creator for this dynamic query type.
     */
    public static class Creator implements DynamicQueryFactory.Creator {
        /**
         * {@inheritDoc}
         */
        public DynamicQuery create(Column c, String type) {
            if (type == DynamicQueryFactory.QUERY_TYPE_SEARCH
                    || c instanceof StringColumn) {
                return new StringSearchDynamicQuery(c);
            }
            return null;
        }
    }

}
