
package Factory;


import java.awt.BorderLayout;
import java.lang.reflect.*;
import javax.swing.*;

import GuiInterception.Guihandler;
import Tunable.Tunable;


public class BooleanHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JCheckBox jcb;
	Boolean bool=null;	
	String title;
	
	public BooleanHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		try{
			this.bool=(Boolean) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();
	}
	
	public void handle(){			
		try {
			f.set(o,jcb.isSelected());
		} catch(Exception e){e.printStackTrace();}
	}
		
	
	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(f.getName());
		jta.setBackground(null);
		pane.add(jta,BorderLayout.WEST);
		jcb = new JCheckBox();
		jcb.setSelected(bool.booleanValue());
		pane.add(jcb,BorderLayout.EAST);
		return pane;
	}
	


	public JPanel update() {
		JPanel result = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(f.getName());
		jta.setBackground(null);
		result.add(jta,BorderLayout.WEST);
		//bool = jcb.isSelected();
		try{
			JCheckBox checkbox = new JCheckBox();
			f.set(o,jcb.isSelected());
			checkbox.setSelected((Boolean) f.get(o));
			result.add(checkbox,BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
	return result;
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
