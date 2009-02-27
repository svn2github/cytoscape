
package org.cytoscape.work.internal.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;


public class StringHandler extends AbstractGuiHandler {

	JTextField jtf;

	public StringHandler(Field f, Object o, Tunable t) {
		super(f,o,t);

		panel = new JPanel(new BorderLayout());
		try {
			JLabel label = new JLabel(t.description());
			label.setFont(new Font(null, Font.PLAIN,12));
			panel.add(label,BorderLayout.WEST );
			jtf = new JTextField( (String)f.get(o), 15);
			jtf.setHorizontalAlignment(JTextField.RIGHT);
			//jtf.addActionListener(this);
			panel.add(jtf,BorderLayout.EAST);
		} catch (Exception e) {e.printStackTrace(); }
			
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
