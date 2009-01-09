/*
 File: CalculatorCatalog.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
 - Agilent Technologies
 
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
 */

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

//----------------------------------------------------------------------------
import java.util.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.ui.*;
import cytoscape.data.Semantics;

//----------------------------------------------------------------------------
/**
 * Stores various types of Calculators from data attributes to an attribute of a
 * specified type. Also keeps track of available mappings. Notifies interested
 * classes of changes to the underlying datasets.
 */
public class CalculatorCatalog {

	Map nodeColorCalculators ;
	List nodeColorListeners ;
	Map nodeLineTypeCalculators ;
	List nodeLineTypeListeners ;
	Map nodeShapeCalculators ;
	List nodeShapeListeners ;
	Map nodeSizeCalculators ;
	List nodeSizeListeners ;
	Map nodeLabelCalculators ;
	List nodeLabelListeners ;
	Map nodeLabelColorCalculators ;
	List nodeLabelColorListeners ;
	Map nodeToolTipCalculators ;
	List nodeToolTipListeners ;
	Map nodeFontFaceCalculators ;
	List nodeFontFaceListeners ;
	Map nodeFontSizeCalculators ;
	List nodeFontSizeListeners;
	Map edgeColorCalculators ;
	List edgeColorListeners ;
	Map edgeLineTypeCalculators ;
	List edgeLineTypeListeners ;
	Map edgeArrowCalculators ;
	List edgeArrowListeners ;
	Map edgeLabelCalculators ;
	List edgeLabelListeners ;
	Map edgeToolTipCalculators ;
	List edgeToolTipListeners ;
	Map edgeFontFaceCalculators ;
	List edgeFontFaceListeners ;
	Map edgeFontSizeCalculators ;
	List edgeFontSizeListeners ;
	Map visualStyles ;
	// mapping database
	Map mappers; 

	/**
	 * Only one <code>ChangeEvent</code> is needed per catalog instance since
	 * the event's only state is the source property. The source of events
	 * generated is always "this".
	 */
	protected transient ChangeEvent changeEvent;

	public CalculatorCatalog() {
		clear();
	}

	public CalculatorCatalog(Properties props) {
		clear();
		// should read calculators from their description in the properties
		// object
	}

	public void clear() {
		nodeColorCalculators = new HashMap();
		nodeColorListeners = new Vector(2, 1);

		nodeLineTypeCalculators = new HashMap();
		nodeLineTypeListeners = new Vector(1, 1);

		nodeShapeCalculators = new HashMap();
		nodeShapeListeners = new Vector(1, 1);

		nodeSizeCalculators = new HashMap();
		nodeSizeListeners = new Vector(2, 1);

		nodeLabelCalculators = new HashMap();
		nodeLabelListeners = new Vector(1, 1);

		nodeLabelColorCalculators = new HashMap();
		nodeLabelColorListeners = new Vector(1, 1);

		nodeToolTipCalculators = new HashMap();
		nodeToolTipListeners = new Vector(1, 1);

		nodeFontFaceCalculators = new HashMap();
		nodeFontFaceListeners = new Vector(1, 1);

		nodeFontSizeCalculators = new HashMap();
		nodeFontSizeListeners = new Vector(1, 1);

		edgeColorCalculators = new HashMap();
		edgeColorListeners = new Vector(1, 1);

		edgeLineTypeCalculators = new HashMap();
		edgeLineTypeListeners = new Vector(1, 1);

		edgeArrowCalculators = new HashMap();
		edgeArrowListeners = new Vector(2, 1);

		edgeLabelCalculators = new HashMap();
		edgeLabelListeners = new Vector(1, 1);

		edgeToolTipCalculators = new HashMap();
		edgeToolTipListeners = new Vector(1, 1);

		edgeFontFaceCalculators = new HashMap();
		edgeFontFaceListeners = new Vector(1, 1);

		edgeFontSizeCalculators = new HashMap();
		edgeFontSizeListeners = new Vector(1, 1);

		visualStyles = new HashMap();

		// mapping database
		mappers = new HashMap();

	}

