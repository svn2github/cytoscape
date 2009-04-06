package org.cytoscape.work.internal.gui;


import javax.swing.*;

import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.util.BoundedFloat;
import org.cytoscape.work.util.myBoundedSwing;
import org.cytoscape.work.util.mySlider;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.lang.reflect.*;


public class BoundedFloatHandler extends AbstractGuiHandler implements Guihandler ,ActionListener{

	JTextField jtf;
	BoundedFloat myBounded;
	String title;
	Boolean useslider=false;
	mySlider slider;
	myBoundedSwing boundedField;
	String newline = System.getProperty("line.separator");
	Double value=null;
	
	protected BoundedFloatHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			this.myBounded = (BoundedFloat)f.get(o);
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();}
		this.title = t.description();
		for ( Param s : t.flag())if(s.equals(Param.slider))useslider=true;
		
		panel = new JPanel(new BorderLayout());
		if(useslider){
			JLabel label = new JLabel(title);
			label.setFont(new Font(null, Font.PLAIN,12));
			slider = new mySlider(title,myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.getValue(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
			//slider.addChangeListener(this);
			panel.add(label,BorderLayout.WEST);
			panel.add(slider,BorderLayout.EAST);
		}
		else{
			JLabel label = new JLabel( title + " (max: " + myBounded.getLowerBound().toString() + "  min: " + myBounded.getUpperBound().toString() + ")" );
			label.setFont(new Font(null, Font.PLAIN,12));
			boundedField = new myBoundedSwing(myBounded.getValue(),myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
			panel.add(label,BorderLayout.WEST);
			panel.add(boundedField,BorderLayout.EAST);
		}
	}

    public void handle() {
       	if(useslider==true){
    		myBounded.setValue(slider.getValue().floatValue());
    	}
    	else{
    		myBounded.setValue(boundedField.getFieldValue().floatValue());
    	}
	}


    public String getState() {
        return myBounded.getValue().toString();
    }
}
