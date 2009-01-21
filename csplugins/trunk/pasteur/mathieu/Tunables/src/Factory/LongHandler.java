package Factory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import javax.swing.*;

import GuiInterception.Guihandler;
import Tunable.Tunable;
import java.lang.Object;


public class LongHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;	
	String title;
	Long myLong;
	Double value = null;
	
	
	String newline=System.getProperty("line.separator");
	
	
	public LongHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();					
		try{
			this.myLong=(Long)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		jtf = new JTextField(11);
	}
	
	
	public void handle(){
		try{
			value = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
				try{
					value = Double.parseDouble(f.get(o).toString());
					JOptionPane.showMessageDialog(null,"A Long was Expected"+newline+"Value will be set to default = "+value, "Error",JOptionPane.ERROR_MESSAGE);
				}catch(Exception e){e.printStackTrace();}
			}
		try {
				f.set(o,value.longValue());
		} catch (Exception e) { e.printStackTrace();}
	}
	
	public JPanel getInputPanel(){
//		JPanel inpane = new JPanel(new BorderLayout());
		JPanel inpane = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		inpane.add(test1);
		inpane.add(test2);
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
//		inpane.add(jta);
		test1.add(jta,BorderLayout.CENTER);
		jta.setBackground(null);
		jta.setEditable(false);
		jtf.setText(myLong.toString());
		jtf.addActionListener(new myActionListener());
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		test2.add(jtf,BorderLayout.EAST);
//		inpane.add(jtf,BorderLayout.EAST);
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
						JOptionPane.showMessageDialog(null, "A long is Expected"+newline+"Value will be set to default = "+f.get(o), "Error",JOptionPane.ERROR_MESSAGE);
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
					JOptionPane.showMessageDialog(null, "A long is Expected"+newline+"Value will be set to default = "+value, "Error",JOptionPane.ERROR_MESSAGE);
				}catch(Exception e){e.printStackTrace();}
			}
		try{
			if(myLong!=null)f.set(o,value.longValue());
			outpane.add(new JTextField(f.get(o).toString()),BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return outpane;
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