	/**
	 * Given a type argument, returns the List structure that holds the
	 * listeners for that type.
	 * 
	 * @param type
	 *            type of calculator to add to, one of {@link VizMapUI}'s
	 *            constants
	 * @throws IllegalArgumentException
	 *             if unknown type passed in
	 */
	protected List getListenerList(byte type) throws IllegalArgumentException {
		switch (type) {
		case VizMapUI.NODE_COLOR:
		case VizMapUI.NODE_BORDER_COLOR:
			return nodeColorListeners;
		case VizMapUI.NODE_LINETYPE:
			return nodeLineTypeListeners;
		case VizMapUI.NODE_SHAPE:
			return nodeShapeListeners;
		case VizMapUI.NODE_HEIGHT:
		case VizMapUI.NODE_WIDTH:
		case VizMapUI.NODE_SIZE:
			return nodeSizeListeners;
		case VizMapUI.NODE_LABEL:
			return nodeLabelListeners;
		case VizMapUI.NODE_LABEL_COLOR:
			return nodeLabelColorListeners;
		case VizMapUI.NODE_TOOLTIP:
			return nodeToolTipListeners;
		case VizMapUI.NODE_FONT_FACE:
			return nodeFontFaceListeners;
		case VizMapUI.NODE_FONT_SIZE:
			return nodeFontSizeListeners;
		case VizMapUI.EDGE_COLOR:
			return edgeColorListeners;
		case VizMapUI.EDGE_LINETYPE:
			return edgeLineTypeListeners;
		case VizMapUI.EDGE_SRCARROW:
		case VizMapUI.EDGE_TGTARROW:
			return edgeArrowListeners;
		case VizMapUI.EDGE_LABEL:
			return edgeLabelListeners;
		case VizMapUI.EDGE_TOOLTIP:
			return edgeToolTipListeners;
		case VizMapUI.EDGE_FONT_FACE:
			return edgeFontFaceListeners;
		case VizMapUI.EDGE_FONT_SIZE:
			return edgeFontSizeListeners;
		default:
			throw new IllegalArgumentException("Unknown type " + type);
		}
	}

