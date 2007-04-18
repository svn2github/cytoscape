package cytoscape.visual.ui.editors;

import com.l2fprod.common.beans.editor.StringPropertyEditor;
import com.l2fprod.common.swing.LookAndFeelTweaks;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class CyStringPropertyEditor extends StringPropertyEditor {
    private Object currentValue;

    /**
     * Creates a new CyStringPropertyEditor object.
     */
    public CyStringPropertyEditor() {
        editor = new JTextField();
        ((JTextField) editor).setBorder(LookAndFeelTweaks.EMPTY_BORDER);

        ((JTextField) editor).addFocusListener(
            new FocusListener() {
                public void focusGained(FocusEvent arg0) {
                    currentValue = ((JTextField) editor).getText();
                }

                public void focusLost(FocusEvent arg0) {
                    firePropertyChange(
                        currentValue,
                        ((JTextField) editor).getText());
                }
            });
    }
}
