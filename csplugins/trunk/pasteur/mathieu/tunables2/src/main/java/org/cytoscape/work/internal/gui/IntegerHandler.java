package org.cytoscape.work.internal.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


public class IntegerHandler extends AbstractGuiHandler {

	JTextField jtf;
	Double value = null;
	Integer myInteger;
	String newline = System.getProperty("line.separator");
	boolean horizontal=false;

	public IntegerHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myInteger=(Integer)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel = new JPanel(new BorderLayout());
		for(Param par : t.alignment())if(par==Param.horizontal)horizontal=true;	
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));

		try {
			jtf = new JTextField(f.get(o).toString(), 10);
			//jtf.addActionListener( this );
		} catch (Exception e) { e.printStackTrace(); }
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		if(horizontal==false){
			panel.add(label,BorderLayout.WEST);
			panel.add(jtf,BorderLayout.EAST);
		}
		else{
			panel.add(label,BorderLayout.NORTH);
			panel.add(jtf,BorderLayout.SOUTH);
		}
			
	}

	public void handle() {
		try{
			jtf.setBackground(Color.white);
			value = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
			try{
				jtf.setBackground(Color.red);
				value = Double.parseDouble(f.get(o).toString());
				JOptionPane.showMessageDialog(null,"An Integer was Expected"+newline+"Value will be set to default = "+value.intValue(), "Error",JOptionPane.ERROR_MESSAGE);
			}catch(Exception e){e.printStackTrace();}
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

	@Override
	public void resetValue() {
		// TODO Auto-generated method stub
		
	}
}
