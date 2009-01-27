package Factory;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import javax.swing.*;

import GuiInterception.Guihandler;
import Tunable.Tunable;


public class BooleanHandler implements Guihandler{
	Field f;
	Tunable t;
	Object o;
	JCheckBox jcb;	
	String title;
	Boolean Init;
	boolean valueChanged=true;

	/*-------------------------------Constructor-----------------------------------*/	
	public BooleanHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();
		jcb = new JCheckBox();
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
		//Set the CheckBox with the Boolean Tunable
		try{
			Init = (Boolean) f.get(o);
			jcb.setSelected((Boolean)f.get(o));
		}catch(Exception e){e.printStackTrace();}
		jcb.addActionListener(new myActionListener());
		test2.add(jcb,BorderLayout.EAST);
		return inpane;
	}

	
	/*-------------------------------Get the Panel with the MODIFIED value-----------------------------------*/	
	public JPanel getOutputPanel(boolean changed) {
		JPanel outpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		//Handle the value that has been modified
		//handle();
		try{
			JCheckBox checkbox = new JCheckBox();
			if(changed==true)checkbox.setSelected((Boolean) f.get(o));
			else checkbox.setSelected(Init);
			//checkbox.setSelected((Boolean) f.get(o));
			outpane.add(checkbox,BorderLayout.EAST);
			checkbox.setEnabled(false);
		}catch(Exception e){e.printStackTrace();}
		valueChanged=true;
	return outpane;
	}
	
	
	/*-------------------------------Set Object with Tunable's new state-----------------------------------*/	
	public void handle(){			
		try {
			f.set(o,jcb.isSelected());
		} catch(Exception e){e.printStackTrace();}
	}
	
	
	private class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			handle();
		}
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


	public boolean valueChanged(){
		handle();
		try{
			if(f.get(o).equals(Init))valueChanged=false;
		}catch(Exception e){e.printStackTrace();}
		return valueChanged;
	}
}