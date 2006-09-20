/*
 File: CalculatorCatalog.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
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

	Map<Byte,Map<String,Calculator>> calculators;
	Map<Byte,List> listeners;

	Map visualStyles ;
	
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
		calculators = new HashMap<Byte,Map<String,Calculator>>();
		listeners = new HashMap<Byte,List>();

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
		Byte b = new Byte(type);
		List l = listeners.get( b );
		if ( l == null ) {
			l = new ArrayList();
			listeners.put( b, l ); 
		}
		return l;

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
		return c.getType();
	}

	/**
	 * Given a known byte identifier, returns the matching Map structure holding
	 * calculators of that type.
	 * 
	 * @param type
	 *            a known type identifier
	 * @return Map the matching Map structure
	 */
	protected Map<String,Calculator> getCalculatorMap(byte type) {
		Byte b = new Byte(type); 
		Map<String,Calculator> m = calculators.get( b );
		if ( m == null ) { 
			m = new HashMap<String,Calculator>();
			calculators.put(b,m);
		}
		return m;
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
		Map<String,Calculator> theMap = getCalculatorMap(calcType);
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
		Map<String,Calculator> theMap = getCalculatorMap(calcType);
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
		Map<String,Calculator> theMap = getCalculatorMap(calcType);
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
		Map<String,Calculator> theMap = getCalculatorMap(calcType);
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
			throw new DuplicateCalculatorNameException("Duplicate mapper name " + name);
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
		System.out.println("visual style name " + name);
		// check for duplicate names
		//System.out.println ("Keyset = " + visualStyles.keySet());
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
		for ( Calculator cc : c.getCalculators() ) {
			Byte b = new Byte(cc.getType());
			Map m = calculators.get(b);
			if ( !m.values().contains(cc) )
				m.put(cc.toString(),cc);
		}
	}

	private void addEdgeAppearanceCalculator(EdgeAppearanceCalculator c) {
		for ( Calculator cc : c.getCalculators() ) {
			Byte b = new Byte(cc.getType());
			Map m = calculators.get(b);
			if ( !m.values().contains(cc) )
				m.put(cc.toString(),cc);
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

	public Collection<Calculator> getCalculators(byte b) {	
		Map<String,Calculator> m = getCalculatorMap(b); 
		return m.values();
	}

	public Calculator getCalculator(byte b, String name) {	
		Map<String,Calculator> m = getCalculatorMap(b); 
		return (Calculator)m.get(name);
	}

	public String checkCalculatorName(byte b, String name) {
		return checkName(name,getCalculatorMap(b));
	}
	
	public Calculator removeCalculator(byte b, String name) {	
		Map<String,Calculator> m = getCalculatorMap(b); 
		return m.remove(name);
	}

	public Collection<Byte> getCalculatorTypes() {
		return calculators.keySet();
	}


	public void createDefaultVisualStyle() {
		// System.out.println("Creating default visual style");
		VisualStyle defaultVS = new VisualStyle("default");

		// Use Semantics.LABEL instead for node labels
		String label = Semantics.LABEL;
		Calculator nlc = getCalculator(VizMapUI.NODE_LABEL, label);
		if (nlc == null) {
			PassThroughMapping m = new PassThroughMapping("",AbstractCalculator.ID);
			nlc = new GenericNodeLabelCalculator(label, m);
		}

		defaultVS.getNodeAppearanceCalculator().setCalculator(nlc);
		addVisualStyle(defaultVS);
	}

}