	/**
	 * Add a ChangeListener to the catalog. Depending on the passed-in type, the
	 * catalog will add the ChangeListener to the appropriate listener vector
	 * for the associated set of calculators. When the catalog's database of
	 * calculators changes, the ChangeListener will be notified.
	 * 
	 * This is used in the UI classes to ensure that the UI panes stay
	 * consistent with the data held in the catalog.
	 * 
	 * @param l
	 *            ChangeListener to add
	 * @param type
	 *            type of calculator to add to, one of {@link VizMapUI}'s
	 *            constants
	 * @throws IllegalArgumentException
	 *             if unknown type passed in
	 */
	public void addChangeListener(ChangeListener l, byte type)
			throws IllegalArgumentException {
		List theListeners = getListenerList(type);
		theListeners.add(l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created.
	 * 
	 * Note that fireStateChanged is only triggered by calling
	 * {@link #addCalculator}, {@link #renameCalculator}, or
	 * {@link #removeCalculator}. Manipulating each type explicitly does not
	 * trigger ChangeEvents to be fired. This is because the UI classes only use
	 * the more general methods.
	 * 
	 * However, this behavior does not permit "hidden" calculators. Upon the
	 * next refresh, all calculators contained will be visible.
	 * 
	 * @param type
	 *            one of VizMapUI constants, which set of listeners to notify
	 * @throws IllegalArgumentException
	 *             if type is unknown
	 */
	protected void fireStateChanged(byte type) throws IllegalArgumentException {
		List notifyEvents = getListenerList(type);

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = notifyEvents.size() - 1; i >= 0; i--) {
			ChangeListener listener = (ChangeListener) notifyEvents.get(i);
			// Lazily create the event:
			if (changeEvent == null)
				changeEvent = new ChangeEvent(this);
			listener.stateChanged(changeEvent);
		}
	}

	/**
	 * Given a calculator, returns the matching byte type identifier.
	 * 
	 * @param c
	 *            the calculator
	 * @return byte the byte identifier
	 */
	public byte getType(Calculator c) throws IllegalArgumentException {
		if (c instanceof NodeColorCalculator) {
			return VizMapUI.NODE_COLOR;
		} else if (c instanceof NodeLineTypeCalculator) {
			return VizMapUI.NODE_LINETYPE;
		} else if (c instanceof NodeShapeCalculator) {
			return VizMapUI.NODE_SHAPE;
		} else if (c instanceof NodeSizeCalculator) {
			return VizMapUI.NODE_SIZE;
		} else if (c instanceof NodeLabelCalculator) {
			return VizMapUI.NODE_LABEL;
		} else if (c instanceof NodeLabelColorCalculator) {
			return VizMapUI.NODE_LABEL_COLOR;
		} else if (c instanceof NodeToolTipCalculator) {
			return VizMapUI.NODE_TOOLTIP;
		} else if (c instanceof NodeFontFaceCalculator) {
			return VizMapUI.NODE_FONT_FACE;
		} else if (c instanceof NodeFontSizeCalculator) {
			return VizMapUI.NODE_FONT_SIZE;
		} else if (c instanceof EdgeColorCalculator) {
			return VizMapUI.EDGE_COLOR;
		} else if (c instanceof EdgeLineTypeCalculator) {
			return VizMapUI.EDGE_LINETYPE;
		} else if (c instanceof EdgeArrowCalculator) {
			return VizMapUI.EDGE_SRCARROW;
		} else if (c instanceof EdgeLabelCalculator) {
			return VizMapUI.EDGE_LABEL;
		} else if (c instanceof EdgeToolTipCalculator) {
			return VizMapUI.EDGE_TOOLTIP;
		} else if (c instanceof EdgeFontFaceCalculator) {
			return VizMapUI.EDGE_FONT_FACE;
		} else if (c instanceof EdgeFontSizeCalculator) {
			return VizMapUI.EDGE_FONT_SIZE;
		} else {
			throw new IllegalArgumentException("Unknown calculator type");
		}
	}

	/**
	 * Given a known byte identifier, returns the matching Map structure holding
	 * calculators of that type.
	 * 
	 * @param type
	 *            a known type identifier
	 * @return Map the matching Map structure
	 */
	protected Map getCalculatorMap(byte type) {
		switch (type) {
		case VizMapUI.NODE_COLOR:
		case VizMapUI.NODE_BORDER_COLOR:
			return nodeColorCalculators;
		case VizMapUI.NODE_LINETYPE:
			return nodeLineTypeCalculators;
		case VizMapUI.NODE_SHAPE:
			return nodeShapeCalculators;
		case VizMapUI.NODE_HEIGHT:
		case VizMapUI.NODE_WIDTH:
		case VizMapUI.NODE_SIZE:
			return nodeSizeCalculators;
		case VizMapUI.NODE_LABEL:
			return nodeLabelCalculators;
		case VizMapUI.NODE_LABEL_COLOR:
			return nodeLabelColorCalculators;
		case VizMapUI.NODE_TOOLTIP:
			return nodeToolTipCalculators;
		case VizMapUI.NODE_FONT_FACE:
			return nodeFontFaceCalculators;
		case VizMapUI.NODE_FONT_SIZE:
			return nodeFontSizeCalculators;
		case VizMapUI.EDGE_COLOR:
			return edgeColorCalculators;
		case VizMapUI.EDGE_LINETYPE:
			return edgeLineTypeCalculators;
		case VizMapUI.EDGE_SRCARROW:
		case VizMapUI.EDGE_TGTARROW:
			return edgeArrowCalculators;
		case VizMapUI.EDGE_LABEL:
			return edgeLabelCalculators;
		case VizMapUI.EDGE_TOOLTIP:
			return edgeToolTipCalculators;
		case VizMapUI.EDGE_FONT_FACE:
			return edgeFontFaceCalculators;
		case VizMapUI.EDGE_FONT_SIZE:
			return edgeFontSizeCalculators;
		default:
			throw new IllegalArgumentException("Unknown type " + type);
		}
	}

	/**
	 * Add any calculator to the catalog. Automatically checks type. Calculator
	 * is added according to its name as reported by the toString() method.
	 * 
	 * @param dupe
	 *            Calculator to add
	 * @throws DuplicateCalculatorNameException
	 *             if calculator's name is a duplicate with valid name as detail
	 *             message
	 * @throws IllegalArgumentException
	 *             if calculator is of an unknown type
	 */
	public void addCalculator(Calculator dupe)
			throws DuplicateCalculatorNameException, IllegalArgumentException {
		byte calcType = getType(dupe);
		Map theMap = getCalculatorMap(calcType);
		addCalculator(dupe, theMap);
		// throw event listeners
		fireStateChanged(calcType);
	}

	/**
	 * Checks whether a name for a calculator is valid
	 * 
	 * @param calcName
	 *            Name to check
	 * @param calcType
	 *            Type of calculator {@link cytoscape.visual.ui.VizMapUI}
	 * 
	 * @return a valid name for the calculator. If the given name was not valid,
	 *         numbers are appended until a valid name is found; this valid name
	 *         is returned to the caller.
	 */
	public String checkCalculatorName(String calcName, byte calcType) {
		Map theMap = getCalculatorMap(calcType);
		return checkName(calcName, theMap);
	}

	/**
	 * Renames a calculator.
	 * 
	 * @param c
	 *            Calculator to rename
	 * @param name
	 *            New name for calculator
	 * @throws DuplicateCalculatorNameException
	 *             if name is a duplicate with valid name as detail message
	 * @throws IllegalArgumentException
	 *             if c is of an unknown type
	 */
	public void renameCalculator(Calculator c, String name)
			throws DuplicateCalculatorNameException, IllegalArgumentException {
		byte calcType = getType(c);
		Map theMap = getCalculatorMap(calcType);
		String newName = checkName(name, theMap);
		if (newName.equals(name)) {// given name is unique
			theMap.remove(c.toString());
			c.setName(name);
			theMap.put(name, c);
			fireStateChanged(calcType);
		} else {
			throw new DuplicateCalculatorNameException(newName);
		}
	}

	/**
	 * Remove a calculator.
	 * 
	 * @param c
	 *            Calculator to remove
	 * @throws IllegalArgumentException
	 *             if c is of an unknown calculator type
	 */
	public void removeCalculator(Calculator c) throws IllegalArgumentException {
		byte calcType = getType(c);
		Map theMap = getCalculatorMap(calcType);
		theMap.remove(c.toString());
		// fire event
		fireStateChanged(calcType);
	}

	/**
	 * Returns the HashMap of mappers
	 */
	public Set getMappingNames() {
		return mappers.keySet();
	}

	/**
	 * Add a mapping to the database of available mappings. Because mappings are
	 * instantiated for each calculator, only class types are stored.
	 * 
	 * @param name
	 *            Name of the mapping
	 * @param m
	 *            Class of the mapping
	 * @throws DuplicateCalculatorNameException
	 *             if the given name is already taken
	 * @throws IllegalArgumentException
	 *             if the given class is not in the mapping hierarchy
	 */
	public void addMapping(String name, Class m)
			throws DuplicateCalculatorNameException, IllegalArgumentException {
		// verify that the class is in the mapping hierarchy
		if (!ObjectMapping.class.isAssignableFrom(m))
			throw new IllegalArgumentException("Class " + m.getName()
					+ " is not an ObjectMapper!");

		// check for duplicate names
		if (mappers.keySet().contains(name))
			throw new DuplicateCalculatorNameException("Duplicate mapper name "
					+ name);
		mappers.put(name, m);
	}

	public Class removeMapping(String name) {
		return (Class) mappers.remove(name);
	}

	public Class getMapping(String name) {
		return (Class) mappers.get(name);
	}

	public String checkMappingName(String name) {
		String newName = name;
		int nameApp = 2;
		while (mappers.keySet().contains(newName)) {
			newName = name + nameApp;
			nameApp++;
		}
		return newName;
	}

	public Set getVisualStyleNames() {
		return visualStyles.keySet();
	}

	public Collection getVisualStyles() {
		return visualStyles.values();
	}

	public void addVisualStyle(VisualStyle vs) {
		if (vs == null) {
			return;
		}
		String name = vs.toString();
		// check for duplicate names
		System.out.println ("Keyset = " + visualStyles.keySet());
		if (visualStyles.keySet().contains(name)) {
			String s = "Duplicate visual style name " + name;
			throw new DuplicateCalculatorNameException(s);
		}
		visualStyles.put(name, vs);
		// store the individual attribute calculators via helper methods
		addNodeAppearanceCalculator(vs.getNodeAppearanceCalculator());
		addEdgeAppearanceCalculator(vs.getEdgeAppearanceCalculator());
	}

	public VisualStyle removeVisualStyle(String name) {
		return (VisualStyle) visualStyles.remove(name);
	}

	public VisualStyle getVisualStyle(String name) {
		if (name != null && name.equals("default")
				&& !visualStyles.containsKey(name))
			createDefaultVisualStyle();
		return (VisualStyle) visualStyles.get(name);
	}

	public String checkVisualStyleName(String name) {
		return checkName(name, visualStyles);
	}

	private void addNodeAppearanceCalculator(NodeAppearanceCalculator c) {
		NodeColorCalculator ncc1 = c.getNodeFillColorCalculator();
		if (ncc1 != null && !nodeColorCalculators.values().contains(ncc1)) {
			addNodeColorCalculator(ncc1);
		}
		NodeColorCalculator ncc2 = c.getNodeBorderColorCalculator();
		if (ncc2 != null && !nodeColorCalculators.values().contains(ncc2)) {
			addNodeColorCalculator(ncc2);
		}
		NodeLineTypeCalculator nltc = c.getNodeLineTypeCalculator();
		if (nltc != null && !nodeLineTypeCalculators.values().contains(nltc)) {
			addNodeLineTypeCalculator(nltc);
		}
		NodeShapeCalculator nsc = c.getNodeShapeCalculator();
		if (nsc != null && !nodeShapeCalculators.values().contains(nsc)) {
			addNodeShapeCalculator(nsc);
		}
		NodeSizeCalculator nsc1 = c.getNodeHeightCalculator();
		if (nsc1 != null && !nodeSizeCalculators.values().contains(nsc1)) {
			addNodeSizeCalculator(nsc1);
		}
		NodeSizeCalculator nsc2 = c.getNodeWidthCalculator();
		if (nsc2 != null && !nodeSizeCalculators.values().contains(nsc2)) {
			addNodeSizeCalculator(nsc2);
		}
		NodeLabelCalculator nlc = c.getNodeLabelCalculator();
		if (nlc != null && !nodeLabelCalculators.values().contains(nlc)) {
			addNodeLabelCalculator(nlc);
		}
		NodeLabelColorCalculator nlcc = c.getNodeLabelColorCalculator();
		if (nlcc != null && !nodeLabelColorCalculators.values().contains(nlcc)) {
			addNodeLabelColorCalculator(nlcc);
		}
		NodeToolTipCalculator nttc = c.getNodeToolTipCalculator();
		if (nttc != null && !nodeToolTipCalculators.values().contains(nttc)) {
			addNodeToolTipCalculator(nttc);
		}
		NodeFontFaceCalculator nffc = c.getNodeFontFaceCalculator();
		if (nffc != null && !nodeFontFaceCalculators.values().contains(nffc)) {
			addNodeFontFaceCalculator(nffc);
		}
		NodeFontSizeCalculator nfsc = c.getNodeFontSizeCalculator();
		if (nfsc != null && !nodeFontSizeCalculators.values().contains(nfsc)) {
			addNodeFontSizeCalculator(nfsc);
		}
	}

	private void addEdgeAppearanceCalculator(EdgeAppearanceCalculator c) {
		EdgeColorCalculator ecc = c.getEdgeColorCalculator();
		if (ecc != null && !edgeColorCalculators.values().contains(ecc)) {
			addEdgeColorCalculator(ecc);
		}
		EdgeLineTypeCalculator eltc = c.getEdgeLineTypeCalculator();
		if (eltc != null && !edgeLineTypeCalculators.values().contains(eltc)) {
			addEdgeLineTypeCalculator(eltc);
		}
		EdgeArrowCalculator eac1 = c.getEdgeSourceArrowCalculator();
		if (eac1 != null && !edgeArrowCalculators.values().contains(eac1)) {
			addEdgeArrowCalculator(eac1);
		}
		EdgeArrowCalculator eac2 = c.getEdgeTargetArrowCalculator();
		if (eac2 != null && !edgeArrowCalculators.values().contains(eac2)) {
			addEdgeArrowCalculator(eac2);
		}
		EdgeLabelCalculator elc = c.getEdgeLabelCalculator();
		if (elc != null && !edgeLabelCalculators.values().contains(elc)) {
			addEdgeLabelCalculator(elc);
		}
		EdgeToolTipCalculator ettc = c.getEdgeToolTipCalculator();
		if (ettc != null && !edgeToolTipCalculators.values().contains(ettc)) {
			addEdgeToolTipCalculator(ettc);
		}
		EdgeFontFaceCalculator effc = c.getEdgeFontFaceCalculator();
		if (effc != null && !edgeFontFaceCalculators.values().contains(effc)) {
			addEdgeFontFaceCalculator(effc);
		}
		EdgeFontSizeCalculator efsc = c.getEdgeFontSizeCalculator();
		if (efsc != null && !edgeFontSizeCalculators.values().contains(efsc)) {
			addEdgeFontSizeCalculator(efsc);
		}
	}

	protected void addCalculator(Calculator c, Map m)
			throws DuplicateCalculatorNameException {
		if (c == null) {
			return;
		}
		String name = c.toString();
		// check for duplicate names
		if (m.keySet().contains(name)) {
			String s = "Duplicate calculator name " + name;
			throw new DuplicateCalculatorNameException(s);
		}
		m.put(name, c);
	}

	protected String checkName(String name, Map m) {
		if (name == null) {
			return null;
		}
		String newName = name;
		int nameApp = 2;
		while (m.keySet().contains(newName)) {
			newName = name + nameApp;
			nameApp++;
		}
		return newName;
	}

	public Collection getNodeColorCalculators() {
		return nodeColorCalculators.values();
	}

	public void addNodeColorCalculator(NodeColorCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeColorCalculators);
	}

	public NodeColorCalculator removeNodeColorCalculator(String name) {
		return (NodeColorCalculator) nodeColorCalculators.remove(name);
	}

	public NodeColorCalculator getNodeColorCalculator(String name) {
		return (NodeColorCalculator) nodeColorCalculators.get(name);
	}

	public String checkNodeColorCalculatorName(String name) {
		return checkName(name, nodeColorCalculators);
	}

	public Collection getNodeLineTypeCalculators() {
		return nodeLineTypeCalculators.values();
	}

	public void addNodeLineTypeCalculator(NodeLineTypeCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeLineTypeCalculators);
	}

	public NodeLineTypeCalculator removeNodeLineTypeCalculator(String name) {
		return (NodeLineTypeCalculator) nodeLineTypeCalculators.remove(name);
	}

	public NodeLineTypeCalculator getNodeLineTypeCalculator(String name) {
		return (NodeLineTypeCalculator) nodeLineTypeCalculators.get(name);
	}

	public String checkNodeLineTypeCalculatorName(String name) {
		return checkName(name, nodeLineTypeCalculators);
	}

	public Collection getNodeShapeCalculators() {
		return nodeShapeCalculators.values();
	}

	public void addNodeShapeCalculator(NodeShapeCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeShapeCalculators);
	}

