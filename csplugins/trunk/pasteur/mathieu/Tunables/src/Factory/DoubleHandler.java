package Factory;

import java.awt.BorderLayout;
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
	
	
	public DoubleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();					
		try{
			this.doub=(Double)f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void handle(){
		try{
			f.set(o, Double.parseDouble(jtf.getText()));
		}catch(Exception e){e.printStackTrace();}
	}
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel(new BorderLayout());		
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		jtf = new JTextField(doub.toString());
		pane.add(jta,BorderLayout.WEST);
		pane.add(jtf,BorderLayout.EAST);
		return pane;
	}



	public JPanel update(){
		JPanel result = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		result.add(jta,BorderLayout.WEST);
		doub = Double.parseDouble(jtf.getText());
		try{
			if(doub!=null)f.set(o, doub);
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
