package org.example.tunable.internal.gui;

import java.lang.reflect.*;
import javax.swing.*;
import org.example.tunable.*;

public class IntHandler extends AbstractGuiHandler {

	JTextField tf;

	public IntHandler(Field f, Object o, Tunable t) {
		super(f,o,t);

		panel = new JPanel();

		try {
			panel.add( new JLabel( t.description() ) );
			tf = new JTextField( Integer.toString(f.getInt(o)), 10);
			tf.addActionListener( this );
			panel.add( tf );
		} catch (Exception e) { e.printStackTrace(); }
	}

	
	public void handle() {
		String s = tf.getText();
		try {
			f.set(o,Integer.parseInt(s));
		} catch (Exception e) { e.printStackTrace(); }
	}

    public String getState() {
		String s;
		try {
        s  = Integer.toString( f.getInt(o) ); 
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
    }
}
