package Factory;


import java.awt.Color;
import java.lang.reflect.*;

import javax.swing.*;
import GuiInterception.Guihandler;
import Tunable.Tunable;
import java.lang.Object;
//import Properties.PropertiesImpl;;
import java.security.acl.Group;



public class GroupHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;	
	
	String title;
	Boolean available;

	
	
	public GroupHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();
		this.available=t.available();
	}
	
	
	public void handle(){
	}

	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();		
//		try{
//			if(available==true){
//				if(useslider==true  && lowerbound!=null && upperbound!=null){				
//					slider = new MySlider(title,lowerbound.doubleValue(),upperbound.doubleValue(),Double.parseDouble(f.get(o).toString()));
//					pane.add(slider);
//				}
//			}
//			else{
//				jtf = new JTextField(value);
//				jtf.setEnabled(false);
//				jtf.setBackground(Color.GRAY);
//			}
//		}catch (Exception e){e.printStackTrace();}
		return pane;
	}

	
	public JPanel getresultpanel(){
		JPanel result = new JPanel();
		try{
			jtf = new JTextField(f.get(o).toString());
			if(available==false){
				jtf.setBackground(Color.GRAY);
				jtf.setEnabled(false);
			}
		}catch (Exception e){e.printStackTrace();}		
		result.add(new JLabel(title));
		result.add(jtf);
		return result;
	}


	public JPanel update(){
		JPanel result = new JPanel();
		result.add(jtf);
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


	public Class<?> getclass() {
		return Group.class;
	}
}