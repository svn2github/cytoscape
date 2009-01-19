package Factory;

import GuiInterception.*;
import Tunable.*;
import Utils.myButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class ButtonHandler implements Guihandler{
	
	Field f;
	Object o;
	Tunable t;
	String title;
	myButton button;

	
	@SuppressWarnings("deprecation")
	public ButtonHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.o=o;
		this.t=t;
		this.title=t.description();
		try{
			button = (myButton)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		button.setLabel(title);
	}


	public void handle(){
		try{
			button = (myButton)f.get(o);
			f.set(o,button);
		}catch(Exception e){e.printStackTrace();}			
	}


	public JPanel getInputPanel() {
		JPanel inpane = new JPanel();
		button.setselected(false);
		button.setActionCommand(title);
		button.addActionListener(new myActionListener());
		inpane.add(button);
		return inpane;
	}
		


	public JPanel getOutputPanel() {
		JPanel outpane = new JPanel();
		try{
			button = (myButton) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		try{
			outpane.add(new JTextField("statut = "+ button.getselected()));
		}catch(Exception e){e.printStackTrace();}		
		return outpane;
	}
	
	public class myActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae) {
			String command=ae.getActionCommand();
			if(command.equals(title)){
				button.setselected(true);
				try{
					f.set(o, button);
				}catch(Exception e){e.printStackTrace();}
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