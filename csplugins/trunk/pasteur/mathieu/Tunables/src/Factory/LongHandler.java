package Factory;

import java.awt.BorderLayout;
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
	Long mylong;
	
	
	public LongHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();					
		try{
			this.mylong=(Long)f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void handle(){
		try{
			f.set(o, Long.parseLong(jtf.getText()));
		}catch(Exception e){e.printStackTrace();}
	}
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel(new BorderLayout());		
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		pane.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);
		jtf = new JTextField(mylong.toString(),11);
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		pane.add(jtf,BorderLayout.EAST);
		return pane;
	}



	public JPanel update(){
		JPanel result = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		result.add(jta,BorderLayout.WEST);
		mylong = Long.parseLong(jtf.getText());
		try{
			if(mylong!=null)f.set(o, mylong);
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
