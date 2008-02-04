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
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;

import cytoscape.visual.calculators.*;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


//----------------------------------------------------------------------------
/**
 * Stores various types of Calculators from data attributes to an attribute of a
 * specified type. Also keeps track of available mappings. Notifies interested
 * classes of changes to the underlying datasets.
 */
public class CalculatorCatalog {
	private static final String label = "label";
	private Map<VisualPropertyType, Map<String, Calculator>> calculators;
	private Map<VisualPropertyType, List> listeners;
	private Map<String, VisualStyle> visualStyles;
	private Map<String, Class> mappers;

	/**
	 * Only one <code>ChangeEvent</code> is needed per catalog instance since
	 * the event's only state is the source property. The source of events
	 * generated is always "this".
	 */
	protected transient ChangeEvent changeEvent;

	/**
	 * Creates a new CalculatorCatalog object.
	 */
	public CalculatorCatalog() {
		clear();
	}

	/**
	 * Creates a new CalculatorCatalog object.
	 *
	 * @param props DOCUMENT ME!
	 */
	public CalculatorCatalog(Properties props) {
		clear();

		// should read calculators from their description in the properties
		// object
	}

	/**
	 * DOCUMENT ME!
	 */
	public void clear() {
		calculators = new EnumMap<VisualPropertyType, Map<String, Calculator>>(VisualPropertyType.class);
		listeners = new EnumMap<VisualPropertyType, List>(VisualPropertyType.class);

		visualStyles = new HashMap<String, VisualStyle>();

		// mapping database
		mappers = new HashMap<String, Class>();
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
	protected List getListenerList(final VisualPropertyType type) throws IllegalArgumentException {
		List l = listeners.get(type);

		if (l == null) {
			l = new ArrayList();
			listeners.put(type, l);
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
	public void addChangeListener(ChangeListener l, VisualPropertyType type)
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
	protected void fireStateChanged(final VisualPropertyType type) throws IllegalArgumentException {
		List notifyEvents = getListenerList(type);

		ChangeListener listener;

		for (int i = notifyEvents.size() - 1; i >= 0; i--) {
			listener = (ChangeListener) notifyEvents.get(i);

			// Lazily create the event:
			if (changeEvent == null)
				changeEvent = new ChangeEvent(this);

			listener.stateChanged(changeEvent);
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
	protected Map<String, Calculator> getCalculatorMap(final VisualPropertyType type) {
		Map<String, Calculator> m = calculators.get(type);

		if (m == null) {
			m = new HashMap<String, Calculator>();

			if (type == null)
				return m;

			calculators.put(type, m);
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
		System.out.println("adding calculator: " + dupe.getVisualPropertyType());
		final VisualPropertyType calcType = dupe.getVisualPropertyType();
		Map<String, Calculator> theMap = getCalculatorMap(calcType);
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
	public String checkCalculatorName(String calcName, VisualPropertyType calcType) {
		Map<String, Calculator> theMap = getCalculatorMap(calcType);

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
		final VisualPropertyType calcType = c.getVisualPropertyType();
		final Map<String, Calculator> theMap = getCalculatorMap(calcType);
		final String newName = checkName(name, theMap);

		if (newName.equals(name)) { // given name is unique
			theMap.remove(c.toString());
			c.setName(name);
			theMap.put(name, c);
			fireStateChanged(calcType);
		} else
			throw new DuplicateCalculatorNameException(newName);
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
		final VisualPropertyType calcType = c.getVisualPropertyType();
		final Map<String, Calculator> theMap = getCalculatorMap(calcType);

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
			throw new IllegalArgumentException("Class " + m.getName() + " is not an ObjectMapper!");

		// check for duplicate names
		if (mappers.keySet().contains(name))
			throw new DuplicateCalculatorNameException("Duplicate mapper name " + name);

		mappers.put(name, m);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Class removeMapping(String name) {
		return mappers.remove(name);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Class getMapping(String name) {
		return mappers.get(name);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String checkMappingName(String name) {
		String newName = name;
		int nameApp = 2;

		while (mappers.keySet().contains(newName)) {
			newName = name + nameApp;
			nameApp++;
		}

		return newName;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Set<String> getVisualStyleNames() {
		return visualStyles.keySet();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Collection<VisualStyle> getVisualStyles() {
		return visualStyles.values();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param vs DOCUMENT ME!
	 */
	public void addVisualStyle(final VisualStyle vs) {
		if (vs == null)
			return;

		final String name = vs.toString();

		// check for duplicate names
		if (visualStyles.keySet().contains(name)) {
			String s = "Duplicate visual style name " + name;
			throw new DuplicateCalculatorNameException(s);
		}

		visualStyles.put(name, vs);

		// store the individual attribute calculators via helper methods
		addNodeAppearanceCalculator(vs.getNodeAppearanceCalculator());
		addEdgeAppearanceCalculator(vs.getEdgeAppearanceCalculator());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public VisualStyle removeVisualStyle(String name) {
		return visualStyles.remove(name);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public VisualStyle getVisualStyle(String name) {
		if ((name != null) && name.equals("default") && !visualStyles.containsKey(name))
			createDefaultVisualStyle();

		return visualStyles.get(name);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String checkVisualStyleName(String name) {
		return checkName(name, visualStyles);
	}

	private void addNodeAppearanceCalculator(NodeAppearanceCalculator c) {
		for (Calculator cc : c.getCalculators()) {
			Map m = getCalculatorMap(cc.getVisualPropertyType());

			if (!m.values().contains(cc))
				m.put(cc.toString(), cc);
		}
	}

	private void addEdgeAppearanceCalculator(EdgeAppearanceCalculator c) {
		for (Calculator cc : c.getCalculators()) {
			Map m = getCalculatorMap(cc.getVisualPropertyType());

			if (!m.values().contains(cc))
				m.put(cc.toString(), cc);
		}
	}

	protected void addCalculator(Calculator c, Map m) throws DuplicateCalculatorNameException {
		if (c == null)
			return;

		final String name = c.toString();

		// check for duplicate names
		if (m.keySet().contains(name)) {
			String s = "Duplicate calculator name " + name;
			throw new DuplicateCalculatorNameException(s);
		}

		m.put(name, c);
	}

	protected String checkName(String name, Map m) {
		if (name == null)
			return null;

		String newName = name;
		int nameApp = 2;

		while (m.keySet().contains(newName)) {
			newName = name + nameApp;
			nameApp++;
		}

		return newName;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Collection<Calculator> getCalculators() {
		final List<Calculator> calcList = new ArrayList<Calculator>();

		for (VisualPropertyType type : calculators.keySet()) {
			for (String s : calculators.get(type).keySet())
				calcList.add(calculators.get(type).get(s));
		}

		return calcList;
	}

	/**
	 * Use public Collection<Calculator> getCalculators(VisualPropertyType type) instead.
	 *
	 * @param type
	 * @return
	 */
	public Collection<Calculator> getCalculators(VisualPropertyType type) {
		Map<String, Calculator> m = getCalculatorMap(type);

		return m.values();
	}

	/**
	 * Use public Calculator getCalculator(VisualPropertyType type, String name) instead.
	 *
	 * @param type
	 * @param name
	 * @return
	 */
	public Calculator getCalculator(VisualPropertyType type, String name) {
		Map<String, Calculator> m = getCalculatorMap(type);

		return m.get(name);
	}

	/**
	 * Use public String checkCalculatorName(VisualPropertyType type, String name) instead.
	 *
	 * @param type
	 * @param name
	 * @return
	 */
	public String checkCalculatorName(VisualPropertyType type, String name) {
		return checkName(name, getCalculatorMap(type));
	}

	/**
	 * Use public Calculator removeCalculator(VisualPropertyType type, String name) instead.
	 *
	 * @param type
	 * @param name
	 * @return
	 */
	public Calculator removeCalculator(VisualPropertyType type, String name) {
		Map<String, Calculator> m = getCalculatorMap(type);

		return m.remove(name);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Collection<VisualPropertyType> getCalculatorTypes() {
		return calculators.keySet();
	}

	/**
	 * DOCUMENT ME!
	 */
	public void createDefaultVisualStyle() {
		final VisualStyle defaultVS = new VisualStyle("default");

		Calculator nlc = getCalculator(NODE_LABEL, label);

		if (nlc == null) {
			PassThroughMapping m = new PassThroughMapping("", AbstractCalculator.ID);
			nlc = new BasicCalculator(label, m, NODE_LABEL);
		}

		defaultVS.getNodeAppearanceCalculator().setCalculator(nlc);
		addVisualStyle(defaultVS);
	}

	public void dumpCalculators() {
		System.out.println("---------------------------------------------------------");
		System.out.println("calculators");
		for ( VisualPropertyType p : calculators.keySet() ) {
			System.out.println(p);	
			Map<String,Calculator> m = calculators.get(p);
			for ( String k : m.keySet() ) 
				System.out.println("  " + k + " -> " + m.get(k) );
		}	
	}

}
