//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.ui;
//--------------------------------------------------------------------------
import java.awt.*;
import java.awt.font.*;
import javax.swing.*;
//--------------------------------------------------------------------------
/**
 * FontRenderer describes a class that renders each font name in a
 * {@link FontChooser}
 * JList or JComboBox in the face specified.
 */
public class FontRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent (JList list,
						   Object value,
						   int index,
						   boolean isSelected,
						   boolean cellHasFocus) {
	setComponentOrientation(list.getComponentOrientation());
	if (isSelected) {
	    setBackground(list.getSelectionBackground());
	    setForeground(list.getSelectionForeground());
	}
	else {
	    setBackground(list.getBackground());
	    setForeground(list.getForeground());
	}
	
	setEnabled(list.isEnabled());

	// just allow a ClassCastException to be thrown if the renderer is not
	// called correctly. Always display in 12 pt.
	if (value instanceof Font) {
	    Font fontValue = ((Font) value).deriveFont(12F);
	    setFont(fontValue);
	    setText(fontValue.getFontName());
	}
	else {
	    setFont(list.getFont());
	    setText((value == null) ? "" : value.toString());
	}
	setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

	return this;
    }
}
