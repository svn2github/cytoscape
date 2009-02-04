
package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import org.example.tunable.*;
import org.example.tunable.util.*;

public class BoundedHandler<T extends AbstractBounded> extends AbstractGuiHandler {

	JTextField tf;
	T b;

	public BoundedHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
		this.b = (T)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
		}

		panel = new JPanel();
		try {
		panel.add( new JLabel( t.description() + " (max: " + b.getLowerBound().toString() + "  min: " + b.getUpperBound().toString() + ")" ) );
		tf = new JTextField( b.getValue().toString(), 10);
		tf.addActionListener( this );
		panel.add( tf );
		} catch (Exception e) { e.printStackTrace(); }
			
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
}
