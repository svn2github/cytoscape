package Factory;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import GuiInterception.Guihandler;
import Tunable.Tunable;


public class StringHandler implements Guihandler, CaretListener{	
	Field f;
	Tunable t;
	Object o;	
	JTextField jtf;
	String title;
	
	/*-------------------------------Constructor-----------------------------------*/	
	public StringHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		this.title=t.description();
		jtf=new JTextField(11);
	}

	/*-------------------------------Get the Panel with the INITIAL text-----------------------------------*/	
	public JPanel getPanel(){
		JPanel inpane = new JPanel(new GridLayout());
		JPanel test1 = new JPanel(new BorderLayout());
		JPanel test2 = new JPanel();
		inpane.add(test1);
		inpane.add(test2);
		JTextArea jta = new JTextArea(title);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		test1.add(jta,BorderLayout.CENTER);
		jta.setBackground(null);
		jta.setEditable(false);
		//Set the TextField with the Tunable's text
		try{
			jtf.setText(f.get(o).toString());
			jtf.addCaretListener(this);
		}catch (Exception e){e.printStackTrace();}		
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		test2.add(jtf,BorderLayout.EAST);
		return inpane;
	}

		
	/*-------------------------------Get the Panel with the MODIFIED value-----------------------------------*/
	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(title);
		jta.setBackground(null);
		outpane.add(jta,BorderLayout.WEST);
		try{
			JTextField jtf2 = new JTextField(f.get(o).toString());
			jtf2.setEditable(false);
			outpane.add(jtf2,BorderLayout.EAST);
		}catch(Exception e){e.printStackTrace();}
		return outpane;
	}	

	
	/*-------------------------------Set String Object with Tunable's new String text-----------------------------------*/
	public void handle(){
		if(jtf==null)
			return;
		try {
			f.set(o,jtf.getText());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/*-------------------------------Update the String Object with the new String text-----------------------------------*/	
	public void caretUpdate(CaretEvent e) {
		handle();
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