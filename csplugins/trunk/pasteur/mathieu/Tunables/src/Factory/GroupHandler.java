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
	
	/*-------------------------------Constructor-----------------------------------*/
	public GroupHandler(Field f, Object o,Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title = t.description();
		try{
			this.group=(Group) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public JPanel getPanel() {
		return null;
	}
	
	public JPanel getOutputPanel() {
		handle();
		return null;
	}

	/*-------------------------------Set Group Object with the new "Collapsed" state-----------------------------------*/	
	public void handle() {
		collapsed = group.isCollapsed();
		try{
			f.set(o,group);
		}catch(Exception e){e.printStackTrace();}	
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