package Factory;

import GuiInterception.*;
import Tunable.*;
import Utils.myButton;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class ButtonHandler implements Guihandler{
	
	Field f;
	Object o;
	Tunable t;

	String title;
	myButton button;
	Boolean selected=null;

	
	@SuppressWarnings("deprecation")
	public ButtonHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		this.title=f.getName();
		try{
			button = (myButton)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		button.setLabel(title);
	}


	public void handle(){ 
		if(selected == true) button.setSelected(true);
		else button.setSelected(false);
		try{
			f.set(o,button);
		}catch(Exception e){e.printStackTrace();}			
	}


	public JPanel getInputPanel() {
		JPanel inputpane = new JPanel();
		button.setselected(false);
		selected=button.getselected();
		button.setActionCommand(title);
		button.addActionListener(new myActionListener());
		inputpane.add(button);
		return inputpane;
	}
		


	public JPanel update() {
		JPanel resultpane = new JPanel();
		System.out.println("button selected = " + selected);
		if(selected == true) button.setselected(true);
		else button.setselected(false);
		try{
			f.set(o,button);
			resultpane.add(new JTextField("statut = "+ button.getselected()));
		}catch(Exception e){e.printStackTrace();}		
		return resultpane;
	}
	
	public class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae) {
			if(ae.getActionCommand().equals(title)){
				button.setselected(true);
				selected=button.getselected();
			}
		}
	}

	
	public Field getField() {
		return f;
	}
	public Tunable getTunable() {
		return t;
	}
	public Object getObject() {
		return o;
	}
}