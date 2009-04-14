package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;

public class LongHandler extends AbstractGuiHandler {

	private JTextField jtf;
	private Double value = null;
	private Long myLong;
	private String newline = System.getProperty("line.separator");
	private boolean horizontal=false;
	
	protected LongHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myLong=(Long)f.get(o);
		}catch(Exception e){e.printStackTrace();}

		jtf = new JTextField(myLong.toString(), 10);
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		jtf.setHorizontalAlignment(JTextField.RIGHT);

		for(Param par : t.alignment())if(par==Param.horizontal) horizontal=true;
		if(horizontal){
			panel.add(label,BorderLayout.NORTH);
			panel.add(jtf,BorderLayout.SOUTH);	
		}
		else{
			panel.add(label,BorderLayout.WEST );
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
		JOptionPane.showMessageDialog(null,"A long was Expected"+newline+"Value will be set to default = "+value.longValue(), "Error",JOptionPane.ERROR_MESSAGE);	
		try{
			jtf.setText(f.get(o).toString());
			jtf.setBackground(Color.white);
		}catch(Exception e){e.printStackTrace();}
		}
		try {
			f.set(o,value.longValue());
		} catch (Exception e) { e.printStackTrace();}
	}

	
	public void resetValue(){
		System.out.println("#########Value will be reset to initial value = " + myLong.longValue() + "#########");
		try{
			f.set(o,myLong);
		}catch(Exception e){e.printStackTrace();}
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
