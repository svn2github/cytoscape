
package org.example.tunable.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import org.example.tunable.*;
import org.example.tunable.util.*;

public class BoundedHandler implements GuiHandler, ActionListener {

	Field f;
	Object o;
	Tunable t;
	JTextField tf;
	Bounded b;
	Class<?> type;

	public BoundedHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		try {
		this.b = (Bounded)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
		}
		type = b.getType();
	}

	
	public JPanel getJPanel() {
		JPanel ret = new JPanel();
		try {
		ret.add( new JLabel( t.description() + " (max: " + b.getLowerBound().toString() + "  min: " + b.getUpperBound().toString() + ")" ) );
		if ( type == Integer.class || type == int.class )
			tf = new JTextField( ((Integer)b.getValue()).toString(), 10);
		else if ( type == Long.class || type == long.class )
			tf = new JTextField( ((Long)b.getValue()).toString(), 10);
		else if ( type == Double.class || type == double.class )
			tf = new JTextField( ((Double)b.getValue()).toString(), 10);
		else if ( type == Float.class || type == float.class )
			tf = new JTextField( ((Float)b.getValue()).toString(), 10);
		else 
			System.err.println(type.toString() + " is not supported by this handler");	

		tf.addActionListener( this );
		ret.add( tf );
		} catch (Exception e) { e.printStackTrace(); }
			
		return ret;
	}

	public void actionPerformed(ActionEvent ae) {
		handle();
	}

	public void handle() {
		String s = tf.getText();
		try {
		if ( type == Integer.class || type == int.class ) {
			int n = Integer.parseInt(s);
			b.setValue(n);
		} else if ( type == Long.class || type == long.class ) {
			long n = Long.parseLong(s);
			b.setValue(n);
		} else if ( type == Double.class || type == double.class ) {
			double n = Double.parseDouble(s);
			b.setValue(n);
		} else if ( type == Float.class || type == float.class ) {
			float n = Float.parseFloat(s);
			b.setValue(n);
		} else { 
			System.err.println(type.toString() + " is not supported for setting");	
		}

		} catch (Exception e) { e.printStackTrace(); }
	}
}
