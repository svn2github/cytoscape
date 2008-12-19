package Factory;

import GuiInterception.*;
import Tunable.*;
import java.lang.reflect.*;
import javax.swing.JPanel;
import Utils.*;
import Tunable.Tunable.Param;
import javax.swing.*;
import Sliders.*;

public class BoundedHandler<O extends Comparable<String>> implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	
	Bounded<String> boundedInObject;
	Bounded<String> boundedOutObject;
	Boolean useslider=false;
	JTextField jtf;
	mySlider slider;
	O upperBound;
	O lowerBound;
	String title;
	O value;
	Number result = null;
	
	
	@SuppressWarnings("unchecked")
	public BoundedHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		if(t.flag()==Param.DoubleSlider || t.flag()==Param.IntegerSlider) this.useslider=true;
		try{
			boundedInObject = (Bounded) f.get(o);
			boundedOutObject =  (Bounded) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		this.upperBound=(O) boundedInObject.getUpperBound();
		this.lowerBound=(O)boundedInObject.getLowerBound();
		this.title=f.getName();
	}
	
	


	@Override
	public void cancel() {
		try{
			f.set(o,boundedInObject);
		}catch(Exception e){e.printStackTrace();}
	}


	public JPanel getInputPanel() {
		JPanel inputPane = new JPanel();
		if(useslider==true){
			if(t.flag()==Param.DoubleSlider){
				slider = new mySlider((java.lang.String) title,Double.parseDouble((String) lowerBound),Double.parseDouble((String) upperBound),Double.parseDouble((String) lowerBound));
			}
			if(t.flag()==Param.IntegerSlider){
				slider = new mySlider((java.lang.String) title,Integer.parseInt((String) lowerBound),Integer.parseInt((String) upperBound),Integer.parseInt((String) lowerBound));
			}
		inputPane.add(slider);
		}
		else{
			jtf = new JTextField("",5);
			inputPane.add(jtf);
		}
		return inputPane;
	}


	
	
	public JPanel getresultpanel() {
		return null;
	}

	public void handle() {
		if(t.flag()==Param.DoubleSlider || t.flag()==Param.IntegerSlider) boundedOutObject.setValue(result.toString());
		else boundedOutObject.setValue((String)value); 
		try{
			f.set(o,boundedOutObject);
		}catch(Exception e){e.printStackTrace();}
	}


	@SuppressWarnings("unchecked")
	public JPanel update() {
		JPanel resultPane = new JPanel();
		if(t.flag()==Param.DoubleSlider){
			result=slider.getValue().doubleValue();
			//boundedOutObject.setValue(result.toString());
			jtf=new JTextField(result.toString());
		}
		if(t.flag()==Param.IntegerSlider){
			result=slider.getValue().intValue();
			//boundedOutObject.setValue(result.toString());
			jtf=new JTextField(result.toString());
		}
		if(t.flag()==Param.Double) {
			value =(O) jtf.getText();
			//boundedOutObject.setValue((String) value);
			jtf=new JTextField(value.toString());
		}
		if(t.flag()==Param.Integer) {
			value =(O) jtf.getText();
			//boundedOutObject.setValue((String)value);
			jtf=new JTextField(value.toString());
		}
		resultPane.add(jtf);
		return resultPane;
	}

	
	
	public Object getObject() {
		return o;
	}
	public Tunable getTunable() {
		return t;
	}
	public Field getField() {
		return f;
	}
	public Class<?> getclass() {
		return null;
	}


}