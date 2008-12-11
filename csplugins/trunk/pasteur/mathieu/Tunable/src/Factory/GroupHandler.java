package Factory;


import java.awt.Color;
import java.lang.reflect.*;

import javax.swing.*;
import GuiInterception.Guihandler;
import TunableDefinition.Tunable;
import java.lang.Object;
import Properties.PropertiesImpl;
import Slider.*;



public class GroupHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;	
	MySlider slider;
	
	Double upperbound;
	Double lowerbound;
	String value;
	String title;
	Boolean useslider;
	Boolean available;
	Double handlevalue;

	
	
	public GroupHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.value=t.value();
		this.title=t.description();
		this.available=t.available();
		this.upperbound=t.upperbound();
		this.lowerbound=t.lowerbound();
		if(t.flag()==8)this.useslider=true;
	}
	
	
	public void handle(){
		if(available==true){
			Number s = slider.getValue();
			handlevalue = s.doubleValue();
		}
		else handlevalue = Double.parseDouble(value);
		try {
			if ( handlevalue != null ) f.set(o,handlevalue);
		} catch (Exception e) { e.printStackTrace(); }
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
		if(available==true){
			Number s = slider.getValue();
			handlevalue = s.doubleValue();
		}
		else handlevalue = Double.parseDouble(value);
		jtf = new JTextField(handlevalue.toString());
		JPanel result = new JPanel();
		result.add(jtf);
		return result;
	}
	
	
	public void cancel(){
	}
	
	
	@Override
	public Tunable getTunable() {
		// TODO Auto-generated method stub
		return t;
	}


	@Override
	public Field getField() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return null;
	}
}
