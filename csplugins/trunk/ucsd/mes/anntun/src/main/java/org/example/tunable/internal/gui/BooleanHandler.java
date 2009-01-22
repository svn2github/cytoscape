
package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import org.example.tunable.*;

public class BooleanHandler extends AbstractGuiHandler {

	JCheckBox cb;

	public BooleanHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}
	
	public JPanel getJPanel() {
		JPanel ret = new JPanel();
		try {
		cb = new JCheckBox( t.description(), f.getBoolean(o));
		cb.addActionListener( this );
		ret.add( cb );
		} catch (Exception e) { e.printStackTrace(); }
			
		return ret;
	}

	public void handle() {
		try {
		f.set(o,cb.isSelected());
		} catch (Exception e) { e.printStackTrace(); }
	}
}
