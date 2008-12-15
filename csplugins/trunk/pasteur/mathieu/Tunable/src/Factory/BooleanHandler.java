
package Factory;


import java.awt.Color;
import java.lang.reflect.*;

import javax.swing.*;

import GuiInterception.Guihandler;
import TunableDefinition.Tunable;


public class BooleanHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;
	JCheckBox jcb;
	
	Boolean status=null;	
	String value;
	Boolean available;
	String title;
	
	public BooleanHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.available = t.available();
		
		try{
			this.value=f.get(o).toString();
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();
	}
	
	public void handle(){
		status = jcb.isSelected();
		
		if(available!=true) status=Boolean.parseBoolean(value);
			
		try {
			if(status!=null)f.set(o,status);
		} catch(Exception e){e.printStackTrace();}
		}
		
	
	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		jcb = new JCheckBox(title,Boolean.parseBoolean(value));
		if(available!=true){
			jcb.setBackground(Color.GRAY);
			jcb.setEnabled(false);
		}
		pane.add(jcb);
		return pane;
	}

	
	public JPanel getresultpanel(){
		JPanel result = new JPanel();
		try{
			status = Boolean.parseBoolean(f.get(o).toString());
			jcb = new JCheckBox(title,status);
			if(available!=true){
				jcb.setBackground(Color.GRAY);
				jcb.setEnabled(false);
			}
		}catch (Exception e){e.printStackTrace();}
		result.add(jcb);
		return result;
	}
	
	
	
	


	public JPanel update() {
		JPanel result = new JPanel();
		if(available==true){
			status = jcb.isSelected();
		}
		result.add(new JCheckBox(title,status));
		return result;
	}	
	
	

	public void cancel() {
		status = Boolean.parseBoolean(value);
		try{
			f.set(o,status);
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
