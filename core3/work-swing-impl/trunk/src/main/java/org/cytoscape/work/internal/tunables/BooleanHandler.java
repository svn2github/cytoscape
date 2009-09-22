
package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.Field;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


/**
 * Handler for the type <i>Boolean</i> of <code>Tunable</code>
 * 
 * @author pasteur
 */
public class BooleanHandler extends AbstractGuiHandler {

	private JCheckBox checkBox;
	private Boolean myBoolean;
	private boolean horizontal = false;
	
	
	/**
	 * Constructs the <code>Guihandler</code> for the <code>Boolean</code> type
	 * 
	 * It creates the Swing component for this Object (JCheckBox) with its description/initial state,  and displays it
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	protected BooleanHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myBoolean = (Boolean) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		//set Gui
		panel = new JPanel(new BorderLayout());
		checkBox = new JCheckBox();
		checkBox.setSelected(myBoolean.booleanValue());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null,Font.PLAIN,12));
		checkBox.addActionListener(this);

		
		//choose the way the textField and its label will be displayed to user
		for(Param s : t.alignment())if(s.equals(Param.horizontal)) horizontal = true;
		if(horizontal){
			panel.add(label,BorderLayout.NORTH);
			panel.add(checkBox,BorderLayout.SOUTH);
		}
		else{
			panel.add(label,BorderLayout.WEST);
			panel.add(checkBox,BorderLayout.EAST);
		}
	}

	/**
	 * To set the current value represented in the <code>Guihandler</code> (in a <code>JCheckBox</code>)to the value of this <code>Boolean</code> object
	 */
	public void handle() {
		try {
			f.set(o,checkBox.isSelected());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/**
	 * To reset the current value of this <code>BooleanHandler</code>, and set it to the initial one
	 */
	public void resetValue(){
		try{
			f.set(o,myBoolean);
		}catch(Exception e){e.printStackTrace();}
	}

	
	/**
	 * To get the state of the value of the <code>BooleanHandler</code> : <code>true</code> or <code>false</code>
	 */
	public String getState() {
		return String.valueOf(checkBox.isSelected());
	}
}
