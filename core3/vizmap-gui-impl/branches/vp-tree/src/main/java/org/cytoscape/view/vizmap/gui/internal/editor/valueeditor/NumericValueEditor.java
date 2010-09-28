package org.cytoscape.view.vizmap.gui.internal.editor.valueeditor;

import java.awt.Component;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.swing.JOptionPane;

public class NumericValueEditor extends
		AbstractValueEditor<Number> {

	private static final String TITLE = "Enter New Number";
	private static final String MESSAGE = "Please enter new number";
	private static final String ERR_MESSAGE = "Not a valid number.";

	public NumericValueEditor(Class<? extends Number> type) {
		super(type);
	}

	public Number showEditor(Component parent, Number initialValue) {
		
		Object value = null;
		Number result = null;
		while(result == null) {
			value = JOptionPane.showInputDialog(parent, MESSAGE, TITLE, JOptionPane.OK_CANCEL_OPTION);
			
			// This means cancel.
			if(value == null)
				return null;
			
			
			BigDecimal number;
			try {
				number = new BigDecimal(value.toString());
				result = validate(number);
			} catch (NumberFormatException ne) {
				JOptionPane.showMessageDialog(editorDialog, ERR_MESSAGE, "Invalid Input!", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return result;
	}
	
	/**
	 * Check entered number is number or not.
	 * 
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <V extends Number> V validate (BigDecimal number) {
		
		// Check number type.
		if(type.equals(Double.class)) {
			Double d = number.doubleValue();
			return (V) d;
		} else if(type.equals(Float.class)) {
			Float f = number.floatValue();
			return (V) f;
		} else if(type.equals(Integer.class)) {
			Integer i = number.intValue();
			return (V) i;
		} else if(type.equals(Long.class)) {
			Long l = number.longValue();
			return (V) l;
		} else if(type.equals(Short.class)) {
			Short s = number.shortValue();
			return (V) s;
		} else if(type.equals(BigInteger.class)) {
			return (V) number.toBigInteger();
		} else {
			Double d = number.doubleValue();
			return (V) d;
		}
	}
}
