package Factory;

import java.awt.BorderLayout;
import java.lang.reflect.*;
import javax.swing.*;
import GuiInterception.Guihandler;
import Tunable.Tunable;


public class IntegerHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf=new JTextField();
	String title;
	Integer inte;

	
	
	public IntegerHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		try{
			this.inte=(Integer)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		this.title=t.description();
	}


	public void handle(){
		try {
				f.set(o,Integer.parseInt(jtf.getText()));
		} catch (Exception e) { e.printStackTrace();}
	}

	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		pane.add(jta,BorderLayout.WEST);
		jtf = new JTextField(inte.toString(),15);		
		pane.add(jtf,BorderLayout.EAST);
		return pane;
	}
	
	
	public JPanel update(){
		JPanel result = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		result.add(jta,BorderLayout.WEST);
		try{
			if(inte!=null)f.set(o, Integer.parseInt(jtf.getText()));
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