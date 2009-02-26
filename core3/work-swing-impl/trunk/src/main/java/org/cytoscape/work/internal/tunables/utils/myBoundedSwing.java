package org.cytoscape.work.internal.tunables.utils;


import javax.swing.*;

import java.awt.Color;
import java.awt.event.*;

public class myBoundedSwing extends JTextField{	
	
	/**
	 * 
	 */
	JTextField jtf;
	Number init;
	Number b_value;
	Number b_min,b_max;
	Boolean lower,upper;
	java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
	String newline = System.getProperty("line.separator");
	
	public myBoundedSwing(Number initVal,Number b_min, Number b_max,Boolean lower,Boolean upper){
		this.init = initVal;
		this.b_min = b_min;
		this.b_max = b_max;
		this.lower = lower;
		this.upper = upper;
		b_value = init;
		setInitValue();
		initUI();
	}
	
	@SuppressWarnings("serial")
	protected void initUI(){
		this.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent ae){
				Number val = getFieldValue();
				if(val!= b_value) b_value = val;
				setFieldValue();
			}
		});
		setHorizontalAlignment(JTextField.RIGHT);
	}
	
	
	private void setFieldValue(){
		setText(b_value.toString());
	}
	
	private void setInitValue(){
		setText(init.toString());
	}	

	
	public Number getFieldValue(){
    	Double val = null;
    	try{
    		val = Double.parseDouble(getText());
    	}catch(NumberFormatException nfe){
    		setBackground(Color.red);
    		JOptionPane.showMessageDialog(null, "Please enter a Value","Alert", JOptionPane.ERROR_MESSAGE);
    		setFieldValue();
    		setBackground(Color.white);
    		try{
    			val = b_value.doubleValue();
    		}catch(Exception e){e.printStackTrace();}
    	}
    	
        if ( init instanceof Double || init instanceof Float ){
        	if ( val < b_min.doubleValue()){
        		setBackground(Color.red);
            	JOptionPane.showMessageDialog(null, "Value ("+val.doubleValue()+") is less than lower limit ("+df.format(b_min.doubleValue())+")"+newline+"Value will be set to default : "+b_value,"Alert",JOptionPane.ERROR_MESSAGE);
            	setInitValue();
            	setFieldValue();
            	setBackground(Color.white);
            	return b_value;
            }
            if ( val > b_max.doubleValue()){
            	setBackground(Color.red);
            	JOptionPane.showMessageDialog(null, "Value ("+val.doubleValue()+") is much than upper limit ("+df.format(b_max.doubleValue())+")"+newline+"Value will be set to default : "+b_value,"Alert",JOptionPane.ERROR_MESSAGE);
            	setInitValue();
            	setFieldValue();
            	setBackground(Color.white);
            	return b_value;
            }
            return b_value instanceof Double ? (Number)val.doubleValue() : val.floatValue();
        }
        else {
            if ( val < b_min.longValue()){
            	setBackground(Color.red);
            	JOptionPane.showMessageDialog(null, "Value ("+val.longValue()+") is less than lower limit ("+b_min.longValue()+")"+newline+"Value will be set to default : "+b_value,"Alert",JOptionPane.ERROR_MESSAGE);
            	setInitValue();
            	setFieldValue();
            	setBackground(Color.white);
            	return b_value;
            }
            if ( val > b_max.longValue()){
            	setBackground(Color.red);
            	JOptionPane.showMessageDialog(null, "Value ("+val.longValue()+") is much than upper limit ("+b_max.longValue()+")"+newline+"Value will be set to default : "+b_value,"Alert",JOptionPane.ERROR_MESSAGE);
            	setInitValue();
            	setFieldValue();
            	setBackground(Color.white);
            	return b_value;
            }
            return b_value instanceof Long ? (Number)val.longValue() : val.intValue();
        }
    }
}