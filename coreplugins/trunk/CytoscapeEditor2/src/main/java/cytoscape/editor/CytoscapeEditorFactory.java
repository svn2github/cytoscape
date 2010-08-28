/* -*-Java-*-
********************************************************************************
*
* File:         CytoscapeEditorFactory.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinsky
* Created:      Mon July 05 18:41:21 2005
* Modified:     Mon Dec 04 18:42:31 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
*
/*
 
 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.

********************************************************************************
*
* Revisions:
*
* Mon Dec 04 18:41:50 2006 (Michael L. Creech) creech@w235krbza760
*  Added createShapePaletteInfoGenerator() and createShapePaletteInfo().
********************************************************************************
*/
package cytoscape.editor;

import java.util.Collection;
import java.util.Iterator;

import cytoscape.editor.event.NetworkEditEventAdapter;
import cytoscape.editor.impl.CytoscapeEditorFactoryImpl;


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
	/**
	 *
	 */
	public static CytoscapeEditorFactory INSTANCE = new CytoscapeEditorFactoryImpl();

	/**
	 * Get the network editor, creating a new one if it doesn't already exist.
	 * @param editorType    non null type of editor to get
	 * @return editor
	 * @throws InvalidEditorException if the editor cannot be created
	 */
	public CytoscapeEditor getEditor(String editorType) throws InvalidEditorException;

	/**
	 * Get the set of valid editor types
	 * @return    non null collection of editor types (String)
	 */
	public Collection getEditorTypes();

	/**
	 * Returns a non-null, read-only Iterator (doesn't allow modification) over
	 * all the fully qualified names of Editors.
	 */
	public Iterator<String> getEditorNames();

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
	public NetworkEditEventAdapter getNetworkEditEventAdapter(CytoscapeEditor editor);

	// MLC 12/04/06 BEGIN:
	/**
	 * Create a ShapePaletteInfoGenerator for easily creating ShapePalette
	 * entries.
	 */
	public ShapePaletteInfoGenerator createShapePaletteInfoGenerator();

	/**
	 * Create a new ShapePaletteInfo object based on the given
	 * controlling Attribute name and controlling attribute value.
	 */
	public ShapePaletteInfo createShapePaletteInfo(String controllingAttributeName,
	                                               String controllingAttributeValue);

	// MLC 12/04/06 END.	
}
