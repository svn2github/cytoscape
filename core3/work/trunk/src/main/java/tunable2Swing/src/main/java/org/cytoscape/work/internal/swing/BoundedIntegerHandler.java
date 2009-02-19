package org.cytoscape.work.internal.swing;

import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Guihandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.utils.BoundedInteger;
import org.cytoscape.work.utils.mySlider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;


public class BoundedIntegerHandler extends AbstractGuiHandler implements Guihandler ,ActionListener{

	JTextField jtf;
	BoundedInteger myBounded;
	String title;
	Boolean useslider=false;
	mySlider slider;
	Double value=null;
	String newline = System.getProperty("line.separator");
	
	public BoundedIntegerHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			this.myBounded = (BoundedInteger)f.get(o);
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
				JLabel label = new JLabel( title + newline+" (max: " + myBounded.getLowerBound().toString() + "  min: " + myBounded.getUpperBound().toString() + ")" );
				label.setFont(new Font(null, Font.PLAIN,12));
				panel.add(label,BorderLayout.WEST);
				jtf = new JTextField( ((Integer)myBounded.getValue()).toString(), 10);
				jtf.addActionListener( this );
				jtf.setHorizontalAlignment(JTextField.RIGHT);
				panel.add( jtf,BorderLayout.EAST );
			} catch (Exception e) { e.printStackTrace(); }			
		}
	}
	
    public void handle() {
    	if(useslider==true){
    		myBounded.setValue(slider.getValue().intValue());
    	}
    	else{
    		try{
    			jtf.setBackground(Color.white);
    			value = Double.parseDouble(jtf.getText());
    		}catch(NumberFormatException nfe){
    			try{
    				jtf.setBackground(Color.red);
    				value = Double.parseDouble(myBounded.getValue().toString());
    				JOptionPane.showMessageDialog(null,"An Integer was Expected"+newline+"Value will be set to default = "+value.intValue(), "Error",JOptionPane.ERROR_MESSAGE);
    			}catch(Exception e){e.printStackTrace();}
    		}
			try {
				myBounded.setValue(value.intValue());
			} catch (Exception e) { e.printStackTrace();}
    	}
	}
    
    
	public void returnPanel(){
		panel.removeAll();
		panel.add(new JLabel(t.description()));
		panel.add(new JTextField(Integer.toString(myBounded.getValue())));
	}
    

    public String getState() {
        return myBounded.getValue().toString();
    }
}