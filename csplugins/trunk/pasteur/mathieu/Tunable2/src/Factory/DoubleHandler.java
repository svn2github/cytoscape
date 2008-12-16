
package Factory;


import java.awt.Color;
import java.lang.reflect.*;
import javax.swing.*;
import GuiInterception.Guihandler;
import Tunable.Tunable;
import java.lang.Object;



public class DoubleHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;	
	
	String value;
	String title;
	Boolean available;
	Double doub;

	
	
	public DoubleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=f.getName();					
		this.available=t.available();
		try{
			this.value=f.get(o).toString();
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void handle(){
		doub= Double.parseDouble(jtf.getText());
		
		if(available==true) doub=Double.parseDouble(value);
		try {
			if (doub != null ) f.set(o,doub);
		} catch (Exception e) { e.printStackTrace(); }
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
	
	



	public JPanel update(){
		JPanel result = new JPanel();
		if(available==true)	doub = Double.parseDouble(jtf.getText());
		result.add(new JTextField(doub.toString()));
		return result;
	}
	
	
	
	
	
	
	public void cancel(){
		try{
			f.set(o, Double.parseDouble(value));
		}catch(Exception e){e.printStackTrace();}
	}
		
	
	public void	setValue(Object object){
		try{
			f.set(o, object);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	

	public Tunable getTunable() {
		return t;
	}


	public Field getField() {
		return f;
	}


	public Object getObject() {
		return o;
	}
}
