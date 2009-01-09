
package org.example.tunable.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import org.example.tunable.*;
import org.example.tunable.util.*;

public class BoundedIntegerHandler implements GuiHandler, ActionListener {

	Field f;
	Object o;
	Tunable t;
	JTextField tf;
	BoundedInteger b;

	public BoundedIntegerHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		try {
		this.b = (BoundedInteger)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
		}
	}

	
	public JPanel getJPanel() {
		JPanel ret = new JPanel();
		try {
		ret.add( new JLabel( t.description() + " (max: " + b.getLowerBound().toString() + "  min: " + b.getUpperBound().toString() + ")" ) );
		tf = new JTextField( ((Integer)b.getValue()).toString(), 10);
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
		int n = Integer.parseInt(s);
		b.setValue(n);
		} catch (Exception e) { e.printStackTrace(); }
	}
}
