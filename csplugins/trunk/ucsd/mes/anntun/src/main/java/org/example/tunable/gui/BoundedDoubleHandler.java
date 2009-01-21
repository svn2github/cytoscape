
package org.example.tunable.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import org.example.tunable.*;
import org.example.tunable.util.*;

public class BoundedDoubleHandler extends AbstractGuiHandler implements GuiHandler, ActionListener {

	JTextField tf;
	BoundedDouble b;

	public BoundedDoubleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
		this.b = (BoundedDouble)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
		}
	}

	
	public JPanel getJPanel() {
		JPanel ret = new JPanel();
		try {
		ret.add( new JLabel( t.description() + " (max: " + b.getLowerBound().toString() + "  min: " + b.getUpperBound().toString() + ")" ) );
		tf = new JTextField( ((Double)b.getValue()).toString(), 10);
		tf.addActionListener( this );
		ret.add( tf );
		} catch (Exception e) { e.printStackTrace(); }
			
		return ret;
	}

	public void handle() {
		String s = tf.getText();
		try {
		double n = Double.parseDouble(s);
		b.setValue(n);
		} catch (Exception e) { e.printStackTrace(); }
	}
}
