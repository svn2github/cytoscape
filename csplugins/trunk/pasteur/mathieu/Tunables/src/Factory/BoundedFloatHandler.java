package Factory;

import java.lang.reflect.*;
import javax.swing.*;

import java.awt.*;

import GuiInterception.*;
import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.BoundedFloat;
import Utils.mySlider;


public class BoundedFloatHandler implements Guihandler {

	Field f;
	Object o;
	Tunable t;
	JTextField jtf;
	BoundedFloat myBounded;
	String title;
	Boolean useslider=false;
	mySlider slider;
	Param[] parameters;
	
	public BoundedFloatHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		try {
			this.myBounded = (BoundedFloat)f.get(o);
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();}
		this.title = t.description();
		this.parameters = t.flag();
		for(int i=0;i<parameters.length;i++)
			if(parameters[i]==Param.Slider) this.useslider = true;
//		if(t.flag()==Param.Slider) this.useslider=true;
	}

	
	public JPanel getInputPanel() {
//		JPanel inpane = new JPanel(new BorderLayout());
		
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		jta.setBackground(null);
		jta.setEditable(false);
		
		JPanel inpane = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		inpane.add(test1);
		inpane.add(test2);
		test1.add(jta,BorderLayout.CENTER);
		
		
		if(useslider==true){
			slider = new mySlider(title,myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.getValue(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
//			inpane.add(slider,BorderLayout.EAST);
			test2.add(slider,BorderLayout.EAST);
		}
		else{
//			inpane.add(myBounded,BorderLayout.EAST);
			test2.add(myBounded,BorderLayout.EAST);
		}
		return inpane;
	}
	
	
	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel(new BorderLayout());
		if(useslider==true){
			myBounded.setValue(slider.getValue().floatValue());
		}
		else	myBounded.updateValue();
		
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		try{
			f.set(o,myBounded);
			JTextField jtf2 = new JTextField(myBounded.getValue().toString());
			jtf2.setEditable(false);
			outpane.add(jtf2,BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return outpane;
	}
	
	
	public void handle() {
		if(useslider==true){
			myBounded.setValue(slider.getValue().floatValue());
		}
		else	myBounded.updateValue();

		try{
			f.set(o,myBounded);
		}catch(Exception e){e.printStackTrace();}
	}	
	

	public Field getField() {
		return f;
	}
	public Object getObject() {
		return o;
	}
	public Tunable getTunable() {
		return t;
	}
}