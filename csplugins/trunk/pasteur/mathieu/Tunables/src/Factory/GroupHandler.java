package Factory;


import java.lang.reflect.*;
import javax.swing.*;
import GuiInterception.Guihandler;
import Tunable.Tunable;
import java.lang.Object;


public class GroupHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;	
	String title;
	
	
	public GroupHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=f.getName();
	}
	
	
	public void handle(){
	}

	
	public JPanel getInputPanel(){
		return null;
	}

	public JPanel update(){
		return null;
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