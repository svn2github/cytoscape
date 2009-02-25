package org.cytoscape.work.internal.tunables;

import java.lang.reflect.Field;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.internal.tunables.utils.mySlider;
import org.cytoscape.work.*;
import org.cytoscape.work.Tunable.Param;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class BoundedDoubleHandler extends AbstractGuiHandler implements Guihandler {
	
	JTextField jtf;
	BoundedDouble myBounded;
	String title;
	boolean useslider=false;
	mySlider slider;
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
			slider.addChangeListener(this);
			panel.add(slider,BorderLayout.EAST);
		}
		else{
			try {
				JLabel label = new JLabel( title + " (max: " + myBounded.getLowerBound().toString() + "  min: " + myBounded.getUpperBound().toString() + ")" );
				label.setFont(new Font(null, Font.PLAIN,12));
				panel.add(label,BorderLayout.WEST);
				jtf = new JTextField( ((Double)myBounded.getValue()).toString(), 10);
				jtf.addActionListener( this );
				jtf.setHorizontalAlignment(JTextField.RIGHT);
				panel.add(jtf,BorderLayout.EAST);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	
	public void handle() {
    	if(useslider==true){
    		myBounded.setValue(slider.getValue().doubleValue());
    	}
    	else{
    		try{
    			jtf.setBackground(Color.white);
    			value = Double.parseDouble(jtf.getText());
    		}catch(NumberFormatException nfe){
    			try{
    				jtf.setBackground(Color.red);
    				value = Double.parseDouble(myBounded.getValue().toString());
    				JOptionPane.showMessageDialog(null,"An Integer was Expected"+newline+"Value will be set to default = "+value.doubleValue(), "Error",JOptionPane.ERROR_MESSAGE);
    			}catch(Exception e){e.printStackTrace();}
    		}
			try {
				myBounded.setValue(value.doubleValue());
			} catch (Exception e) { e.printStackTrace();}
    	}
	}
	
	public String getState() {
		return myBounded.getValue().toString();
	}

}
