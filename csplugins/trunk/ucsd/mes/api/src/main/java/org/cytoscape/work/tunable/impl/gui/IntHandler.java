
package org.cytoscape.work.tunable.impl.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import org.cytoscape.work.tunable.*;
import org.cytoscape.work.Tunable;

public class IntHandler implements GuiHandler, ActionListener {

	Field f;
	Object o;
	Tunable t;
	JTextField tf;

	public IntHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
	}

	
	public JPanel getJPanel() {
		JPanel ret = new JPanel();
		try {
		ret.add( new JLabel( t.description() ) );
		tf = new JTextField( Integer.toString(f.getInt(o)), 10);
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
		f.set(o,n);
		} catch (Exception e) { e.printStackTrace(); }
	}
}
