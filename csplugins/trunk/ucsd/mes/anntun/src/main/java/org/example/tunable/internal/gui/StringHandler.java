
package org.example.tunable.internal.gui;

import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.example.tunable.Tunable;

public class StringHandler extends AbstractGuiHandler {

	JTextField tf;

	public StringHandler(Field f, Object o, Tunable t) {
		super(f,o,t);

		panel = new JPanel();
		try {
		panel.add( new JLabel( t.description() ) );
		tf = new JTextField( (String)f.get(o), 20);
		tf.addActionListener( this );
		panel.add( tf );
		} catch (Exception e) { e.printStackTrace(); }
			
	}

	public void handle() {
		String s = tf.getText();
		try {
		if ( s != null )
			f.set(o,s);
		} catch (Exception e) { e.printStackTrace(); }
	}

	public String getState() {
		String s;
		try {
			s = (String)f.get(o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
	}
}
