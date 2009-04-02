package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


public class StringHandler extends AbstractGuiHandler {

	private JTextField jtf;

	protected StringHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			jtf = new JTextField( (String)f.get(o), 15);
		} catch (Exception e) {e.printStackTrace(); }
		
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		
		for(Param par : t.alignment())if(par==Param.horizontal){
			panel.add(label,BorderLayout.NORTH);
			panel.add(jtf,BorderLayout.SOUTH);	
		}
		else{
			panel.add(label,BorderLayout.WEST );
			panel.add(jtf,BorderLayout.EAST);
		}
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
