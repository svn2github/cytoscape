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
	Param[] parameters;
	boolean valueChanged=true;
	Long Init;
	
	/*-------------------------------Declaration of the BoundedObject with his parameters(description, useslider)-----------------------------------*/
	public BoundedLongHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		//Set the BoundedObject with the Tunable
		try{
			this.myBounded = (BoundedLong)f.get(o);
			this.Init = myBounded.getValue();
		}catch(IllegalAccessException iae){iae.printStackTrace();}
		this.title = t.description();
		this.parameters = t.flag();
		for(int i=0;i<parameters.length;i++) if(parameters[i]==Param.Slider) this.useslider = true;
	}

	
	/*-------------------------------Get the Panel with the INITIAL value-----------------------------------*/
	public JPanel getPanel() {
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
		//initialisation of the Slider or the bounded
		if(useslider==true){
			slider = new mySlider(title,myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.getValue(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
			test2.add(slider,BorderLayout.EAST);
		}
		else{
			test2.add(myBounded,BorderLayout.EAST);
		}
		return inpane;
	}
	
	
	/*-------------------------------Get the Panel with the MODIFIED value-----------------------------------*/	
	public JPanel getOutputPanel(boolean changed) {
		JPanel outpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		//Handle the value that has been modified
		//handle();
		JTextField jtf2 = new JTextField();
		//Set the Tunable's new value
		if(changed==true)jtf2.setText(myBounded.getValue().toString());
		else jtf2.setText(Init.toString());
		jtf2.setEditable(false);
		outpane.add(jtf2,BorderLayout.EAST);
		valueChanged=true;
		return outpane;
	}
	
	
	/*-------------------------------Handle the value of the BoundedObject-----------------------------------*/
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
	
	public boolean valueChanged(){
		handle();
		if(myBounded.equals(Init))valueChanged=false;
		return valueChanged;
	}

}