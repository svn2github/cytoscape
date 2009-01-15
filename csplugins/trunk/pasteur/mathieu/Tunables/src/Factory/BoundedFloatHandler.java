package Factory;

import java.lang.reflect.*;
import javax.swing.*;

import java.awt.*;
import GuiInterception.*;
import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.BoundedFloat;
import Utils.mySlider;


public class BoundedFloatHandler implements Guihandler {

	Field f;
	Object o;
	Tunable t;
	JTextField jtf;
	BoundedFloat bounded;
	String title;
	Boolean useslider=false;
	mySlider slider;
	Double val;

	
	public BoundedFloatHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		try {
			this.bounded = (BoundedFloat)f.get(o);
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();}
		this.title = t.description();
		if(t.flag()==Param.Slider) this.useslider=true;
	}

	
	public JPanel getInputPanel() {
		JPanel inputPane = new JPanel(new BorderLayout());
		if(useslider==true){
			slider = new mySlider(title,bounded.getLowerBound(),bounded.getUpperBound(),bounded.getValue(),bounded.isLowerBoundStrict(),bounded.isUpperBoundStrict());
			inputPane.add(slider,BorderLayout.EAST);
		}
		else{
			//jtf = new JTextField(bounded.getValue().toString(),11);
			//jtf.setHorizontalAlignment(JTextField.RIGHT);
			inputPane.add(bounded,BorderLayout.EAST);
		}
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		inputPane.add(jta);
		jta.setBackground(null);
		jta.setEditable(false);
		return inputPane;
	}
	
	
	public JPanel update() {
		JPanel resultPane = new JPanel(new BorderLayout());
		if(useslider==true){
			bounded.setValue(slider.getValue().floatValue());
		}
		else{
			bounded.updateValue();
		}
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		resultPane.add(jta,BorderLayout.WEST);
		try{
			f.set(o,bounded);
			resultPane.add(new JTextField(bounded.getValue().toString()),BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return resultPane;
	}
	
	
	public void handle() {
		if(useslider==true){
			bounded.setValue(slider.getValue().floatValue());
		}
		else{
			bounded.updateValue();
//			try{
//				val = Double.parseDouble(jtf.getText());
//			}catch(NumberFormatException nfe){
//				JOptionPane.showMessageDialog(null, "Float Expected", "Error", JOptionPane.ERROR_MESSAGE);
//				try{
//					val = Double.parseDouble(bounded.getValue().toString());
//				}catch(Exception e){e.printStackTrace();}
//			}
//			bounded.setValue(val.floatValue());
		}
		try{
			f.set(o,bounded);
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