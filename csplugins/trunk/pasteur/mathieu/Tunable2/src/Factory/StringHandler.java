package Factory;


import java.lang.reflect.*;
import javax.swing.*;
import GuiInterception.Guihandler;
import Tunable.Tunable;



public class StringHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	
	JTextField jtf;
	String title;
	String value;
	Boolean available;
	
	
	public StringHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.available=t.available();
		this.title=f.getName();
		try{
			this.value = f.get(o).toString();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void handle(){
		try {
			f.set(o,jtf.getText());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		try{
			jtf = new JTextField(value,10);
		}catch (Exception e){e.printStackTrace();}
		pane.add(jtf);
		return pane;
	}	


	public JPanel update() {
		JPanel result = new JPanel();
		String text=null;
		if(available==true){
			text = jtf.getText();
		}
		try{
			f.set(o, text);
			result.add(new JTextField(f.get(o).toString()));
		}catch(Exception e){e.printStackTrace();}
		return result;
	}	
	
	
	

	public void cancel() {
		try{
			f.set(o,value);
		}catch(Exception e){e.printStackTrace();}
		
	}
	

	public Tunable getTunable(){
		return t;
	}

	public Field getField(){
		return f;
	}

	public Object getObject(){
		return o;
	}

	@Override
	public Class<?> getclass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
