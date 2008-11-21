/* -*-Java-*-
 ********************************************************************************
 *
 * File:         CytoscapeEditorFactoryImpl.java
 * RCS:          $Header: $
 * Description:
 * Author:       Allan Kuchinsky
 * Created:      Mon Jul 05 18:56:28 2006
 * Modified:     Mon Dec 04 18:58:34 2006 (Michael L. Creech) creech@w235krbza760
 * Language:     Java
 * Package:
 * Status:       Experimental (Do Not Distribute)
 *
 * (c) Copyright 2006, Agilent Technologies, all rights reserved.
 *
 ********************************************************************************
 *
 * Revisions:
 *
 * Mon Dec 04 18:56:57 2006 (Michael L. Creech) creech@w235krbza760
 *  Added createShapePaletteInfoGenerator() and createShapePaletteInfo().
 ********************************************************************************
 */

/*
 * Created on Jul 5, 2005
 *
 */
package cytoscape.editor.impl;

import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
import cytoscape.editor.ShapePaletteInfo;
import cytoscape.editor.ShapePaletteInfoGenerator;
import cytoscape.editor.event.NetworkEditEventAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *
 * builds new instances of editors and network edit event adapters. Before an
 * editor and its network edit event adapter can be built, the editor first
 * needs to be registered with the CytoscapeEditorManager.
 *
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 * @see CytoscapeEditorManager
 */
public class CytoscapeEditorFactoryImpl implements CytoscapeEditorFactory {
	private Collection<String> editorTypes = new ArrayList<String>();

	/**
	 * mapping of editor types to editors
	 */
	private Map<String, CytoscapeEditor> editors = new HashMap<String, CytoscapeEditor>();

	/**
	 * get the Cytoscape editor for the specified type
	 *
	 * @param editorType
	 *            the type of the editor
	 * @param args
	 *            an arbitrary list of arguments
	 * @return the Cytoscape editor for the specified editor type
	 * @throws InvalidEditorException
	 */
	public CytoscapeEditor getEditor(String editorType, List args) throws InvalidEditorException {
		Class editorClass;
		CytoscapeEditor cyEditor = null;

		Object cyEditObj = editors.get(editorType);

		if (cyEditObj != null) {
			cyEditor = (CytoscapeEditor) cyEditObj;

			return cyEditor;
		}

		if (editorType == null) {
			editorType = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;
			cyEditor = (CytoscapeEditor) editors.get(editorType);

			if (cyEditor != null) {
				return cyEditor;
			}
		}

		// AJK: 12/10/06 END
		try {
			// AJK: 12/09/06 have the CytoscapeEditorFactory take a fully
			// qualified path
			// for editor
			// editorClass = Class.forName("cytoscape.editor.editors." +
			CytoscapeEditorManager.log("trying to instantiate editor type: " + editorType);
			// AJK: we have to now correct for a null editorType, since we are
			// no longer
			// prepending a "cytoscape.editor.editors." to a string, so we
			// can get a NullPointerException on Class.forName
			editorClass = Class.forName(editorType);
			editorTypes.add(editorType);
			CytoscapeEditorManager.log("trying to instantiate editor class: " + editorClass);
			cyEditor = (CytoscapeEditor) editorClass.newInstance();
			CytoscapeEditorManager.log("got editor: " + cyEditor);
			editors.put(editorType, cyEditor);
			cyEditor.setEditorName(editorType);
		} catch (ClassNotFoundException e) {
			// AJK: 12/10/06 BEGIN
			// for backward compatibility, try prepending
			// "cytoscape.editor.editors" to editorType
			try {
				editorClass = Class.forName((new String("cytoscape.editor.editors." + editorType)));
				editorTypes.add(editorType);
				cyEditor = (CytoscapeEditor) editorClass.newInstance();
				CytoscapeEditorManager.log("got editor: " + cyEditor);
				editors.put(editorType, cyEditor);
				cyEditor.setEditorName(editorType);
			} catch (ClassNotFoundException e1) {
				String msg = "Cannot create editor of type: " + editorType;
				InvalidEditorException ex = new InvalidEditorException(msg,
				                                                       new Throwable("type not found"));

				// ex.printStackTrace();
				throw ex;
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			}

			// AJK: 12/10/06 END
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}

		CytoscapeEditorManager.setCurrentEditor(cyEditor);

		CytoscapeEditorManager.log("returning editor: " + cyEditor);

		return cyEditor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param editorType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws InvalidEditorException DOCUMENT ME!
	 */
	public CytoscapeEditor getEditor(String editorType) throws InvalidEditorException {
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

	// implements CytoscapeEditorFactory interface:
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Iterator<String> getEditorNames() {
		return Collections.unmodifiableCollection(this.editorTypes).iterator();
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
	 * gets an instance of the NetworkEditEventAdaptor associated with the input
	 * editor The NetworkEditEventAdapter handles events that are associated
	 * with user input to the editor, such as mouse actions, drag/drop,
	 * keystrokes. Each NetworkEditEventAdapter is specialized for the editor
	 * that is is associated with. This is written by the developer and is at
	 * the heart of the specialized behaviour of the editor.
	 *
	 * @param editor
	 * @return the NetworkEditEventAdapter that is assigned to the editor
	 *
	 */
	public NetworkEditEventAdapter getNetworkEditEventAdapter(CytoscapeEditor editor) {
		return editor.getNetworkEditEventAdapter();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ShapePaletteInfoGenerator createShapePaletteInfoGenerator() {
		return new ShapePaletteInfoGeneratorImpl();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param controllingAttributeName DOCUMENT ME!
	 * @param controllingAttributeValue DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ShapePaletteInfo createShapePaletteInfo(String controllingAttributeName,
	                                               String controllingAttributeValue) {
		return new ShapePaletteInfoImpl(controllingAttributeName, controllingAttributeValue);
	}
}
