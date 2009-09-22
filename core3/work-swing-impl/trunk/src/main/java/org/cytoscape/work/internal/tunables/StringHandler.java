package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


/**
 * Handler for the type <i>String</i> of <code>Tunable</code>
 * 
 * @author pasteur
 */
public class StringHandler extends AbstractGuiHandler {

	private JTextField textField;
	private boolean horizontal = false;
	private String myString;
	
	
	/**
	 * Constructs the <code>Guihandler</code> for the <code>String</code> type
	 * 
	 * It creates the Swing component for this Object (JTextField) that contains the initial string, adds its description, and displays it in a proper way
	 * 
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	protected StringHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try {
			this.myString = (String)f.get(o);
		} catch (Exception e) {e.printStackTrace(); }
		
		//set Gui
		textField = new JTextField(myString, 15);
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		textField.setHorizontalAlignment(JTextField.RIGHT);
		
		//choose the way the textField and its label will be displayed to user
		for(Param s: t.alignment())if(s.equals(Param.horizontal)) horizontal = true;
		if(horizontal){
			panel.add(label,BorderLayout.NORTH);
			panel.add(textField,BorderLayout.SOUTH);	
		}
		else{
			panel.add(label,BorderLayout.WEST );
			panel.add(textField,BorderLayout.EAST);
		}
	}

	
	/**
	 * Catches the value inserted in the JTextField, and tries to set it to the initial object. If it can't, throws an exception that displays the source error to the user
	 */
	public void handle() {
		String string = textField.getText();
		try {
		if ( string != null )
			f.set(o,string);
		} catch (Exception e) { e.printStackTrace(); }
	}

	
	/**
	 * To reset the string that has been typed to the initial one
	 */
	public void resetValue(){
		try{
			f.set(o,myString);
		}catch(Exception e){e.printStackTrace();}
	}
	

	/**
	 * To get the current string of the <code>String</code> object
	 * 
	 * @return the value of the object contained in <code>f</code>
	 */
	public String getState() {
		String state;
		try {
			state = (String)f.get(o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			state = "";
		}
		return state;
	}
}
