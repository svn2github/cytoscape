package Factory;


import java.awt.Color;
import java.lang.reflect.*;
import javax.swing.*;
import GuiInterception.Guihandler;
import TunableDefinition.Tunable;
import TunableDefinition.Tunable.Param;
import java.lang.Object;
import Slider.*;
import Command.*;



public class BoundedDoubleHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;	
	MySlider slider;
	
	Double upperbound;
	Double lowerbound;
	Double value;
	String title;
	Boolean useslider;
	Boolean available;
	Double handlevalue;
	BoundedDouble input;
	BoundedDouble output;
	
	public BoundedDoubleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		try{
			this.input=(BoundedDouble) f.get(o);	
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();
		this.value=input.getValue();
		this.available=t.available();
		this.upperbound=input.getUpperBound();
		this.lowerbound=input.getLowerBound();
		if(t.flag()==Param.UseSlider)this.useslider=true;
	}
	
	
	public void handle(){
		if(available==true){
			Number s = slider.getValue();
			handlevalue = s.doubleValue();
		}
		else handlevalue = value;
		try {
			output = new BoundedDouble(handlevalue,lowerbound,upperbound,true,true);
			if ( handlevalue != null ) f.set(o,output);
		} catch (Exception e) { e.printStackTrace(); }
	}

	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();		
		try{
			if(available==true){
				if(useslider==true  && lowerbound!=null && upperbound!=null){				
					slider = new MySlider(title,lowerbound.doubleValue(),upperbound.doubleValue(),value.doubleValue());
					pane.add(slider);
				}
			}
			else{
				jtf = new JTextField(value.toString());
				jtf.setEnabled(false);
				jtf.setBackground(Color.GRAY);
			}
		}catch (Exception e){e.printStackTrace();}
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
		else handlevalue =value;
		jtf = new JTextField(handlevalue.toString());
		JPanel result = new JPanel();
		result.add(jtf);
		return result;
	}
	
	
	public void cancel(){
		output = new BoundedDouble(value,lowerbound,upperbound,true,true);
		try{
			f.set(o,output);
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