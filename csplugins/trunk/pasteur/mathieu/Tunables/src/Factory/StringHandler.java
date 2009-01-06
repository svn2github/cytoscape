package Factory;


import java.awt.BorderLayout;
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
	
	
	public StringHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
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
		JPanel pane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(f.getName());
		jta.setBackground(null);
		pane.add(jta,BorderLayout.WEST);
		try{
			jtf = new JTextField(value,10);
		}catch (Exception e){e.printStackTrace();}
		pane.add(jtf,BorderLayout.EAST);
		return pane;
	}	


	public JPanel update() {
		JPanel result = new JPanel();
		String text=null;
		text = jtf.getText();
		try{
			f.set(o, text);
			result.add(new JTextField(f.get(o).toString()));
		}catch(Exception e){e.printStackTrace();}
		return result;
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
