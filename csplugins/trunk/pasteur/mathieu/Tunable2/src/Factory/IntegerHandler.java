package Factory;

import java.lang.reflect.*;
import javax.swing.*;
import java.awt.Color;
import GuiInterception.Guihandler;
import Tunable.Tunable;

public class IntegerHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf=new JTextField();
	String title;
	String value;
	Boolean available;
	Integer inte;

	
	
	public IntegerHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.available=t.available();
		try{
			this.value=f.get(o).toString();
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();
	}

	
	public void handle(){
		try {
				f.set(o,Integer.parseInt(jtf.getText()));
		} catch (Exception e) { e.printStackTrace();}
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
		if(available==true)	inte = Integer.parseInt(jtf.getText());
		else inte = Integer.parseInt(value);
		try{
			if(inte!=null)f.set(o, inte);
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