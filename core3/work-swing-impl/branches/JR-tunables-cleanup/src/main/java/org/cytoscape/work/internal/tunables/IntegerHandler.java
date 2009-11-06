package org.cytoscape.work.internal.tunables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;



/**
 * Handler for the type <i>Integer</i> of <code>Tunable</code>
 * 
 * @author pasteur
 */
public class IntegerHandler extends AbstractGuiHandler {

	private JTextField textField;
	private Double value = null;
	private Integer myInteger;
	private String newline = System.getProperty("line.separator");
	private boolean horizontal = false;

	
	/**
	 * Constructs the <code>Guihandler</code> for the <code>Integer</code> type
	 * 
	 * It creates the Swing component for this Object (JTextField) that contains the initial value of the Integer Object annotated as <code>Tunable</code>, its description, and displays it in a proper way
	 * 
	 * 
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code>
	 */
	public IntegerHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			this.myInteger=(Integer)f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
		//set Gui
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(t.description());
		label.setFont(new Font(null, Font.PLAIN,12));
		textField = new JTextField(myInteger.toString(), 10);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		
		//choose the way the textField and its label will be displayed to user
		for(Param s : t.alignment())if(s.equals(Param.horizontal)) horizontal = true;
		if(horizontal==false){
			panel.add(label,BorderLayout.WEST);
			panel.add(textField,BorderLayout.EAST);
		}
		else{
			panel.add(label,BorderLayout.NORTH);
			panel.add(textField,BorderLayout.SOUTH);
		}
			
	}
	
	
	/**
	 * Catches the value inserted in the JTextField, parses it to a <code>Integer</code> value, and tries to set it to the initial object. If it can't, throws an exception that displays the source error to the user
	 */
	public void handle() {
		try{
			textField.setBackground(Color.white);
			value = Double.parseDouble(textField.getText());
		}catch(NumberFormatException nfe){
			try{
				textField.setBackground(Color.red);
				value = Double.parseDouble(f.get(o).toString());
				JOptionPane.showMessageDialog(null,"An Integer was Expected"+newline+"Value will be set to default = "+value.intValue(), "Error",JOptionPane.ERROR_MESSAGE);
			}catch(Exception e){e.printStackTrace();}
		}
		try {
			f.set(o,value.intValue());
		} catch (Exception e) { e.printStackTrace();}
	}

	
	/**
	 * To reset the current value of this, and set it to the initial one
	 */
	public void resetValue(){
		try{
			f.set(o,myInteger);
		}catch(Exception e){e.printStackTrace();}
	}

	
	/**
	 * To get the state of the <code>IntegerHandler</code> : its current value
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