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


public class IntegerHandler implements Guihandler{
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;
	String title;
	Double value = null;
	String newline = System.getProperty("line.separator");
	
	/*-------------------------------Constructor-----------------------------------*/		
	public IntegerHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title = t.description();
		jtf = new JTextField(11);
	}

	/*-------------------------------Get the Panel with the INITIAL value-----------------------------------*/	
	public JPanel getPanel(){
		JPanel inpane = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		inpane.add(test1);
		inpane.add(test2);
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		test1.add(jta,BorderLayout.CENTER);
		jta.setBackground(null);
		jta.setEditable(false);
		//Set the JTextField with the initial Integer value
		try{
			//value = Double.parseDouble(f.get(o).toString());
			jtf.setText(((Integer)f.get(o)).toString());
		}catch(Exception e){e.printStackTrace();}
		jtf.addActionListener(new myActionListener());
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		test2.add(jtf,BorderLayout.EAST);
		return inpane;
	}
		
	
	/*-------------------------------Get the Panel with the MODIFIED value-----------------------------------*/		
	public JPanel getOutputPanel(){
		JPanel outpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		//handle the new value in the field
		handle();
		try{
			JTextField jtf2 = new JTextField(f.get(o).toString());
			jtf2.setEditable(false);
			outpane.add(jtf2,BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return outpane;
	}
	
	
	private class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			handle();
		}
	}
	

	/*-------------------------------Get the new value from the JTextField-----------------------------------*/	
	public void handle(){
		try{
			jtf.setBackground(Color.white);
			value = Double.parseDouble(jtf.getText());
		}catch(NumberFormatException nfe){
				//get the Text from the Field and set it to Integer format
				try{
					jtf.setBackground(Color.red);
					value = Double.parseDouble(f.get(o).toString());
					JOptionPane.showMessageDialog(null,"An Integer was Expected"+newline+"Value will be set to default = "+value, "Error",JOptionPane.ERROR_MESSAGE);
				}catch(Exception e){e.printStackTrace();}
			}
		//set the new value to the Integer object
		try {
				f.set(o,value.intValue());
		} catch (Exception e) { e.printStackTrace();}
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