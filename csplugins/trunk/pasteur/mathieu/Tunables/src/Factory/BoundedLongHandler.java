package Factory;

import java.lang.reflect.*;
import javax.swing.*;

import java.awt.*;
import GuiInterception.*;
import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.BoundedLong;
import Utils.mySlider;


public class BoundedLongHandler implements Guihandler {

	Field f;
	Object o;
	Tunable t;
	JTextField jtf;
	BoundedLong myBounded;
	String title;
	Boolean useslider=false;
	mySlider slider;

	
	public BoundedLongHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		try {
			this.myBounded = (BoundedLong)f.get(o);
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();}
		this.title = t.description();
		if(t.flag()==Param.Slider) this.useslider=true;
	}

	
	public JPanel getInputPanel() {
		JPanel inpane = new JPanel(new BorderLayout());
		if(useslider==true){
			slider = new mySlider(title,myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.getValue(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
			inpane.add(slider,BorderLayout.EAST);
		}
		else{
			inpane.add(myBounded,BorderLayout.EAST);
		}
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		inpane.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);
		return inpane;
	}
	
	
	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel(new BorderLayout());
		if(useslider==true){
			myBounded.setValue(slider.getValue().longValue());
		}
		else	myBounded.updateValue();
		
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		
		try{
			f.set(o,myBounded);
			outpane.add(new JTextField(myBounded.getValue().toString()),BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return outpane;
	}
	
	
	public void handle() {
		if(useslider==true){
			myBounded.setValue(slider.getValue().longValue());
		}
		else myBounded.updateValue();
		
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