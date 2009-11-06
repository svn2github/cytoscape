package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.*;
import javax.swing.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;


/**
 * Handler for the type <i>Double</i> of <code>Tunable</code>
 * 
 * @author pasteur
 */
public class DoubleHandler extends AbstractGuiHandler {

	private JTextField textField;
	private Double myDouble;
	private String newline = System.getProperty("line.separator");
	private boolean horizontal = false;
	private double initValue;

	
	/**
	 * Constructs the <code>Guihandler</code> for the <code>Double</code> type
	 * 
	 * It creates the Swing component for this Object (JTextField) that contains the initial value of the Double Object annotated as <code>Tunable</code>, its description, and displays it in a proper way
	 * 
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	protected DoubleHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myDouble = (Double) f.get(o);
		}catch(Exception e){e.printStackTrace();}

		//set Gui
		initValue = myDouble.doubleValue();
		textField = new JTextField(myDouble.toString(), 10);
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		textField.setHorizontalAlignment(JTextField.RIGHT);

		//choose the way the textField and its label will be displayed to user
		for(Param s : t.alignment())if(s.equals(Param.horizontal)) horizontal = true;
		if(horizontal){
			panel.add(label,BorderLayout.NORTH);
			panel.add(textField,BorderLayout.SOUTH);
		}
		else {
			panel.add(label,BorderLayout.WEST);
			panel.add(textField,BorderLayout.EAST);
		}
	}
	
	
	/**
	 * Catches the value inserted in the JTextField, parses it to a <code>Double</code> value, and tries to set it to the initial object. If it can't, throws an exception that displays the source error to the user
	 */
	public void handle() {
		textField.setBackground(Color.white);
		try{
			myDouble = Double.parseDouble(textField.getText());
		}catch(NumberFormatException nfe){
			textField.setBackground(Color.red);
			try{
				myDouble = Double.parseDouble(f.get(o).toString());
			}catch(Exception e){e.printStackTrace();}
			JOptionPane.showMessageDialog(null,"A double was Expected"+newline+"Value will be set to default = "+myDouble.doubleValue(), "Error",JOptionPane.ERROR_MESSAGE);
			try{
				textField.setText(f.get(o).toString());
				textField.setBackground(Color.white);
			}catch(Exception e){e.printStackTrace();}
		}
		try {
			f.set(o,myDouble.doubleValue());
		} catch (Exception e) { e.printStackTrace();}
	}

	
	/**
	 * To reset the current value of this, and set it to the initial one
	 */
	public void resetValue(){
		try {
			f.set(o,initValue);
		} catch (Exception e) { e.printStackTrace();}
	}

	/**
	 * To get the state of the <code>DoubleHandler</code> : its current value
	 * 
	 * @return the value of the object contained in <code>f</code>
	 */
    public String getState() {
		String state;
		try {
			state = f.get(o).toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			state = "";
		}
		return state;
    }
}
