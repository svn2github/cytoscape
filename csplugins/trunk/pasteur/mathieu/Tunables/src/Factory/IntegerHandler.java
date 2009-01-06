package Factory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import GuiInterception.Guihandler;
import Tunable.Tunable;

public class IntegerHandler implements Guihandler{
	
	Field f;
	Tunable t;
	Object o;
	JTextField jtf=new JTextField();
	String title;
	Integer inte;

	
	
	public IntegerHandler(Field f, Object o, Tunable t){
		this.f=f;
		this.t=t;
		this.o=o;
		try{
			this.inte=(Integer)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		this.title=f.getName();
	}


	public void handle(){
		try {
				f.set(o,Integer.parseInt(jtf.getText()));
		} catch (Exception e) { e.printStackTrace();}
	}

	
	public JPanel getInputPanel(){
		JPanel pane = new JPanel(new BorderLayout());
		
		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = null;
		titleBorder = BorderFactory.createTitledBorder(f.getType().getSimpleName());
		titleBorder.setTitleColor(Color.blue);
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);

		pane.setBorder(titleBorder);

		JTextArea jta = new JTextArea(f.getName());
		jta.setBackground(null);
		pane.add(jta,BorderLayout.WEST);
		jtf = new JTextField(inte.toString());		
		pane.add(jtf,BorderLayout.EAST);
		return pane;
	}
	
	
	public JPanel update(){
		JPanel result = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea(f.getName());
		jta.setBackground(null);
		result.add(jta,BorderLayout.WEST);
		//inte = Integer.parseInt(jtf.getText());
		try{
			if(inte!=null)f.set(o, Integer.parseInt(jtf.getText()));
			result.add(new JTextField(f.get(o).toString()),BorderLayout.EAST);
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