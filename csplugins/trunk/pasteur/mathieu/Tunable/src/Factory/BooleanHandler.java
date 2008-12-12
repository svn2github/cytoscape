/*
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
	
	Boolean status;
	Boolean available;
	String title;
	
	public BooleanHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.available = t.available();
		this.status = Boolean.parseBoolean(t.value());
		try{
			f.set(o,status);
		}catch(Exception e){e.printStackTrace();}
		this.title=t.description();
	}
	
	public void handle(){
		status = jcb.isSelected();
		
		try {
			f.set(o,status);
		} catch(Exception e){e.printStackTrace();}
		}
	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		//text = t.value();  OR  text = (f.get(o)).toString();  TAKE THE LAST STATE(OBJECT) BUT need to INITIALIZE the Object
		try{
			jcb = new JCheckBox(title,Boolean.parseBoolean(f.get(o).toString()));
		}catch(Exception e){e.printStackTrace();}
		if(available==false){
			jcb.setBackground(Color.GRAY);
			jcb.setEnabled(false);
		}
		pane.add(jcb);
		return pane;
	}

	
	public JPanel getresultpanel(){
		JPanel result = new JPanel();
		if(available==true){
			try{
					status = Boolean.parseBoolean(f.get(o).toString());
			}catch (Exception e){e.printStackTrace();}
			jcb = new JCheckBox(title,status);
		}
		else{
			jcb = new JCheckBox(title,status);
			jcb.setBackground(Color.GRAY);
			jcb.setEnabled(false);
		}
		result.add(jcb);
		return result;
	}



	public JPanel update() {
		status = jcb.isSelected();
		jcb = new JCheckBox(title,status);
		JPanel result = new JPanel();
		result.add(jcb);
		return result;
	}


	public void cancel() {
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
*/