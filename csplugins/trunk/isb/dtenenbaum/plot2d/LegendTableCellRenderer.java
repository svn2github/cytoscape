//LegendTableCellRenderer

package csplugins.isb.dante.plot2d;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 * Table cell renderer for the row legend table. Taken almost verbatim
 * from <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/example-1dot4/ColorRenderer.java">
 * http://java.sun.com/docs/books/tutorial/uiswing/components/example-1dot4/ColorRenderer.java</a>.
 * 
 * @author Dan Tenenbaum (sort of)
 */
public class LegendTableCellRenderer extends JLabel
                           implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public LegendTableCellRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(
                            JTable table, Object vec,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        Vector newVec = (Vector)vec;
        Color newColor = (Color)newVec.get(0);
        String tooltip = (String)newVec.get(1);
        
        if (!"".equals(tooltip))
        	setToolTipText(tooltip);
        
        
        
        setBackground(newColor);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
        
        return this;
    }
} //LegendTableCellRenderer 