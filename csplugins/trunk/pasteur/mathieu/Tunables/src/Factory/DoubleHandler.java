
package Factory;


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
	String title;
	Double doub;

	
	
	public DoubleHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=f.getName();					
		try{
			this.doub=(Double)f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public void handle(){
		try{
			f.set(o, Double.parseDouble(jtf.getText()));
		}catch(Exception e){e.printStackTrace();}
	}
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();		
		jtf = new JTextField(doub.toString());
		pane.add(jtf);
		return pane;
	}



	public JPanel update(){
		JPanel result = new JPanel();
		doub = Double.parseDouble(jtf.getText());
		try{
			if(doub!=null)f.set(o, doub);
			result.add(new JTextField(f.get(o).toString()));
		}catch(Exception e){e.printStackTrace();}
		return result;
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
