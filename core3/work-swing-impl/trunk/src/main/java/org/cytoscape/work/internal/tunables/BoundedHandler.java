package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.internal.tunables.utils.myBoundedSwing;
import org.cytoscape.work.internal.tunables.utils.mySlider;
import org.cytoscape.work.util.AbstractBounded;

public class BoundedHandler<T extends AbstractBounded> extends AbstractGuiHandler {


	T bounded;
	private String title;
	private boolean useslider = false;
	private mySlider slider;
	private myBoundedSwing boundedField;
	
	public BoundedHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			this.bounded = (T)f.get(o);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();	
		}

		this.title = t.description();
		for ( Param s : t.flag())if(s.equals(Param.slider))useslider=true;		
		panel = new JPanel(new BorderLayout());
		
		try{
			if(useslider){
				JLabel label = new JLabel(title);
				label.setFont(new Font(null, Font.PLAIN,12));
				panel.add(label,BorderLayout.WEST);
				slider = new mySlider(title,(Number) bounded.getLowerBound(),(Number)bounded.getUpperBound(),(Number)bounded.getValue(),bounded.isLowerBoundStrict(),bounded.isUpperBoundStrict());
				// Add ChangeListener????????
				slider.addChangeListener(this);
				panel.add(slider,BorderLayout.EAST);
			}
	
			else{
				JLabel label = new JLabel( title + " (max: " + bounded.getLowerBound().toString() + "  min: " + bounded.getUpperBound().toString() + ")" );
				label.setFont(new Font(null, Font.PLAIN,12));
				boundedField = new myBoundedSwing((Number)bounded.getValue(),(Number)bounded.getLowerBound(),(Number)bounded.getUpperBound(),bounded.isLowerBoundStrict(),bounded.isUpperBoundStrict());
				panel.add(label,BorderLayout.WEST);
				panel.add(boundedField,BorderLayout.EAST);
			}
		}catch (Exception e) { e.printStackTrace(); }
	}

	public void handle() {
		try{
			if(useslider==true){
				bounded.setValue(slider.getValue().doubleValue());
			}
			else{
				bounded.setValue(boundedField.getFieldValue().doubleValue());
			}
		}catch (Exception e){e.printStackTrace();}
	}

    public String getState() {
        return bounded.getValue().toString();
    }

	@Override
	public void resetValue() {
		// TODO Auto-generated method stub
		
	}
}
