/*
 * Created on Jul 5, 2005
 *
 */
package cytoscape.editor.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
import cytoscape.editor.event.NetworkEditEventAdapter;

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
 * builds new instances of editors and network edit event adapters.
 * Before an editor and its network edit event adapter can be built, the editor first needs to be 
 * registered with the CytoscapeEditorManager.
 * <p>
 * This functionality is not available in Cytoscape 2.2
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * 
 * @see CytoscapeEditorManager
 */
public class CytoscapeEditorFactoryImpl implements CytoscapeEditorFactory {

	private Collection editorTypes = new ArrayList();

	/**
	 * mapping of editor types to editors
	 */
	private HashMap editors = new HashMap();



	/**
	 * get the Cytoscape editor for the specified type
	 * @param editorType the type of the editor
	 * @param args an arbitrary list of arguments
	 * @return the Cytoscape editor for the specified editor type
	 * @throws InvalidEditorException
	 */
	public CytoscapeEditor getEditor(String editorType, List args)
			throws InvalidEditorException {

		Class editorClass;
		CytoscapeEditor cyEditor = null;

		Object cyEditObj = editors.get(editorType);
		if (cyEditObj != null) {
			cyEditor = (CytoscapeEditor) cyEditObj;
			return cyEditor;
		}
		try {
			editorClass = Class.forName("cytoscape.editor.editors."
					+ editorType);
			editorTypes.add(editorType);
			cyEditor = (CytoscapeEditor) editorClass.newInstance();
			editors.put(editorType, cyEditor);
			cyEditor.setEditorName(editorType);
		} catch (ClassNotFoundException e) {
			String msg = "Cannot create editor of type: " + editorType;
			InvalidEditorException ex = new InvalidEditorException(msg,
					new Throwable("type not found"));
			ex.printStackTrace();
			throw ex;
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}

		CytoscapeEditorManager.setCurrentEditor(cyEditor);
		return cyEditor;

	}

	public CytoscapeEditor getEditor(String editorType)
			throws InvalidEditorException {
		return getEditor(editorType, null);
	}

	/**
	 * Get the set of valid editor types
	 * 
	 * @return non null collection of editor types (String)
	 */
	public Collection getEditorTypes() {
		return this.editorTypes;
	}

	/**
	 * adds a new editorType to the collection of editor types
	 * 
	 * @param editorType
	 */
	public void addEditorType(String editorType) {
		editorTypes.add(editorType);
	}

	/**
	 * creates the network edit event adapter associated with the editor
	 * @param editor the CytoscapeEditor
	 */
	public NetworkEditEventAdapter getNetworkEditEventAdapter(
			CytoscapeEditor editor) {
		NetworkEditEventAdapter event = null;
		String editorType = editor.getEditorName();
		String adapterName = CytoscapeEditorManager
				.getNetworkEditEventAdapterType(editorType);
		try {
//			Class eventAdapterClass = Class.forName("cytoscape.editor.event."
//					+ adapterName);
			Class eventAdapterClass = Class.forName(adapterName);
			event = (NetworkEditEventAdapter) eventAdapterClass
					.newInstance();
		} catch (ClassNotFoundException ex) {
			String msg = "Cannot create NetworkEditEvent handler of type: "
					+ adapterName;
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		return event;
	}

}