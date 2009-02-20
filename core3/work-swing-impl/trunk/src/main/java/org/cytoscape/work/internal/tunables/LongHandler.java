package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;

public class LongHandler extends AbstractGuiHandler {

	JTextField jtf;
	Double value = null;
	Long myLong;
	String newline = System.getProperty("line.separator");


	public LongHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myLong=(Long)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		panel.add(label,BorderLayout.WEST);
		try {
			jtf = new JTextField( f.get(o).toString(), 10);
			jtf.addActionListener( this );
			jtf.setHorizontalAlignment(JTextField.RIGHT);
			panel.add(jtf,BorderLayout.EAST);
		} catch (Exception e) { e.printStackTrace(); }
			
	}

	public void handle() {
		try{
			jtf.setBackground(Color.white);
			value = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
			try{
				jtf.setBackground(Color.red);
				value = Double.parseDouble(f.get(o).toString());
				JOptionPane.showMessageDialog(null,"A long was Expected"+newline+"Value will be set to default = "+value.longValue(), "Error",JOptionPane.ERROR_MESSAGE);
			}catch(Exception e){e.printStackTrace();}
		}
		try {
			f.set(o,value.longValue());
		} catch (Exception e) { e.printStackTrace();}
	}

	
	public void returnPanel(){
		panel.removeAll();
		panel.add(new JLabel(t.description()));
		panel.add(new JTextField(Long.toString(value.longValue())));
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
