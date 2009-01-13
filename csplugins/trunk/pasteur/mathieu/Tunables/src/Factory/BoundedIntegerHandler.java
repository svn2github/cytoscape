
package Factory;

import java.lang.reflect.*;
import javax.swing.*;
import java.awt.*;
import GuiInterception.*;
import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.BoundedInteger;
import Utils.mySlider;


public class BoundedIntegerHandler implements Guihandler {

	Field f;
	Object o;
	Tunable t;
	JTextField jtf;
	BoundedInteger bounded;
	String title;
	Boolean useslider=false;
	mySlider slider;

	
	public BoundedIntegerHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		try {
			this.bounded = (BoundedInteger)f.get(o);
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();}
		this.title = t.description();
		if(t.flag()==Param.Slider) this.useslider=true;
	}

	
	public JPanel getInputPanel() {
		JPanel inputPane = new JPanel(new BorderLayout());
		if(useslider==true){
			slider = new mySlider(title,bounded.getLowerBound(),bounded.getUpperBound(),bounded.getValue());
			inputPane.add(slider,BorderLayout.EAST);
		}
		else{
			jtf = new JTextField(bounded.getValue().toString(),11);
			jtf.setHorizontalAlignment(JTextField.RIGHT);
			inputPane.add(jtf,BorderLayout.EAST);
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
			bounded.setValue(slider.getValue().intValue());
		}
		else{
			bounded.setValue(Integer.parseInt(jtf.getText()));
		}
		
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		resultPane.add(jta,BorderLayout.WEST);
		
		try{
			f.set(o,bounded);
			resultPane.add(new JTextField(bounded.getValue()),BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return resultPane;
	}
	
	
	public void handle() {
		if(useslider==true){
			bounded.setValue(slider.getValue().intValue());
		}
		else{
			bounded.setValue(Integer.parseInt(jtf.getText()));
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