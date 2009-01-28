package Factory;

import java.lang.reflect.Field;
import javax.swing.*;

import java.awt.Color;
import java.awt.event.ActionListener;
import GuiInterception.*;
import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.BoundedDouble;
import Utils.mySlider;


public class BoundedDoubleHandler extends AbstractGuiHandler implements Guihandler ,ActionListener{
	
	JTextField jtf;
	BoundedDouble myBounded;
	String title;
	Boolean useslider=false;
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
		for ( Param s : t.flag())if(s.equals(Param.Slider))useslider=true;
		
		panel = new JPanel();
		if(useslider){
			panel.add(new JLabel(title));
			slider = new mySlider(title,myBounded.getLowerBound(),myBounded.getUpperBound(),myBounded.getValue(),myBounded.isLowerBoundStrict(),myBounded.isUpperBoundStrict());
			panel.add(slider);
		}
		else{
			try {
				panel.add( new JLabel( title + " (max: " + myBounded.getLowerBound().toString() + "  min: " + myBounded.getUpperBound().toString() + ")" ) );
				jtf = new JTextField( ((Double)myBounded.getValue()).toString(), 10);
				jtf.addActionListener( this );
				jtf.setHorizontalAlignment(JTextField.RIGHT);
				panel.add( jtf );
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
    				value = Double.parseDouble(f.get(o).toString());
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