	public NodeShapeCalculator removeNodeShapeCalculator(String name) {
		return (NodeShapeCalculator) nodeShapeCalculators.remove(name);
	}

	public NodeShapeCalculator getNodeShapeCalculator(String name) {
		return (NodeShapeCalculator) nodeShapeCalculators.get(name);
	}

	public String checkNodeShapeCalculatorName(String name) {
		return checkName(name, nodeShapeCalculators);
	}

	public Collection getNodeSizeCalculators() {
		return nodeSizeCalculators.values();
	}

	public void addNodeSizeCalculator(NodeSizeCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeSizeCalculators);
	}

	public NodeSizeCalculator removeNodeSizeCalculator(String name) {
		return (NodeSizeCalculator) nodeSizeCalculators.remove(name);
	}

	public NodeSizeCalculator getNodeSizeCalculator(String name) {
		return (NodeSizeCalculator) nodeSizeCalculators.get(name);
	}

	public String checkNodeSizeCalculatorName(String name) {
		return checkName(name, nodeSizeCalculators);
	}

	public Collection getNodeLabelCalculators() {
		return nodeLabelCalculators.values();
	}

	public void addNodeLabelCalculator(NodeLabelCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeLabelCalculators);
	}

	public NodeLabelCalculator removeNodeLabelCalculator(String name) {
		return (NodeLabelCalculator) nodeLabelCalculators.remove(name);
	}

	public NodeLabelCalculator getNodeLabelCalculator(String name) {
		return (NodeLabelCalculator) nodeLabelCalculators.get(name);
	}

	public String checkNodeLabelCalculatorName(String name) {
		return checkName(name, nodeLabelCalculators);
	}

	public Collection getNodeLabelColorCalculators() {
		return nodeLabelColorCalculators.values();
	}

	public void addNodeLabelColorCalculator(NodeLabelColorCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeLabelColorCalculators);
	}

	public NodeLabelColorCalculator removeNodeLabelColorCalculator(String name) {
		return (NodeLabelColorCalculator) nodeLabelColorCalculators
				.remove(name);
	}

	public NodeLabelColorCalculator getNodeLabelColorCalculator(String name) {
		return (NodeLabelColorCalculator) nodeLabelColorCalculators.get(name);
	}

	public String checkNodeLabelColorCalculatorName(String name) {
		return checkName(name, nodeLabelColorCalculators);
	}

	public Collection getNodeToolTipCalculators() {
		return nodeToolTipCalculators.values();
	}

	public void addNodeToolTipCalculator(NodeToolTipCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeToolTipCalculators);
	}

	public NodeToolTipCalculator removeNodeToolTipCalculator(String name) {
		return (NodeToolTipCalculator) nodeToolTipCalculators.remove(name);
	}

	public NodeToolTipCalculator getNodeToolTipCalculator(String name) {
		return (NodeToolTipCalculator) nodeToolTipCalculators.get(name);
	}

	public String checkNodeToolTipCalculatorName(String name) {
		return checkName(name, nodeToolTipCalculators);
	}

	public Collection getNodeFontFaceCalculators() {
		return nodeFontFaceCalculators.values();
	}

	public void addNodeFontFaceCalculator(NodeFontFaceCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeFontFaceCalculators);
	}

	public NodeFontFaceCalculator removeNodeFontFaceCalculator(String name) {
		return (NodeFontFaceCalculator) nodeFontFaceCalculators.remove(name);
	}

	public NodeFontFaceCalculator getNodeFontFaceCalculator(String name) {
		return (NodeFontFaceCalculator) nodeFontFaceCalculators.get(name);
	}

	public String checkNodeFontFaceCalculatorName(String name) {
		return checkName(name, nodeFontFaceCalculators);
	}

	public Collection getNodeFontSizeCalculators() {
		return nodeFontSizeCalculators.values();
	}

	public void addNodeFontSizeCalculator(NodeFontSizeCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, nodeFontSizeCalculators);
	}

	public NodeFontSizeCalculator removeNodeFontSizeCalculator(String name) {
		return (NodeFontSizeCalculator) nodeFontSizeCalculators.remove(name);
	}

	public NodeFontSizeCalculator getNodeFontSizeCalculator(String name) {
		return (NodeFontSizeCalculator) nodeFontSizeCalculators.get(name);
	}

	public String checkNodeFontSizeCalculatorName(String name) {
		return checkName(name, nodeFontSizeCalculators);
	}

	public Collection getEdgeColorCalculators() {
		return edgeColorCalculators.values();
	}

	public void addEdgeColorCalculator(EdgeColorCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, edgeColorCalculators);
	}

	public EdgeColorCalculator removeEdgeColorCalculator(String name) {
		return (EdgeColorCalculator) edgeColorCalculators.remove(name);
	}

	public EdgeColorCalculator getEdgeColorCalculator(String name) {
		return (EdgeColorCalculator) edgeColorCalculators.get(name);
	}

	public String checkEdgeColorCalculatorName(String name) {
		return checkName(name, edgeColorCalculators);
	}

	public Collection getEdgeLineTypeCalculators() {
		return edgeLineTypeCalculators.values();
	}

	public void addEdgeLineTypeCalculator(EdgeLineTypeCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, edgeLineTypeCalculators);
	}

	public EdgeLineTypeCalculator removeEdgeLineTypeCalculator(String name) {
		return (EdgeLineTypeCalculator) edgeLineTypeCalculators.remove(name);
	}

	public EdgeLineTypeCalculator getEdgeLineTypeCalculator(String name) {
		return (EdgeLineTypeCalculator) edgeLineTypeCalculators.get(name);
	}

	public String checkEdgeLineTypeCalculatorName(String name) {
		return checkName(name, edgeLineTypeCalculators);
	}

	public Collection getEdgeArrowCalculators() {
		return edgeArrowCalculators.values();
	}

	public void addEdgeArrowCalculator(EdgeArrowCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, edgeArrowCalculators);
	}

	public EdgeArrowCalculator removeEdgeArrowCalculator(String name) {
		return (EdgeArrowCalculator) edgeArrowCalculators.remove(name);
	}

	public EdgeArrowCalculator getEdgeArrowCalculator(String name) {
		return (EdgeArrowCalculator) edgeArrowCalculators.get(name);
	}

	public String checkEdgeArrowCalculatorName(String name) {
		return checkName(name, edgeArrowCalculators);
	}

	public Collection getEdgeLabelCalculators() {
		return edgeLabelCalculators.values();
	}

	public void addEdgeLabelCalculator(EdgeLabelCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, edgeLabelCalculators);
	}

	public EdgeLabelCalculator removeEdgeLabelCalculator(String name) {
		return (EdgeLabelCalculator) edgeLabelCalculators.remove(name);
	}

	public EdgeLabelCalculator getEdgeLabelCalculator(String name) {
		return (EdgeLabelCalculator) edgeLabelCalculators.get(name);
	}

	public String checkEdgeLabelCalculatorName(String name) {
		return checkName(name, edgeLabelCalculators);
	}

	public Collection getEdgeToolTipCalculators() {
		return edgeToolTipCalculators.values();
	}

	public void addEdgeToolTipCalculator(EdgeToolTipCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, edgeToolTipCalculators);
	}

	public EdgeToolTipCalculator removeEdgeToolTipCalculator(String name) {
		return (EdgeToolTipCalculator) edgeToolTipCalculators.remove(name);
	}

	public EdgeToolTipCalculator getEdgeToolTipCalculator(String name) {
		return (EdgeToolTipCalculator) edgeToolTipCalculators.get(name);
	}

	public String checkEdgeToolTipCalculatorName(String name) {
		return checkName(name, edgeToolTipCalculators);
	}

	public Collection getEdgeFontFaceCalculators() {
		return edgeFontFaceCalculators.values();
	}

	public void addEdgeFontFaceCalculator(EdgeFontFaceCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, edgeFontFaceCalculators);
	}

	public EdgeFontFaceCalculator removeEdgeFontFaceCalculator(String name) {
		return (EdgeFontFaceCalculator) edgeFontFaceCalculators.remove(name);
	}

	public EdgeFontFaceCalculator getEdgeFontFaceCalculator(String name) {
		return (EdgeFontFaceCalculator) edgeFontFaceCalculators.get(name);
	}

	public String checkEdgeFontFaceCalculatorName(String name) {
		return checkName(name, edgeFontFaceCalculators);
	}

	public Collection getEdgeFontSizeCalculators() {
		return edgeFontSizeCalculators.values();
	}

	public void addEdgeFontSizeCalculator(EdgeFontSizeCalculator c)
			throws DuplicateCalculatorNameException {
		addCalculator(c, edgeFontSizeCalculators);
	}

	public EdgeFontSizeCalculator removeEdgeFontSizeCalculator(String name) {
		return (EdgeFontSizeCalculator) edgeFontSizeCalculators.remove(name);
	}

	public EdgeFontSizeCalculator getEdgeFontSizeCalculator(String name) {
		return (EdgeFontSizeCalculator) edgeFontSizeCalculators.get(name);
	}

	public String checkEdgeFontSizeCalculatorName(String name) {
		return checkName(name, edgeFontSizeCalculators);
	}

	public void createDefaultVisualStyle() {
		// System.out.println("Creating default visual style");
		VisualStyle defaultVS = new VisualStyle("default");
		// Commented by iavila on 5.5.06
		// setup the default to at least put canonical names on the nodes
		// String cName = "Common Names";
		// NodeLabelCalculator nlc = getNodeLabelCalculator(cName);
		// if (nlc == null) {
		// PassThroughMapping m = new PassThroughMapping("",
		// AbstractCalculator.ID);
		// nlc = new GenericNodeLabelCalculator(cName, m);
		// }

		// Use Semantics.LABEL instead for node labels
		String label = Semantics.LABEL;
		NodeLabelCalculator nlc = getNodeLabelCalculator(label);
		if (nlc == null) {
			PassThroughMapping m = 
				new PassThroughMapping("",AbstractCalculator.ID);
			nlc = new GenericNodeLabelCalculator(label, m);
		}

		defaultVS.getNodeAppearanceCalculator().setNodeLabelCalculator(nlc);
		addVisualStyle(defaultVS);
	}
}
