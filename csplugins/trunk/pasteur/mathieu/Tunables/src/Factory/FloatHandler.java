package Factory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import javax.swing.*;

import GuiInterception.Guihandler;
import Tunable.Tunable;
import java.lang.Object;


public class FloatHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;	
	String title;
	Float myfloat;
	Double value = null;
	String newline = System.getProperty("line.separator");
	
	public FloatHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();					
		try{
			this.myfloat=(Float)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		jtf = new JTextField(11);
	}
	
	
	public void handle(){
		try{
			value = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
				try{
					value = Double.parseDouble(f.get(o).toString());
					JOptionPane.showMessageDialog(null,"A float was Expected"+newline+"Value will be set to default = "+value, "Error",JOptionPane.ERROR_MESSAGE);
				}catch(Exception e){e.printStackTrace();}
			}
		try {
				f.set(o,value.floatValue());
		} catch (Exception e) { e.printStackTrace();}
	}
	
	public JPanel getInputPanel(){
		JPanel inpane = new JPanel(new BorderLayout());		
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		inpane.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);
		jtf.setText(myfloat.toString());
		jtf.addActionListener(new myActionListener());
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		inpane.add(jtf,BorderLayout.EAST);
		return inpane;
	}


	private class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			try{
				value = Double.parseDouble(jtf.getText());
				jtf.setBackground(Color.white);
			}catch(NumberFormatException nfe){
					jtf.setBackground(Color.red);
					try{
						jtf.setText(f.get(o).toString());
						JOptionPane.showMessageDialog(null, "A float is Expected"+newline+"Value will be set to default = "+f.get(o), "Error",JOptionPane.ERROR_MESSAGE);
					}catch(Exception e){e.printStackTrace();}
				}
		}
	}
	
	
	

	public JPanel getOutputPanel(){
		JPanel outpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		try{
			jtf.setBackground(Color.white);
			value = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
				jtf.setBackground(Color.red);
				try{
					jtf.setText(f.get(o).toString());
					value = Double.parseDouble(f.get(o).toString());
					JOptionPane.showMessageDialog(null, "A float is Expected"+newline+"Value will be set to default = "+value, "Error",JOptionPane.ERROR_MESSAGE);
				}catch(Exception e){e.printStackTrace();}
			}
		try{
			if(myfloat!=null)f.set(o,value.floatValue());
			outpane.add(new JTextField(f.get(o).toString()),BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return outpane;
	}

	
	public Tunable getTunable() {
		return t;
	}
	public Field getField() {
		return f;
	}
	public Object getObject() {
		return o;
	}
}
