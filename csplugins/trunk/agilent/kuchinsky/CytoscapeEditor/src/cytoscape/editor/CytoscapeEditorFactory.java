/*
 * Created on Jul 5, 2005
 *
 * 
 */
package cytoscape.editor;

import java.util.Collection;


import cytoscape.editor.event.NetworkEditEventAdapter;
import cytoscape.editor.impl.CytoscapeEditorFactoryImpl;


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
 * Interface used for building new instances of editors.  Before an editor can be built, it first needs to be 
 * registered with the CytoscapeEditorManager.
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see CytoscapeEditorManager
 *
 */

public interface CytoscapeEditorFactory {

    //~ Instance fields ////////////////////////////////////////////////////////

	/**
	 * 
	 */
    public static CytoscapeEditorFactory INSTANCE = new CytoscapeEditorFactoryImpl ();
    
   

	/**
	 * Get the network editor, creating a new one if it doesn't already exist.
	 * @param editorType	non null type of editor to get
	 * @return editor
	 * @throws InvalidEditorException if the editor cannot be created 
	 */	
	public CytoscapeEditor getEditor(String editorType) throws InvalidEditorException;

	
	/**
	 * Get the set of valid editor types
	 * @return	non null collection of editor types (String)
	 */
	public Collection getEditorTypes();
	
	
	/**
	 * adds an editor type to the collection of editor types
	 * @param editorType  a name that specifies the type of the editor
	 */
	public void addEditorType(String editorType);
	
	/**
	 * gets an instance of the NetworkEditEventAdaptor associated with the input editor
	 * The NetworkEditEventAdapter handles events that are associated with user input to the 
	 * editor, such as mouse actions, drag/drop, keystrokes.  Each NetworkEditEventAdapter is specialized
	 * for the editor that is is associated with.  This is written by the developer and is at the heart of 
	 * the specialized behaviour of the editor.
	 * @param editor
	 * @return the NetworkEditEventAdapter that is assigned to the editor
	 * 
	 */
	public NetworkEditEventAdapter getNetworkEditEventAdapter(
			CytoscapeEditor editor) ;
	
}
