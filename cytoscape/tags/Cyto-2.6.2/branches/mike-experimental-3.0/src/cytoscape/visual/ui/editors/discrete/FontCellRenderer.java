package cytoscape.visual.ui.editors.discrete;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;


/**
 * DOCUMENT ME!
 *
 * @author kono
 */
public class FontCellRenderer extends DefaultCellRenderer {
	private final static long serialVersionUID = 120233986931967L;
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

        if ((value != null) && value instanceof Font) {
            final Font font = (Font) value;
            final Font modFont = new Font(
                    font.getFontName(),
                    font.getStyle(),
                    12);

            this.setFont(modFont);
            this.setText(modFont.getFontName());
        } else
            this.setValue(null);

        return this;
    }
}
