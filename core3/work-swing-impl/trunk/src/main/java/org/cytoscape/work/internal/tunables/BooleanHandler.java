
package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;



public class BooleanHandler extends AbstractGuiHandler {

	private JCheckBox jcb;
	private Boolean myBoolean;
	private boolean horizontal=false;
	
	protected BooleanHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myBoolean = (Boolean) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		panel = new JPanel(new BorderLayout());
		jcb = new JCheckBox();
		jcb.setSelected(myBoolean.booleanValue());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null,Font.PLAIN,12));
		jcb.addActionListener(this);

		for(Param par : t.alignment())if(par==Param.horizontal) horizontal=true;
		
		if(horizontal){
			panel.add(label,BorderLayout.NORTH);
			panel.add(jcb,BorderLayout.SOUTH);
		}
		else{
			panel.add(label,BorderLayout.WEST);
			panel.add(jcb,BorderLayout.EAST);
		}
	}

	public void handle() {
		try {
			f.set(o,jcb.isSelected());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void resetValue(){
		System.out.println("#########Value will be reset to initial value = "+ myBoolean + "#########");
		try{
			f.set(o,myBoolean);
		}catch(Exception e){e.printStackTrace();}
	}

	
	
	public String getState() {
		String s;
		try {
			s = f.get(o).toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
	}
}
