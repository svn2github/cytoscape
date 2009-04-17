package org.cytoscape.work.internal.tunables;

import javax.swing.*;

import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.internal.tunables.utils.myBoundedSwing;
import org.cytoscape.work.internal.tunables.utils.mySlider;
import org.cytoscape.work.*;
import org.cytoscape.work.Tunable.Param;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.Field;


public class BoundedDoubleHandler extends AbstractGuiHandler implements Guihandler {
	
	private BoundedDouble myBounded;
	private Double initValue;
	private String title;
	private	boolean useslider=false;
	private mySlider slider;
	private myBoundedSwing boundedField;
	
	protected BoundedDoubleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			this.myBounded = (BoundedDouble)f.get(o);
		} catch (IllegalAccessException iae){iae.printStackTrace();}

		this.initValue = myBounded.getValue();
		this.title = t.description();
		for ( Param s : t.flag())if(s.equals(Param.slider))useslider=true;		
		panel = new JPanel(new BorderLayout());
		if(useslider){
			JLabel label = new JLabel(title);
			label.setFont(new Font(null, Font.PLAIN,12));
			panel.add(label,BorderLayout.WEST);
			slider = new mySlider(title,myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.getValue(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
			// Add ChangeListener????????
			slider.addChangeListener(this);
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
			myBounded.setValue(slider.getValue().doubleValue());
		}
		else{
			myBounded.setValue(boundedField.getFieldValue().doubleValue());
		}
	}
	
	
	public void resetValue(){
//		System.out.println("#########Value will be reset to initial value = " + initValue + "#########");
		myBounded.setValue(initValue);
	}

	
	public String getState() {
		return myBounded.getValue().toString();
	}

}
