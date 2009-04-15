package org.cytoscape.work.internal.gui;

import java.lang.reflect.Field;
import javax.swing.*;


import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.myBoundedSwing;
import org.cytoscape.work.util.mySlider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class BoundedDoubleHandler extends AbstractGuiHandler implements Guihandler {
	
	JTextField jtf;
	BoundedDouble myBounded;
	String title;
	Boolean useslider=false;
	mySlider slider;
	myBoundedSwing boundedField;
	Double value=null;
	String newline = System.getProperty("line.separator");
	
	public BoundedDoubleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			this.myBounded = (BoundedDouble)f.get(o);
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();}
		this.title = t.description();
		for ( Param s : t.flag())if(s.equals(Param.slider))useslider=true;
		
		panel = new JPanel(new BorderLayout());
		if(useslider){
			JLabel label = new JLabel(title);
			label.setFont(new Font(null, Font.PLAIN,12));
			panel.add(label,BorderLayout.WEST);
			slider = new mySlider(title,myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.getValue(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
			//slider.addChangeListener(this);
			panel.add(slider,BorderLayout.EAST);
		}
		else{
			try {
				JLabel label = new JLabel( title + " (max: " + myBounded.getLowerBound().toString() + "  min: " + myBounded.getUpperBound().toString() + ")" );
				label.setFont(new Font(null, Font.PLAIN,12));
				panel.add(label,BorderLayout.WEST);
				boundedField = new myBoundedSwing(myBounded.getValue(),myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
				panel.add(boundedField,BorderLayout.EAST);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	
	public void handle() {
    	if(useslider==true){
    		myBounded.setValue(slider.getValue().doubleValue());
    	}
    	else myBounded.setValue(boundedField.getFieldValue().doubleValue());
	}
	
	
	public String getState() {
		return myBounded.getValue().toString();
	}


	@Override
	public void resetValue() {
		// TODO Auto-generated method stub
		
	}

}