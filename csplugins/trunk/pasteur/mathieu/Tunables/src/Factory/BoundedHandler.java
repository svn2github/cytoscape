package Factory;

import GuiInterception.*;
import Tunable.*;

import java.awt.BorderLayout;
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
	
	Bounded<String> boundedObject;
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
			boundedObject = (Bounded) f.get(o);
			this.upperBound=(O) boundedObject.getUpperBound();
			this.lowerBound=(O)boundedObject.getLowerBound();
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();
	}



	public JPanel getInputPanel() {
		JPanel inputPane = new JPanel(new BorderLayout());
		
		if(useslider==true){
			if(t.flag()==Param.DoubleSlider){
				Double initvalue = Double.parseDouble((String)lowerBound)+(Double.parseDouble((String)upperBound)-Double.parseDouble((String) lowerBound))/2;
				slider = new mySlider((java.lang.String) title,Double.parseDouble((String) lowerBound),Double.parseDouble((String) upperBound),initvalue.doubleValue());
			}
			if(t.flag()==Param.IntegerSlider){
				Integer initvalue = Integer.parseInt((String)lowerBound)+(Integer.parseInt((String)upperBound)-Integer.parseInt((String) lowerBound))/2;
				slider = new mySlider((java.lang.String) title,Integer.parseInt((String) lowerBound),Integer.parseInt((String) upperBound),initvalue.intValue());
			}
		inputPane.add(slider,BorderLayout.EAST);
		}
		else{
			jtf = new JTextField(5);
			inputPane.add(jtf,BorderLayout.EAST);
		}
		JTextArea jta = new JTextArea(t.flag().toString());
		jta.setBackground(null);
		inputPane.add(jta,BorderLayout.WEST);
		return inputPane;
	}


	@SuppressWarnings("unchecked")
	public void handle() {
		if(t.flag() == Param.DoubleSlider){
			result=slider.getValue().doubleValue();
			boundedObject.setValue(result.toString(),Double.class);
		}
		if(t.flag() == Param.IntegerSlider){
			result=slider.getValue().intValue();
			boundedObject.setValue(result.toString(),Integer.class);
		}
		if(t.flag() == Param.Double){
			if(jtf.getText().isEmpty()==false){
				value =(O) jtf.getText();
				boundedObject.setValue((String) value,Double.class);
			}
		}
		if(t.flag() == Param.Integer){
			if(jtf.getText().isEmpty()==false){
				value =(O) jtf.getText();
				boundedObject.setValue((String) value,Integer.class);
			}
		}
			
		try{
			f.set(o,boundedObject);
		}catch(Exception e){e.printStackTrace();}
	}
	

	@SuppressWarnings("unchecked")
	public JPanel update() {
		JPanel resultPane = new JPanel();
		if(t.flag() == Param.DoubleSlider){
			result=slider.getValue().doubleValue();
			boundedObject.setValue(result.toString(),Double.class);
		}
		if(t.flag() == Param.IntegerSlider){
			result=slider.getValue().intValue();
			boundedObject.setValue(result.toString(),Integer.class);
		}
		if(t.flag() == Param.Double){
			if(jtf.getText().isEmpty()==false){
				value =(O) jtf.getText();
				boundedObject.setValue((String) value,Double.class);
			}
		}
		if(t.flag() == Param.Integer){
			if(jtf.getText().isEmpty()==false){
				value =(O) jtf.getText();
				boundedObject.setValue((String) value,Integer.class);
			}
		}
		try{
			f.set(o,boundedObject);
			resultPane.add(new JTextField(boundedObject.getValue()));
		}catch(Exception e){e.printStackTrace();}
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

}