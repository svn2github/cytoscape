package cytoscape.visual.ui.editors.discrete;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import com.l2fprod.common.beans.editor.DoublePropertyEditor;

public class CyDoublePropertyEditor extends DoublePropertyEditor {

	private Object currentValue;
	/**
     * Creates a new CyStringPropertyEditor object.
     */
    public CyDoublePropertyEditor() {
    	super();
    	
    	
        ((JTextField) editor).addFocusListener(
            new FocusListener() {
                public void focusGained(FocusEvent arg0) {
                	setCurrentValue();
                }

                public void focusLost(FocusEvent arg0) {
                		
                	checkChange();
                }
            });
    }
    
    private void setCurrentValue() {
    	this.currentValue = super.getValue();
    }
    
    private void checkChange() {
    	
    	firePropertyChange(
        		currentValue,
            super.getValue());
    }
	
	
}
