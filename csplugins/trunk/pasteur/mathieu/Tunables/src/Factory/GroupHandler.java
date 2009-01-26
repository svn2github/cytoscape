package Factory;

import java.lang.reflect.Field;

import javax.swing.JPanel;

import GuiInterception.Guihandler;
import Tunable.Tunable;
import Utils.Group;



public class GroupHandler implements Guihandler{
	
	Tunable t;
	Field f;
	Object o;
	String title;
	Group group;
	boolean collapsed;
	
	public GroupHandler(Field f, Object o,Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title = t.description();
		try{
			this.group=(Group) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public JPanel getInputPanel() {
		JPanel test = new JPanel();
		// TODO Auto-generated method stub
		return test;
	}
	public JPanel getOutputPanel() {
		collapsed = group.isCollapsed();
		try{
			f.set(o,group);
		}catch(Exception e){e.printStackTrace();}
		// TODO Auto-generated method stub
		return null;
	}
	public void handle() {
		collapsed = group.isCollapsed();
		try{
			f.set(o,group);
		}catch(Exception e){e.printStackTrace();}
		// TODO Auto-generated method stub
		
	}


	
	public Field getField() {
		return f;
	}
	public Tunable getTunable() {
		return t;
	}
	public Object getObject() {
		return group;
	}
}