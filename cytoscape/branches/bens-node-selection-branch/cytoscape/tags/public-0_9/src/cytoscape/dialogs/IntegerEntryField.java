// IntegerEntryField.java
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.*;
import y.view.*;
//---------------------------------------------------------------------------------------

public class IntegerEntryField extends JPanel {
    
    JTextField sizeField;
    JLabel sizeLabel;
    JFrame mainFrame;
    JPanel mainPanel;
    
    public IntegerEntryField (String fieldName, int defaultValue, int maxValue) {
	super(new GridLayout(1,2,10,10));
	
	sizeLabel = new JLabel(fieldName);
	sizeField = new JTextField(Integer.toString(defaultValue));
	sizeField.addFocusListener(new PositiveIntegerListener(defaultValue,maxValue));
	
	add(sizeLabel);
	add(sizeField);
    }
    
    public Integer getInteger() { return new Integer(sizeField.getText()); }
    public void setInteger(Integer newVal) { sizeField.setText(newVal.toString()); }
    public int getInt() { return Integer.parseInt(sizeField.getText()); }
    public void setInt(int newVal) { sizeField.setText(Integer.toString(newVal)); }
    
    public class PositiveIntegerListener implements FocusListener { 
	private int digits, maxval, defaultVal;
	private String maxvalString, defaultString;
	
	public PositiveIntegerListener(int defaultVal, int maxval) {
	    super();
	    this.maxval = maxval;
	    this.defaultVal = defaultVal;
	    this.maxvalString = Integer.toString(maxval);
	    this.defaultString = Integer.toString(defaultVal);
	    this.digits = this.maxvalString.length();
	}
	public void focusGained (FocusEvent e) {
	    //System.out.println("gained");
	    validate((JTextField)e.getSource());
	}
	public void focusLost (FocusEvent e) {
	    //System.out.println("lost");
	    validate((JTextField)e.getSource());
	}
	private void validate(JTextField field) {
	    String fieldStr = field.getText();
	    fieldStr = fieldStr.replaceAll("[^0-9]",""); // ditch all non-numeric
	    if(fieldStr.length()>0) {
		if(fieldStr.length()>digits) {
		    field.setText(maxvalString);
		}
		else {
		    //System.out.println(" length " + fieldStr.length());
		    try {
			int val = Integer.parseInt(fieldStr);
			if(val<=0) {
			    field.setText(defaultString);
			}
			else if(val>maxval) {
			    field.setText(maxvalString);
			}
			else {
			    field.setText(fieldStr);
			}
		    }
		    catch (NumberFormatException nfe) {
			System.out.println("Not an integer: " + fieldStr);
			field.setText(defaultString);
		    }
		}
	    }  // if gt 0
	    else {
		field.setText(defaultString);
	    }  // if gt 0 (else)
	}
	
    } // PositiveIntegerListener
    public JTextField getField() {
	return sizeField;
    }
    public JLabel getLabel() {
	return sizeLabel;
    }
    
}//class IntegerEntryField
