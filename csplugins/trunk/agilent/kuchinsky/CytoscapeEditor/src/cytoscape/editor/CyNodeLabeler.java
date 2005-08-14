/*
 * Created on May 22, 2005
 *
 */
package cytoscape.editor;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * <b>CyNodeLabeler</b> is a wrapper
 * for JTextField, used for editing and setting the label (identifier) of a CyNode
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 * 
 */
public class CyNodeLabeler extends JTextField {

	/**
	 * 
	 */
	public CyNodeLabeler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param columns initial number of columns for the text field
	 */
	public CyNodeLabeler(int columns) {
		super(columns);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param text initial text value in the text field
	 */
	public CyNodeLabeler(String text) {
		super(text);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param text initial text value in the text field
	 * @param columns initial number of columns for the text field
	 */
	public CyNodeLabeler(String text, int columns) {
		super(text, columns);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param doc document associated with the text field
	 * @param text initial text value in the text field
	 * @param columns initial number of columns for the text field
	 */
	public CyNodeLabeler(Document doc, String text, int columns) {
		super(doc, text, columns);
		// TODO Auto-generated constructor stub
	}
}
