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


public class DoubleHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;	
	String title;
	Double doub;
	Double val = null;
	String newline=System.getProperty("line.separator");
	
	
	
	public DoubleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();					
		try{
			this.doub=(Double)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		jtf = new JTextField(11);
	}
	
	
	public void handle(){
		try{
			val = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
				try{
					val = Double.parseDouble(f.get(o).toString());
					JOptionPane.showMessageDialog(null,"A double was Expected"+newline+"Value will be set to default = "+val, "Error",JOptionPane.ERROR_MESSAGE);
				}catch(Exception e){e.printStackTrace();}
			}
		try {
				f.set(o,val.doubleValue());
		} catch (Exception e) { e.printStackTrace();}
	}
	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel(new BorderLayout());		

		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		pane.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);
		jtf.setText(doub.toString());
		jtf.addActionListener(new myActionListener());
		
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		pane.add(jtf,BorderLayout.EAST);
		return pane;
	}
	
	
	private class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			try{
				val = Double.parseDouble(jtf.getText());
				jtf.setBackground(Color.white);
			}catch(NumberFormatException nfe){
					jtf.setBackground(Color.red);
					
					try{
						jtf.setText(f.get(o).toString());
						JOptionPane.showMessageDialog(null, "A double is Expected"+newline+"Value will be set to default = "+f.get(o), "Error",JOptionPane.ERROR_MESSAGE);
					}catch(Exception e){e.printStackTrace();}
				}
		}
	}
	


	public JPanel update(){
		JPanel result = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		result.add(jta,BorderLayout.WEST);
		try{
			jtf.setBackground(Color.white);
			val = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
				jtf.setBackground(Color.red);
				//result.setBackground(Color.red);
				try{
					jtf.setText(f.get(o).toString());
					val = Double.parseDouble(f.get(o).toString());
					JOptionPane.showMessageDialog(null, "A double is Expected"+newline+"Value will be set to default = "+val, "Error",JOptionPane.ERROR_MESSAGE);
				}catch(Exception e){e.printStackTrace();}
			}
		try{
			if(doub!=null)f.set(o,val.doubleValue());
			//jtf.setColumns(0);
			//jtf.setText(f.get(o).toString());
			result.add(new JTextField(f.get(o).toString()),BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return result;
	}
		
	
	public void	setValue(Object object){
		try{
			f.set(o, object);
		}catch(Exception e){e.printStackTrace();}
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
