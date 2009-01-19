
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
		this.title=t.description();
	}
	
	public void handle(){			
		try {
			f.set(o,jcb.isSelected());
		} catch(Exception e){e.printStackTrace();}
	}
	
	public JPanel getInputPanel(){
		JPanel inpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		inpane.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);
		jcb = new JCheckBox();
		jcb.setSelected(bool.booleanValue());
		inpane.add(jcb,BorderLayout.EAST);
		return inpane;
	}

	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		try{
			JCheckBox checkbox = new JCheckBox();
			f.set(o,jcb.isSelected());
			checkbox.setSelected((Boolean) f.get(o));
			outpane.add(checkbox,BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
	return outpane;
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