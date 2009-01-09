package Factory;

import GuiInterception.*;
import Tunable.*;
import java.awt.BorderLayout;
import java.lang.reflect.*;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import Utils.*;
import Tunable.Tunable.Param;
import javax.swing.*;


public class BoundedHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	
	Bounded boundedObject;
	Boolean useslider=false;
	JTextField jtf;
	mySlider slider;
	Object upperBound;
	Object lowerBound;
	String title;
	Object value;
	Number result = null;
	
	
	public BoundedHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		if(t.flag()==Param.DoubleSlider || t.flag()==Param.IntegerSlider) this.useslider=true;
		try{
			boundedObject = (Bounded) f.get(o);
			this.upperBound= boundedObject.getUpperBound();
			this.lowerBound= boundedObject.getLowerBound();
		}catch(Exception e){e.printStackTrace();}
		this.title=t.description() + " " + t.flag();
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
			jtf = new JTextField(11);
			jtf.setHorizontalAlignment(JTextField.RIGHT);
			inputPane.add(jtf,BorderLayout.EAST);
		}	
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		inputPane.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);
		return inputPane;
	}


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
			if(jtf.getText().equals("")){
				//boundedObject.setValue("0.0",Double.class);
			}
			else{
				value = jtf.getText();
				boundedObject.setValue(value.toString(),Double.class);
			}
		}
		if(t.flag() == Param.Integer){
			if(jtf.getText().equals("")){
				//boundedObject.setValue("0",Integer.class);
			}
			else{
				value = jtf.getText();
				boundedObject.setValue(value.toString(),Integer.class);
			}
		}
		try{
			f.set(o,boundedObject);
		}catch(Exception e){e.printStackTrace();}
	}
	

	public JPanel update() {
		//java.text.DecimalFormat df = new java.text.DecimalFormat("###.###");
		JPanel resultPane = new JPanel(new BorderLayout());
		if(t.flag() == Param.DoubleSlider){
			result=slider.getValue().doubleValue();
			boundedObject.setValue(result.toString(),Double.class);
		}
		if(t.flag() == Param.IntegerSlider){
			result=slider.getValue().intValue();
			boundedObject.setValue(result.toString(),Integer.class);
		}
		if(t.flag() == Param.Double){
			if(jtf.getText().equals("")){
				//boundedObject.setValue("0.0",Double.class);
			}
			else{
				value = jtf.getText();
				boundedObject.setValue(value.toString(),Double.class);
			}
		}
		if(t.flag() == Param.Integer){
			if(jtf.getText().equals("")){
				//boundedObject.setValue("0",Integer.class);
			}
			else{
				value = jtf.getText();
				boundedObject.setValue(value.toString(),Integer.class);
			}
		}
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		resultPane.add(jta,BorderLayout.WEST);
		
		try{
			f.set(o,boundedObject);
			resultPane.add(new JTextField(boundedObject.getValue()),BorderLayout.EAST);
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