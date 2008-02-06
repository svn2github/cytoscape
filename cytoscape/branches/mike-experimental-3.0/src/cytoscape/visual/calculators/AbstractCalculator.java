/*
 File: AbstractCalculator.java

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

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;

import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.MappingFactory;
import cytoscape.visual.mappings.ObjectMapping;

//------------------------------------------------------------------------------
import giny.model.Edge;
import giny.model.GraphObject;
import giny.model.Node;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


//------------------------------------------------------------------------------
/**
 * AbstractCalculator is the top of the tree for the Calculator classes. <b>DO
 * NOT</b> extend this class directly! All calculators should extend one of
 * {@link NodeCalculator} or {@link EdgeCalculator} TODO this shouldn't be
 * public
 */
public abstract class AbstractCalculator implements Calculator {

	/**
	 * Vector of all mappings contained by this calculator. Usually small.
	 * Contains ObjectMapping objects.
	 */
	protected Vector<ObjectMapping> mappings = new Vector<ObjectMapping>(4, 2);

	/**
	 * The domain classes accepted by the mappings underlying this calculator.
	 * Contains an array of classes - each representing the appropriate domain
	 * classes of the mapping in the {@link #mappings} vector at the same index.
	 */
	protected Vector acceptedDataClasses = new Vector(4, 2);
	protected String name;

	/**
	 * Type of this visual property.
	 * (New in version 2.5)
	 */
	protected VisualPropertyType type;

	/** keep track of how many times I've been duplicated */
	private int dupeCount = 0;

	/** keep track of interested UI classes. */
	protected List changeListeners = new Vector(1, 1);

	/**
	 * Only one <code>ChangeEvent</code> is needed per calculator instance
	 * since the event's only state is the source property.
	 */
	protected transient ChangeEvent changeEvent;

	/**
	 * Creates a new AbstractCalculator object.
	 *
	 * @param name DOCUMENT ME!
	 * @param m DOCUMENT ME!
	 * @param c DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 */
	public AbstractCalculator(String name, ObjectMapping m, VisualPropertyType type) {
		if (type == null)
			throw new NullPointerException("Type parameter for Calculator is null");

		this.type = type;
		this.name = name;
		this.addMapping(m);
		Class c = type.getDataType();

		if (!c.isAssignableFrom(m.getRangeClass()))
			throw new ClassCastException("Invalid Calculator: Expected class " + c.toString()
			                             + ", got " + m.getRangeClass().toString());
	}

	/**
	 * Add a mapping to the mappings contained by the calculator.
	 *
	 * @param m
	 *            Mapping to add.
	 */
	public void addMapping(ObjectMapping m) {
		this.mappings.add(m);
		this.acceptedDataClasses.add(m.getAcceptedDataClasses());
	}

	/**
	 * Get all mappings contained by this calculator.
	 *
	 * @return Vector of all mappings contained in this calculator
	 */
	public Vector<ObjectMapping> getMappings() {
		return mappings;
	}

	/**
	 * Get the mapping at a specific index in this calculator.
	 *
	 * @param i
	 *            index of mapping to retrieve
	 * @return ObjectMapping at index i
	 */
	public ObjectMapping getMapping(int i) {
		return mappings.get(i);
	}

	/**
	 * Get how many times this calculator has been duplicated.
	 *
	 * @return Calculator duplication count
	 */
	public int getDupeCount() {
		return dupeCount;
	}

	/**
	 * Clone the calculator. AbstractCalculator makes an independent clone of
	 * itself but DOES NOT ensure that a unique name is created. Whoever is
	 * cloning the calculator should enter the new calculator in the catalog and
	 * create an appropriate name for it if needed.
	 *
	 *
	 * @return Clone of this calculator
	 * @throws CloneNotSupportedException
	 *             if something is seriously borked.
	 */
	public Object clone() throws CloneNotSupportedException {
		final AbstractCalculator clonedCalc = (AbstractCalculator) super.clone();

		/*
		 * remove the duplication count appended to the name. This makes
		 * maintaining the duplicate naming scheme much easier by starting the
		 * valid name search at the first object every time
		 */
		String dupeFreeName;

		if (dupeCount != 0) {
			int dupeCountIndex = name.lastIndexOf(new Integer(dupeCount).toString());

			if (dupeCountIndex == -1)
				dupeFreeName = new String(name);
			else
				dupeFreeName = name.substring(0, dupeCountIndex);
		} else
			dupeFreeName = new String(name);

		clonedCalc.name = dupeFreeName;
		clonedCalc.mappings = new Vector(this.mappings.size(), 2);

		for (int i = 0; i < this.mappings.size(); i++) {
			final ObjectMapping m = this.getMapping(i);
			clonedCalc.mappings.add((ObjectMapping) m.clone());
		}

		clonedCalc.dupeCount++;

		return clonedCalc;
	}

	/**
	 * Get the name of this calculator.
	 *
	 * @return the calculator's name
	 */
	public final String toString() {
		return name;
	}

	/**
	 * Set the name of this calculator. Should only be done by the
	 * CalculatorCatalog after checking that name will not be duplicated.
	 *
	 * @param newName
	 *            the new name for this calculator. Must be unique.
	 */
	public void setName(String newName) {
		this.name = newName;
	}

	/**
	 * Returns a properties description of this calculator. Adds the keyword
	 * ".mapping" to the supplied base key and calls the getProperties method of
	 * MappingFactory with the ObjectMapping and the augmented base key.
	 */
	public Properties getProperties() {
		final String mapBaseKey = type.getPropertyLabel() + "." + toString() + ".mapping";
		final ObjectMapping m = getMapping(0);
		final Properties props = MappingFactory.getProperties(m, mapBaseKey);

		props.put(type.getPropertyLabel() + "." + toString() + ".visualPropertyType", type.toString());

		return props;
	}

