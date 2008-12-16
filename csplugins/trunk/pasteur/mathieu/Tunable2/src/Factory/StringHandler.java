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
		String valuehandle = jtf.getText();
		if(available!=true) valuehandle= value;
		try {
			if (valuehandle!=null)f.set(o,valuehandle);
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

	
	
	public JPanel getresultpanel(){
		JPanel result = new JPanel();
		try{
			jtf = new JTextField(f.get(o).toString());
		}catch (Exception e){e.printStackTrace();}
		result.add(new JLabel(title));
		result.add(jtf);
		return result;
	}
	
	
	


	public JPanel update() {
		JPanel result = new JPanel();
		String text=null;
		if(available==true){
			text = jtf.getText();
		}
		result.add(new JTextField(text));
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

}
