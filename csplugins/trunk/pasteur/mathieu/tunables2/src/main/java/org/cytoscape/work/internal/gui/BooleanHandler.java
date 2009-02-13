
package org.cytoscape.work.internal.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.work.AbstractGuiHandler;
import org.cytoscape.work.Tunable;


public class BooleanHandler extends AbstractGuiHandler {

	JCheckBox jcb;
	Boolean myBoolean;
	
	public BooleanHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	
		try{
			this.myBoolean = (Boolean) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel = new JPanel(new BorderLayout());
		try {
			jcb = new JCheckBox();
			jcb.setSelected(myBoolean.booleanValue());
			JLabel label = new JLabel(t.description());
			label.setFont(new Font(null, Font.PLAIN,12));
			jcb.addActionListener(this);
			panel.add(label,BorderLayout.WEST);
			panel.add( jcb ,BorderLayout.EAST);
		} catch (Exception e) { e.printStackTrace(); }
	}

	public void handle() {
		try {
		f.set(o,jcb.isSelected());
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	
	public void returnPanel(){
		panel.removeAll();
		panel.add(new JCheckBox(t.description(),jcb.isSelected()));
	}
	
	
	public String getState() {
		String s;
		try {
			s = f.get(o).toString();
		//s = Boolean.toString(f.getBoolean(o));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
	}
}
