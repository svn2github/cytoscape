package org.cytoscape.work.internal.tunables;

import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.AbstractFlexiblyBounded;

import java.awt.event.*;


/**
 * Handler for the type <i>FlexiblyBounded</i> of <code>Tunable</code>
 * 
 * @author pasteur
 *
 * @param <T> type of <code>AbstractFlexiblyBounded</code>
 */
@SuppressWarnings("unchecked")
public class FlexiblyBoundedHandler<T extends AbstractFlexiblyBounded> extends AbstractGuiHandler {

	private JTextField textField;
	private T flexiblyBounded;
	private final JLabel label;
	private  T flexiblyBoundedInit;
	
	
	
	/**
	 * Construction of the <code>Guihandler</code> for the <code>FlexiblyBounded</code> type
	 * 
	 * Creates a JTextField to display the value contained in the object and its information, and adds 2 buttons to modify the upper and lower bounds
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	public FlexiblyBoundedHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			flexiblyBoundedInit = (T)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
			flexiblyBoundedInit = null;
		}
		flexiblyBounded = flexiblyBoundedInit;
		panel = new JPanel();
		
		JButton up = new JButton( new SetAction("Set Upper Bound",panel,false));
		JButton low = new JButton( new SetAction("Set Lower Bound",panel,true));
		
		label = new JLabel();
		setLabelText();
		try {
			panel.add( label );
			textField = new JTextField( flexiblyBounded.getValue().toString(), 6);
			textField.addActionListener(this);
			panel.add(textField);
			panel.add(low);
			panel.add(up);
		} catch (Exception e) { e.printStackTrace(); }
			
	}

	
	/**
	 * displays information about the <code>Tunable</code>, and the bounds/value of the FlexiblyBounded object
	 */
	private void setLabelText() {
		label.setText( t.description() + " (min: " + flexiblyBounded.getLowerBound().toString() + "  max: " + flexiblyBounded.getUpperBound().toString() + ")" );
	}

	
	
	/**
	 * To set the value to the <code>FlexiblyBounded</code> object
	 * 
	 * The constraints of the bound values have to be respected : <code>lowerBound &lt; value &lt; upperBound</code> or <code>lowerBound &lti; value &lti; upperBound</code> or ....
	 */
	public void handle() {
		String value = textField.getText();
		try {
			flexiblyBounded.setValue(value);
		} catch (Exception e) { e.printStackTrace(); }
	}

	
	/**
	 * To get the current value of the <code>FlexiblyBounded</code> object
	 * @return the value of the <code>FlexiblyBounded</code> object
	 */
    public String getState() {
        return flexiblyBounded.getValue().toString();
    }

    
    
    /**
     * set the action related to buttons used to modify the upper and lower bounds of the FlexiblyBounded object : displays a Dialog box to enter the new value
     * @author pasteur
     */
	private class SetAction extends AbstractAction {
		JPanel par;
		boolean low;
		SetAction(String name,JPanel par,boolean low) {
			super(name);
			this.par = par;
			this.low = low;
		}
		public void actionPerformed(ActionEvent ae) {
			String value = JOptionPane.showInputDialog(par,"Enter the new bound value:");
			if ( value != null ) {
				if ( low )
					flexiblyBounded.setLowerBound(value);
				else
					flexiblyBounded.setUpperBound(value);

				setLabelText();
			}
		}
	}

	
	/**
	 * To reset the current value and the bounds of this <code>FlexiblyBoundedHandler</code>, and set it to the initial ones
	 */
	public void resetValue() {
		//need to be set
	}

}
