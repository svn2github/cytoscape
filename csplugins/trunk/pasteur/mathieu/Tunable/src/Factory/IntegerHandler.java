package Factory;

import java.lang.reflect.*;
import javax.swing.*;
import java.awt.Color;
import GuiInterception.Guihandler;
import TunableDefinition.Tunable;
import Slider.*;


public class IntegerHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;
	MySlider slider;
	
	String title;
	String value;
	Boolean available;
	Integer inte;

	
	
	public IntegerHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.available=t.available();
		try{
			this.value=f.get(o).toString();
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();
	}
	
	
	public void handle(){
		inte = Integer.parseInt(jtf.getText());

		if(available!=true) inte=Integer.parseInt(value);
		
		try {
			if (inte != null) f.set(o,inte);
		} catch (Exception e) { e.printStackTrace();}
	}

	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		jtf = new JTextField(value);
		if(available!=true){
			jtf.setEnabled(false);
			jtf.setBackground(Color.GRAY);
		}			
		pane.add(jtf);
		return pane;
	}
	

	
	public JPanel getresultpanel(){
		JPanel result = new JPanel();
		try{
			jtf = new JTextField((f.get(o)).toString());
			if(available==false){
				jtf.setEnabled(false);
				jtf.setBackground(Color.GRAY);
			}			
		}catch (Exception e){e.printStackTrace();}
		result.add(new JLabel(title));
		result.add(jtf);
		return result;
	}


	public void cancel(){
		inte = Integer.parseInt(value);
		try{
			f.set(o, inte);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	
	
	
	public JPanel update(){
		JPanel result = new JPanel();
		if(available==true){
			inte = Integer.parseInt(jtf.getText());
		}
		result.add(new JTextField(inte.toString()));
		return result;
	}
	
	

	
	public void	setValue(Object object){
		try{
			f.set(o, object);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	@Override
	public Tunable getTunable() {
		return t;
	}


	@Override
	public Field getField() {
		return f;
	}
	

	public Object getObject() {
		return o;
	}	
	
//	@Override
//	public void stateChanged(ChangeEvent CE) {
//		JSlider source = (JSlider) CE.getSource();
//			//if(!source.getValueIsAdjusting())
//		jtf.setText(String.valueOf(source.getValue()));
//			
//		
//	}
//
//	@Override
//	public void actionPerformed(ActionEvent ae) {
//		if(ae.getSource()!=jtf.getText())
//		slider.setValue(Integer.parseInt(jtf.getText()));
//	}
}