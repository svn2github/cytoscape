/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.color;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Class ColorSchemeListCellRenderer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class ColorSchemeListCellRenderer extends
        DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list,
            Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
        ColorScheme cs = (ColorScheme)value;
        setText(cs.getName());
        return this;
    }
}