	/**
	 * This exists so that it can be overridden in legacy classes
	 * (GenericNodeSizeCalculator, GenericColorCalculator,
	 * GenericArrowCalculator) such that they return the a new class name
	 * instead of a legacy class name. This will help prevent legacy classes
	 * from persisting in vizmap.props files.
	 */
	protected String getClassName() {
		return this.getClass().getName();
	}

	/**
	 * updateAttribute is called when the currently selected attribute changes.
	 * Any changes needed in the mapping UI should be performed at this point.
	 * Use {@link #updateAttribute(String, CyNetwork, int)} for best
	 * performance.
	 *
	 * @param attrName
	 *            the name of the newly selected attribute
	 * @param network
	 *            the CyNetwork on which this attribute is defined
	 * @param m
	 *            the object mapping to update
	 * @throws IllegalArgumentException
	 *             if the given object mapping isn't in this calculator.
	 */
	void updateAttribute(String attrName, CyNetwork network, ObjectMapping m)
	    throws IllegalArgumentException {
		int mapIndex = this.mappings.indexOf(m);

		if (mapIndex == -1)
			throw new IllegalArgumentException(m.getClass().getName() + " " + m.toString()
			                                   + " is not contained in calculator "
			                                   + this.toString());

		this.updateAttribute(attrName, network, mapIndex);
	}

	/**
	 * updateAttribute is called when the currently selected attribute changes.
	 * Any changes needed in the mapping UI should be performed at this point.
	 * <p>
	 * Calls the specified mapper's setControllingAttribute method.
	 *
	 * @param attrName
	 *            the name of the newly selected attribute
	 * @param network
	 *            the CyNetwork on which this attribute is defined
	 * @param mIndex
	 *            the index of the object mapping to update
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the given object mapping index is out of bounds.
	 */
	void updateAttribute(String attrName, CyNetwork network, int mIndex)
	    throws ArrayIndexOutOfBoundsException {
		ObjectMapping m = (ObjectMapping) this.mappings.get(mIndex);
		m.setControllingAttributeName(attrName, network, false);

		// fireStateChanged();
	}

	/**
	 * Add a ChangeListener to the calcaultor. When the state underlying the
	 * calculator changes, all ChangeListeners will be notified.
	 *
	 * This is used in the UI classes to ensure that the UI panes stay
	 * consistent with the data held in the mappings.
	 *
	 * @param l
	 *            ChangeListener to add
	 */
	public void addChangeListener(ChangeListener l) {
		this.changeListeners.add(l);
	}

	/**
	 * Remove a ChangeListener from the calcaultor. When the state underlying
	 * the calculator changes, all ChangeListeners will be notified.
	 *
	 * This is used in the UI classes to ensure that the UI panes stay
	 * consistent with the data held in the mappings.
	 *
	 * @param l
	 *            ChangeListener to add
	 */
	public void removeChangeListener(ChangeListener l) {
		this.changeListeners.remove(l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created.
	 *
	 * UI classes should attach themselves with a listener to the calculator to
	 * be notified about changes in the underlying data structures that require
	 * the UI classes to fetch a new copy of the UI and display it.
	 *
	 */
	protected void fireStateChanged() {
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = this.changeListeners.size() - 1; i >= 0; i--) {
			ChangeListener listener = (ChangeListener) this.changeListeners.get(i);

			// Lazily create the event:
			if (this.changeEvent == null)
				this.changeEvent = new ChangeEvent(this);

			listener.stateChanged(this.changeEvent);
		}
	}

	/**
	 * Returns a map of attribute names to single values.
	 *
	 * @param canonicalName
	 *            The attribute name returned from the CyNode or CyEdge.
	 * @return Map of the attribute names to values.
	 */
	protected Map getAttrBundle(String canonicalName, CyAttributes cyAttrs) {
		return CyAttributesUtils.getAttributes(canonicalName, cyAttrs);
	}

	protected Map getAttrBundle(String canonicalName) {
		return getAttrBundle(canonicalName,
		                     type.isNodeProp() ? Cytoscape.getNodeAttributes()
		                                       : Cytoscape.getEdgeAttributes());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param appr DOCUMENT ME!
	 * @param e DOCUMENT ME!
	 * @param net DOCUMENT ME!
	 */
	public void apply(Appearance appr, Edge e, CyNetwork net) {
		//System.out.println("AbstractCalculator.apply(edge) " + type.toString());
		Object o = getRangeValue(e);

		// default has already been set - no need to do anything
		if (o == null)
			return;

		appr.set(type, o);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param appr DOCUMENT ME!
	 * @param n DOCUMENT ME!
	 * @param net DOCUMENT ME!
	 */
	public void apply(Appearance appr, Node n, CyNetwork net) {
		//System.out.println("AbstractCalculator.apply(node) " + type.toString());
		Object o = getRangeValue(n);

		// default has already been set - no need to do anything
		if (o == null)
			return;

		appr.set(type, o);
	}

	protected Object getRangeValue(GraphObject obj) {
		if (obj == null)
			return null;

		final String nodeID = obj.getIdentifier();
		final Map attrBundle = getAttrBundle(nodeID);
		attrBundle.put(AbstractCalculator.ID, obj.getIdentifier());

		return getMapping(0).calculateRangeValue(attrBundle);
	}

	/**
	 * Replaces the following 3 methods:
	 * <ul>
	 *         <li>public byte getType();
	 *         <li>public String getTypeName();
	 *         <li>public String getPropertyLabel();
	 * </ul>
	 *
	 * The returned enum VisualPropertyType has replacement for these methods.
	 */
	public VisualPropertyType getVisualPropertyType() {
		return type;
	}

}
