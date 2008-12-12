/*
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

	AbstractButton button;
	Object lowerbound;
	String title;
	String value;
	JTextField jtf;
	
	public ButtonHandler(Field f, Object o, Tunable t){
		this.f = f;
		this.o = o;
		this.t = t;
		this.title=t.description();
		this.value=t.value();
		try{
			button = (AbstractButton) f.get(o);
			button.setSelected(Boolean.parseBoolean(value));
			f.set(o,button);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel();
		try{
			button.setSelected(Boolean.parseBoolean(value));
			button = (AbstractButton) f.get(o);
		}catch (Exception e){e.printStackTrace();}
		//button.setSelected(false);
		button.setText(title);
		button.addActionListener(this);
		//pane.add(new JLabel(title));
		pane.add(button);
		return pane;
	}
	
	public void handle(){
			try{
				f.set(o, button);
			}catch(Exception e){e.printStackTrace();}
	}

	
	public JPanel getresultpanel(){
		JPanel resultpane = new JPanel();
		jtf = new JTextField("Clicked off");
		jtf.setBackground(Color.RED);
		try{
			button=(AbstractButton) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		if(button.isSelected()){
			jtf.setText("Clicked on");
			jtf.setBackground(Color.GREEN);
		}
		resultpane.add(jtf);
		return resultpane;
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		//if(e.getActionCommand().equals(t.description())){
			button.setSelected(true);
		//}			
	}


	@Override
	public Tunable getTunable() {
		// TODO Auto-generated method stub
		return t;
	}


	@Override
	public JPanel update() {
		JPanel result = new JPanel();
		jtf = new JTextField("Has not been Clicked");
		jtf.setBackground(Color.RED);
		if(button.isSelected()){
			jtf.setText("Has been Clicked");
			jtf.setBackground(Color.GREEN);
		}
		result.add(jtf);
		return result;
	}


	@Override
	public void cancel() {
		try{
			button = (AbstractButton) f.get(o);
			button.setSelected(Boolean.parseBoolean(value));
		}catch(Exception e){e.printStackTrace();}
		// TODO Auto-generated method stub
		
	}


	@Override
	public Field getField() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return null;
	}

}
*/