
package org.cytoscape.work.internal.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


public class StringHandler extends AbstractGuiHandler {

	JTextField jtf;
	boolean horizontal = false;
	
	public StringHandler(Field f, Object o, Tunable t) {
		super(f,o,t);

		for(Param par : t.alignment())if(par==Param.horizontal) horizontal = true;

		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		try {
			jtf = new JTextField( (String)f.get(o), 15);
		}catch (Exception e) {e.printStackTrace(); }
		jtf.setHorizontalAlignment(JTextField.RIGHT);

		if(horizontal==false){
			panel.add(label,BorderLayout.WEST);
			panel.add(jtf,BorderLayout.EAST);
		}
		else {
			panel.add(label,BorderLayout.NORTH);
			panel.add(jtf,BorderLayout.SOUTH);
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

	@Override
	public void resetValue() {
		// TODO Auto-generated method stub
		
	}
}
