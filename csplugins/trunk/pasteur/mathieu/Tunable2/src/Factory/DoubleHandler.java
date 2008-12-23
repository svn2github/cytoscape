
package Factory;


import java.awt.Color;
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
	
	String value;
	String title;
	Boolean available;
	Double doub;

	
	
	public DoubleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=f.getName();					
		this.available=t.available();
		try{
			this.value=f.get(o).toString();
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void handle(){
		try{
			f.set(o, Double.parseDouble(jtf.getText()));
		}catch(Exception e){e.printStackTrace();}
	}
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();		
		jtf = new JTextField(value);
		if(available!=true){
			jtf.setEnabled(false);
			jtf.setBackground(Color.GRAY);
		}
		pane.add(jtf);
		return pane;
	}



	public JPanel update(){
		JPanel result = new JPanel();
		if(available==true)	doub = Double.parseDouble(jtf.getText());
		else doub = Double.parseDouble(value);
		try{
			if(doub!=null)f.set(o, doub);
			result.add(new JTextField(f.get(o).toString()));
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
	public Class<?> getclass() {
		return null;
	}

}
