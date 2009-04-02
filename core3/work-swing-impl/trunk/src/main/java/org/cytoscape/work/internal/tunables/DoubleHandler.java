package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


public class DoubleHandler extends AbstractGuiHandler {

	private JTextField jtf;
	private Double value = null;
	Double myDouble;
	private String newline = System.getProperty("line.separator");


	protected DoubleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myDouble=(Double)f.get(o);
			jtf = new JTextField(f.get(o).toString(), 10);
		}catch(Exception e){e.printStackTrace();}
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		jtf.setHorizontalAlignment(JTextField.RIGHT);

		for(Param par : t.alignment())if(par==Param.horizontal){
			panel.add(label,BorderLayout.NORTH);
			panel.add(jtf,BorderLayout.SOUTH);	
		}
		else{
			panel.add(label,BorderLayout.WEST);
			panel.add(jtf,BorderLayout.EAST);
		}
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
			JOptionPane.showMessageDialog(null,"A double was Expected"+newline+"Value will be set to default = "+value.doubleValue(), "Error",JOptionPane.ERROR_MESSAGE);
			try{
				jtf.setText(f.get(o).toString());
				jtf.setBackground(Color.white);
			}catch(Exception e){e.printStackTrace();}
		}
		try {
			f.set(o,value.doubleValue());
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
