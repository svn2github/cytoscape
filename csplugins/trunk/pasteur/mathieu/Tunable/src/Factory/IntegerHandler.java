package Factory;


import java.lang.reflect.*;
import javax.swing.*;
import java.awt.Color;
import GuiInterception.Guihandler;
import TunableDefinition.Tunable;
import Properties.PropertiesImpl;
import Slider.*;


public class IntegerHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;
	MySlider slider;
	
	Boolean useslider=false;
	String title;
	Double upperbound;
	Double lowerbound;
	String value;
	Boolean available;
	Integer i;

	
	
	public IntegerHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.available=t.available();
		this.value=t.value();
		try{
			f.set(o,Integer.parseInt(value));
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();	
		this.lowerbound = t.lowerbound();
		this.upperbound = t.upperbound();
		if(t.flag()==8)this.useslider=true;
	}
	
	
	public void handle(){
		if(available==true){
			Number s = slider.getValue();
			i = s.intValue();
		}
		else i = Integer.parseInt(value);
		
		try {
			if (i != null) f.set(o,i);
		} catch (Exception e) { e.printStackTrace();}
	}

	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		try{
			if(available==true){
				if(useslider==true  && lowerbound!=null && upperbound!=null){
						slider = new MySlider(title,lowerbound.intValue(),upperbound.intValue(),Double.parseDouble(f.get(o).toString()));
						pane.add(slider);			
				}
			}
			else{
				jtf = new JTextField(value);
				jtf.setEnabled(false);
				jtf.setBackground(Color.GRAY);
				//pane.add(new JLabel(title));
				pane.add(jtf);
			}
		}catch (Exception e){e.printStackTrace();}
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
		try{
			f.set(o, Integer.parseInt(value));
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	
	public JPanel update(){
		if(available==true){
			Number s = slider.getValue();
			i = s.intValue();
		}
		else i = Integer.parseInt(value);
		JPanel result = new JPanel();
		jtf= new JTextField(i.toString());
		result.add(jtf);
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