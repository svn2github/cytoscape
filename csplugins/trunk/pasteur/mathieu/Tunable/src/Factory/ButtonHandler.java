
package Factory;

import GuiInterception.*;
import TunableDefinition.Tunable;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import javax.swing.*;


public class ButtonHandler implements Guihandler,ActionListener{
	
	Field f;
	Object o;
	Tunable t;

	JButton buttonIn;
	JButton buttonOut;
	Object lowerbound;
	String title;
	String value;
	JTextField jtf;
	
	public ButtonHandler(Field f, Object o, Tunable t){
		this.f = f;
		this.o = o;
		this.t = t;
		this.title=f.getName();
	}

	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		try{
			buttonIn = (JButton) f.get(o);
		}catch (Exception e){e.printStackTrace();}
		buttonIn.setText(title);
		buttonIn.setBackground(Color.GRAY);
		buttonIn.addActionListener(this);
		pane.add(buttonIn);
		return pane;
	}
	
	public void handle(){
		buttonOut = new JButton();
		buttonOut.setBackground(Color.RED);
		if(buttonIn.isSelected()) buttonOut.setBackground(Color.GREEN);
		try{
			f.set(o, buttonOut);
		}catch(Exception e){e.printStackTrace();}
	}



	public JPanel getresultpanel(){
	/*	JPanel resultpane = new JPanel();
		jtf = new JTextField("Clicked off");
		jtf.setBackground(Color.RED);
		try{
		//	button=(AbstractButton) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		if(button.isSelected()){
			jtf.setText("Clicked on");
			jtf.setBackground(Color.GREEN);
		}
		resultpane.add(jtf);
		return resultpane;
	*/	return null;
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		//if(e.getActionCommand().equals(t.description())){
			buttonIn.setSelected(true);
		//}			
	}




	@Override
	public JPanel update() {
		JPanel result = new JPanel();
		JButton handlebutton=null;
		handlebutton = new JButton();
		handlebutton.setBackground(Color.RED);
		if(buttonIn.isSelected()){
			handlebutton.setBackground(Color.GREEN);
		}
		result.add(handlebutton);
		return result;
	}
	
	

	public void cancel() {
		try{
			f.set(o,buttonIn);
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