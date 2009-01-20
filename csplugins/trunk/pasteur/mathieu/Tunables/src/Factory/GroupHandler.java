package Factory;

import java.lang.reflect.Field;

import javax.swing.JPanel;

import GuiInterception.Guihandler;
import Tunable.Tunable;



public class GroupHandler implements Guihandler{
	
	Tunable t;
	Field f;
	Object o;
	String title;
	
	
	public GroupHandler(Field f, Object o,Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title = t.description();
	}
	
	
	public JPanel getInputPanel() {
		JPanel test = new JPanel();
		// TODO Auto-generated method stub
		return test;
	}
	public JPanel getOutputPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	public void handle() {
		// TODO Auto-generated method stub
		
	}

	
	public Field getField() {
		return f;
	}
	public Tunable getTunable() {
		return t;
	}
	public Object getObject() {
		return o;
	}
}