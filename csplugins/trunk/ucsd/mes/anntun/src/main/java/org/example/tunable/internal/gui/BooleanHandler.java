
package org.example.tunable.internal.gui;

import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.example.tunable.Tunable;

public class BooleanHandler extends AbstractGuiHandler {

	JCheckBox cb;

	public BooleanHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	
		panel = new JPanel();
		try {
		cb = new JCheckBox( t.description(), f.getBoolean(o));
		cb.addActionListener( this );
		panel.add( cb );
		} catch (Exception e) { e.printStackTrace(); }
	}

	public void handle() {
		try {
		f.set(o,cb.isSelected());
		} catch (Exception e) { e.printStackTrace(); }
	}

	public String getState() {
		String s;
		try {
		s = Boolean.toString(f.getBoolean(o));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
	}
}
