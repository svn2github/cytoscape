package cytoscape.visual.ui.editors.discrete;

import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTable;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.icon.VisualPropertyIcon;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class ShapeCellRenderer extends DefaultCellRenderer {
    private final Map<Object, Icon> icons;
    private VisualPropertyType type;

    /**
     * Creates a new ShapeCellRenderer object.
     *
     * @param type DOCUMENT ME!
     */
    public ShapeCellRenderer(VisualPropertyType type) {
        this.type = type;
        icons = type.getVisualProperty().getIconSet();
    }

    /**
         * DOCUMENT ME!
         *
         * @param table
         *            DOCUMENT ME!
         * @param value
         *            DOCUMENT ME!
         * @param isSelected
         *            DOCUMENT ME!
         * @param hasFocus
         *            DOCUMENT ME!
         * @param row
         *            DOCUMENT ME!
         * @param column
         *            DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        if (value != null) {
            final VisualPropertyIcon shapeIcon = (VisualPropertyIcon) icons.get(value);

            if (shapeIcon != null) {
                shapeIcon.setIconHeight(16);
                shapeIcon.setIconWidth(16);

                this.setIcon(shapeIcon);
            }

            this.setIconTextGap(10);
            this.setText(value.toString());
        } else {
            this.setIcon(null);
            this.setText(null);
        }

        return this;
    }
}
