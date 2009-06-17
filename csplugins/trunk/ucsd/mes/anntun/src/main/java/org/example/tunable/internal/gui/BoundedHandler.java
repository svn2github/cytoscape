
package org.example.tunable.internal.gui;

import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.example.tunable.Tunable;
import org.example.tunable.util.AbstractBounded;

public class BoundedHandler<T extends AbstractBounded<?>> extends AbstractGuiHandler {

	JTextField tf;
	T b;

	@SuppressWarnings("unchecked")
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
