
package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import org.example.tunable.*;
import org.example.tunable.util.*;

public class BoundedIntegerHandler extends AbstractGuiHandler {

	JTextField tf;
	BoundedInteger b;

	public BoundedIntegerHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
		this.b = (BoundedInteger)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
		}

		panel = new JPanel();
		try {
		panel.add( new JLabel( t.description() + " (max: " + b.getLowerBound().toString() + "  min: " + b.getUpperBound().toString() + ")" ) );
		tf = new JTextField( ((Integer)b.getValue()).toString(), 10);
		tf.addActionListener( this );
		panel.add( tf );
		} catch (Exception e) { e.printStackTrace(); }
			
	}

	public void handle() {
		String s = tf.getText();
		try {
		int n = Integer.parseInt(s);
		b.setValue(n);
		} catch (Exception e) { e.printStackTrace(); }
	}

    public String getState() {
        return b.getValue().toString();
    }
}
