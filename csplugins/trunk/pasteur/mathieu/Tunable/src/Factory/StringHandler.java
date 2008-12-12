/*
package Factory;


import java.lang.reflect.*;

import javax.swing.*;

import GuiInterception.Guihandler;
import TunableDefinition.Tunable;

public class StringHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf;
	String title;
	
	public StringHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();
		try{
			f.set(o,t.value());
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void handle(){
		String value = jtf.getText();
		try {
			if (value!=null)
				f.set(o,value);
			} catch (Exception e) {e.printStackTrace();}
	}
	
	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		try{
			jtf = new JTextField(f.get(o).toString(),10);
		}catch (Exception e){e.printStackTrace();}
		//pane.add(new JLabel(title));
		pane.add(jtf);
		return pane;
	}
	
	public JPanel getresultpanel(){
		JPanel result = new JPanel();
		try{
			jtf = new JTextField((String)f.get(o));
		}catch (Exception e){e.printStackTrace();}
		result.add(new JLabel(title));
		result.add(jtf);
		return result;
	}



	public JPanel update() {
		JPanel result = new JPanel();
		String text = jtf.getText();
		jtf = new JTextField(text);
		result.add(jtf);
		return result;
	}


	public void cancel() {
	
		try{
			f.set(o,t.value());
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

	
	

}
*/