/*
 * Created on Jul 5, 2005
 *
 */
package cytoscape.editor;


/**
 * NOTE: THE CYTOSCAPE EDITOR FUNCTIONALITY IS STILL BEING EVOLVED AND IN A STATE OF TRANSITION TO A 
 * FULLY EXTENSIBLE EDITING FRAMEWORK FOR CYTOSCAPE VERSION 2.3.  
 * 
 * THE JAVADOC COMMENTS ARE OUT OF DATE IN MANY PLACES AND ARE BEING UPDATED.  
 * THE APIs WILL CHANGE AND THIS MAY IMPACT YOUR CODE IF YOU 
 * MAKE EXTENSIONS AT THIS POINT.  PLEASE CONTACT ME (mailto: allan_kuchinsky@agilent.com) 
 * IF YOU ARE INTENDING TO EXTEND THIS CODE AND I WILL WORK WITH YOU TO HELP MINIMIZE THE IMPACT TO YOUR CODE OF 
 * FUTURE CHANGES TO THE FRAMEWORK
 *
 * PLEASE SEE http://www.cytoscape.org/cgi-bin/moin.cgi/CytoscapeEditorFramework FOR 
 * DETAILS ON THE EDITOR FRAMEWORK AND PLANNED EVOLUTION FOR CYTOSCAPE VERSION 2.3.
 *
 */

/**
 * 
 * Create a new exception when there is no registered editor corresponding
 * to the editor type suppied to the CytoscapeEditorFactory.getEditor() method.
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 */
public class InvalidEditorException extends Exception {
	
	/**
	 * Create a new exception when there is no registered editor corresponding
	 * to the editor type suppied to the CytoscapeEditorFactory.getEditor() method.
	 * @param msg	message, may be null
	 * @param cause	cause, may be null
	 * @see CytoscapeEditorFactory
	 */
	public InvalidEditorException(String msg, Throwable cause) {
		super(msg,cause);
	}
	

}
