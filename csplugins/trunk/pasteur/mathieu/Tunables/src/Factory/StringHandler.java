package Factory;


import java.awt.BorderLayout;
import java.lang.reflect.*;
import javax.swing.*;

import GuiInterception.Guihandler;
import Tunable.Tunable;


public class StringHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	
	JTextField jtf;
	String title;
	String value;
	
	
	public StringHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();
		try{
			this.value = f.get(o).toString();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void handle(){
		try {
			f.set(o,jtf.getText());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	
	
	public JPanel getInputPanel(){
		JPanel inpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		inpane.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);
		try{
			jtf = new JTextField(value,11);
		}catch (Exception e){e.printStackTrace();}
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		inpane.add(jtf,BorderLayout.EAST);
		return inpane;
	}	


	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		try{
			f.set(o, jtf.getText());
			outpane.add(new JTextField(f.get(o).toString()),BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return outpane;
	}	
	
	public Tunable getTunable(){
		return t;
	}
	public Field getField(){
		return f;
	}
	public Object getObject(){
		return o;
	}
}
