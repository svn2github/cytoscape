
package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import org.example.tunable.*;
import org.example.tunable.util.*;


public class FlexiblyBoundedHandler<T extends AbstractFlexiblyBounded> extends AbstractGuiHandler {

	JTextField tf;
	final T b;
	final JLabel label;
	
	
	public FlexiblyBoundedHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		T bb; 
		try {
			bb = (T)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
			bb = null;
		}

		b = bb;

		panel = new JPanel();
		
		JButton up = new JButton( new SetAction("Set Upper Bound",panel,false));
		JButton low = new JButton( new SetAction("Set Lower Bound",panel,true));
		
		label = new JLabel();
		setLabelText();
		try {
			panel.add( label );
			tf = new JTextField( b.getValue().toString(), 6);
			tf.addActionListener( this );
			panel.add( tf );
			panel.add(low);
			panel.add(up);
		} catch (Exception e) { e.printStackTrace(); }
			
	}

	private void setLabelText() {
		label.setText( t.description() + " (min: " + b.getLowerBound().toString() + "  max: " + b.getUpperBound().toString() + ")" );
	}

	public void handle() {
		String s = tf.getText();
		try {
			b.setValue(s);
		} catch (Exception e) { e.printStackTrace(); }
	}

    public String getState() {
        return b.getValue().toString();
    }

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
					b.setLowerBound(value);
				else
					b.setUpperBound(value);

				setLabelText();
			}
		}
	}

}
