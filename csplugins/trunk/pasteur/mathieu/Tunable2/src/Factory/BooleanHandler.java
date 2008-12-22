
package Factory;


import java.awt.Color;
import java.lang.reflect.*;

import javax.swing.*;

import GuiInterception.Guihandler;
import Tunable.Tunable;


public class BooleanHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;
	JCheckBox jcb;
	
	Boolean bool=null;	
	String value;
	Boolean available;
	String title;
	
	public BooleanHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.available = t.available();
		
		try{
			this.bool=(Boolean) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();
	}
	
	public void handle(){
//		status = jcb.isSelected();
//		
//		if(available!=true) status=Boolean.parseBoolean(value);
//			
//		try {
//			if(status!=null)f.set(o,status);
//		} catch(Exception e){e.printStackTrace();}
		}
		
	
	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		jcb = new JCheckBox(title,bool.booleanValue());
		if(available!=true){
			jcb.setBackground(Color.GRAY);
			jcb.setEnabled(false);
		}
		pane.add(jcb);
		return pane;
	}
	


	public JPanel update() {
		JPanel result = new JPanel();
		try{
			f.set(o, jcb.isSelected());
			result.add(new JCheckBox(title,(Boolean) f.get(o)));
		}catch(Exception e){e.printStackTrace();}
	return result;
	}
	
	

	public void cancel() {
		bool = Boolean.parseBoolean(value);
		try{
			f.set(o,bool);
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

	@Override
	public Class<?> getclass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
