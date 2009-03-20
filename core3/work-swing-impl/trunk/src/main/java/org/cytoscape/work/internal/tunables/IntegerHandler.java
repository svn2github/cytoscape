package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;


public class IntegerHandler extends AbstractGuiHandler {

	JTextField jtf;
	Double value = null;
	Integer myInteger;
	String newline = System.getProperty("line.separator");
	
	
	protected IntegerHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myInteger=(Integer)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		panel.add(label,BorderLayout.WEST );
		try {
			jtf = new JTextField(f.get(o).toString(), 10);
		} catch (Exception e) { e.printStackTrace(); }
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		panel.add(jtf,BorderLayout.EAST);			
	}

	public void handle() {
		jtf.setBackground(Color.white);
		try{
			value = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
			jtf.setBackground(Color.red);
			try{
				value = Double.parseDouble(f.get(o).toString());
			}catch(Exception e){e.printStackTrace();}
		JOptionPane.showMessageDialog(null,"An Integer was Expected"+newline+"Value will be set to default = "+value.intValue(), "Error",JOptionPane.ERROR_MESSAGE);
		}
		try {
			f.set(o,value.intValue());
		} catch (Exception e) { e.printStackTrace();}
	}


    public String getState() {
		String s;
		try {
			s = f.get(o).toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
    }
}
