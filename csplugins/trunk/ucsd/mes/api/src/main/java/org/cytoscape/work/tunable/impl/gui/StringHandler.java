
package org.cytoscape.work.tunable.impl.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import org.cytoscape.work.tunable.*;
import org.cytoscape.work.Tunable;

public class StringHandler implements GuiHandler, ActionListener {

	Field f;
	Object o;
	Tunable t;
	JTextField tf;

	public StringHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		this.tf = null;
	}

	
	public JPanel getJPanel() {
		JPanel ret = new JPanel();
		try {
		ret.add( new JLabel( t.description() ) );
		tf = new JTextField( (String)f.get(o), 20);
		tf.addActionListener( this );
		ret.add( tf );
		} catch (IllegalAccessException e) { e.printStackTrace(); }
			
		return ret;
	}

	public void actionPerformed(ActionEvent ae) {
		handle();
	}

	public void handle() {
		if ( tf == null )
			return;
		String s = tf.getText();
		try {
		if ( s != null )
			f.set(o,s);
		} catch (Exception e) { e.printStackTrace(); }
	}
}
