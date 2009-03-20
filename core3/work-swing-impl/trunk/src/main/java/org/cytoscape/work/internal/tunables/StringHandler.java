package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;


public class StringHandler extends AbstractGuiHandler {

	private JTextField jtf;

	protected StringHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		panel.add(label,BorderLayout.WEST );
		try {
			jtf = new JTextField( (String)f.get(o), 15);
		} catch (Exception e) {e.printStackTrace(); }
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		panel.add(jtf,BorderLayout.EAST);			
	}

	public void handle() {
		String s = jtf.getText();
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
