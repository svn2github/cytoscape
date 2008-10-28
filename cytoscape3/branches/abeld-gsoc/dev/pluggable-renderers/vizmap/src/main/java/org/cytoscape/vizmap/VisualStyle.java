/*
  File: VisualStyle.java

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
// $Revision: 12526 $
// $Date: 2007-12-11 14:14:55 -0800 (Tue, 11 Dec 2007) $
// $Author: mcreech $
//----------------------------------------------------------------------------
package org.cytoscape.vizmap;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.cytoscape.GraphPerspective;
import org.cytoscape.Node;
import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesFactory;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.VisualPropertyCatalog;
import org.cytoscape.vizmap.calculators.Calculator;


//----------------------------------------------------------------------------
/**
 * This class encapsulates a full set of visual mapping specifications for
 * Cytoscape. Currently this is implemented by holding a reference to three
 * appearance calculators, one for nodes, one for edges, and one for global
 * visual attributes.
 */
public class VisualStyle implements Cloneable {
	
	// Name of Visual Style
	private String name = "default";
	
	// Calculators associated with this VS.
	private HashMap<VisualProperty, Calculator> calculators;
	private HashMap<VisualProperty, Object> defaultValues;
	private HashMap<String, Object> globalVisualProperties;
	
	/**
	 * Keep track of number of times this style has been cloned.
	 */
	protected int dupeCount = 0;

	/**
	 * Get how many times this style has been cloned.
	 */
	public int getDupeCount() {
		return dupeCount;
	}

	/**
	 * Check if contained appearance calculators are using given calculator
	 *
	 * @param    c    calculator to check conflicts for
	 * @return    vector with: name of conflicting visual style (index 0),
	 *        name of conflicting attributes. If size == 1, then no conflicts
	 */
	public Vector checkConflictingCalculator(Calculator calc) {
		Vector<String> conflicts = new Vector<String>();
		conflicts.add(name);

		for (Calculator c : calculators.values()) {
			if (calc == c)
				conflicts.add(c.getVisualProperty().getName());
		}

		return conflicts;
	}

	/**
	 * Perform deep copy of this VisualStyle.
	 */
	public Object clone() throws CloneNotSupportedException {
		VisualStyle copy = (VisualStyle) super.clone();
		String dupeFreeName;

		if (dupeCount != 0) {
			int dupeCountIndex = name.lastIndexOf(Integer.valueOf(dupeCount).toString());

			if (dupeCountIndex == -1)
				dupeFreeName = new String(name);
			else
				dupeFreeName = name.substring(0, dupeCountIndex);
		} else
			dupeFreeName = new String(name);

		copy.name = dupeFreeName;
		copy.dupeCount++;
		copy.calculators = (HashMap <VisualProperty, Calculator>) this.calculators.clone();
		copy.defaultValues= (HashMap <VisualProperty, Object>) this.defaultValues.clone();
		copy.globalVisualProperties = (HashMap<String, Object>) this.globalVisualProperties.clone();  
		

		return copy;
	}

	/**
	 * Simple constructor, creates default node/edge/global appearance calculators.
	 */
	public VisualStyle(String name) {
		setName(name);
		calculators = new HashMap<VisualProperty, Calculator>();
		defaultValues = new HashMap <VisualProperty, Object>();
		globalVisualProperties = new HashMap<String, Object>();
	}

	/**
	 * Full constructor.
	 */
	public VisualStyle(String name, HashMap<VisualProperty, Calculator> calculators, HashMap<String, Object> globalVisualProperties) {
		setName(name);
		this.calculators = calculators;
		this.defaultValues = new HashMap <VisualProperty, Object>();
		this.globalVisualProperties = globalVisualProperties;
	}

	/**
	 * Copy constructor. Creates a default object if the argument is null.
	 * The name of this new object should be changed by calling setName
	 * with a new, unique name before adding it to a CalculatorCatalog.
	 */
	public VisualStyle(VisualStyle toCopy) {
		this(toCopy, toCopy.getName());
	}

	/**
	 * Copy constructor with new name. Creates a default object if the first
	 * argument is null, otherwise copies the members of the first argument.
	 * The name of this new VisualStyle will be equal to the second argument;
	 * the caller should ensure that this is a unique name.
	 *
	 * @throws NullPointerException if the second argument is null
	 */
    public VisualStyle(final VisualStyle toCopy, final String newName) {
        if (toCopy == null)
            return;

        if (newName == null)
            throw new NullPointerException("Unexpected null name in VisualStyle constructor");

        setName(newName);
        this.calculators = (HashMap<VisualProperty, Calculator>) toCopy.calculators.clone();
        this.defaultValues = (HashMap <VisualProperty, Object>) toCopy.defaultValues.clone();
        this.globalVisualProperties = (HashMap<String, Object>) toCopy.globalVisualProperties.clone();
    }

	/**
	 * Returns the name of this object, as returned by getName.
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Returns the name of this object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this visual style. This should be a unique name, or
	 * a collision will ocur when adding this to a CalcualtorCatalog.
	 *
	 * @param n  the new name
	 * @return   the old name
	 */
	public String setName(String n) {
		String tmp = name;
		name = n;

		return tmp;
	}

	private Object getByPass(VisualProperty vp, String graphObjectIdentifier, CyAttributes attr){
		String value = attr.getStringAttribute(graphObjectIdentifier, vp.getName());
		if (value == null){
			return null;
		} else {
			return vp.parseStringValue(value);
		}
	}

	/** Apply this VisualStyle to the view */
	public void apply(GraphView network_view){
		System.out.println("APPLYING VISUAL STYLE");
		
		// FIXME: rethink this:
		// setup proper background colors
		Color backgroundColor = (Color) globalVisualProperties.get("backgroundColor");
		if (backgroundColor== null){ // FIXME FIXME: temporary hack, shouldn't be needed!!
			System.out.println("error: having to force default backgroundColor");
			backgroundColor= Color.lightGray;
		}
		network_view.setBackgroundPaint(backgroundColor);

		// will ignore sloppy & reverse selection color for now // FIXME
		GraphPerspective network = network_view.getNetwork();
		
		// apply visual style to Nodes:
    	NodeView nodeView;

    	CyAttributes attrs = CyAttributesFactory.getCyAttributes("node");
    	List<VisualProperty> nodeVisualProperties = VisualPropertyCatalog.getNodeVisualPropertyList();
		for (Iterator i = network_view.getNodeViewsIterator(); i.hasNext();) {
			nodeView = (NodeView) i.next();

			if (nodeView == null) // FIXME:
				// WARNING: This is a hack, nodeView should not be null, but
				// for now do this! (iliana)
				continue;

			for (VisualProperty vp: nodeVisualProperties){
				Object o = getByPass(vp, nodeView.getNode().getIdentifier(), attrs);
				if (o == null) {
					Calculator c = calculators.get(vp);
					if (c!= null){
						o = c.getRangeValue(nodeView.getNode(), attrs);
					}
				}
				if (o == null) { o = defaultValues.get(vp); }
				if (o == null) { o = vp.getDefaultAppearanceObject(); }
				vp.applyToNodeView(nodeView, o);
			}
		}

    	EdgeView edgeView;

    	attrs = CyAttributesFactory.getCyAttributes("edge");
    	List<VisualProperty> edgeVisualProperties = VisualPropertyCatalog.getEdgeVisualPropertyList();
    	
		for (Iterator i = network_view.getEdgeViewsIterator(); i.hasNext();) {
			edgeView = (EdgeView) i.next();

			if (edgeView == null)
				// WARNING: This is a hack, edgeView should not be null, but
				// for now do this! (iliana)
				continue;

			for (VisualProperty vp: edgeVisualProperties ){
				Object o = getByPass(vp, edgeView.getEdge().getIdentifier(), attrs);
				if (o == null) {
					Calculator c = calculators.get(vp);
					if (c!= null){
						o = c.getRangeValue(edgeView.getEdge(), attrs);
					}
				}
				if (o == null) { o = defaultValues.get(vp); }
				if (o == null) { o = vp.getDefaultAppearanceObject(); }
				vp.applyToEdgeView(edgeView, o);
			}
		}
		
		// Set selection colors
		Color nodeSelectionColor = (Color) globalVisualProperties.get("nodeSelectionColor");
		for (Node n: network.nodesList()){ // FIXME: GraphView should have an .nodeViewsList() method but apparently doesn't have one now.
			network_view.getNodeView(n).setSelectedPaint(nodeSelectionColor );
		}
		
		Color edgeSelectionColor = (Color) globalVisualProperties.get("edgeSelectionColor");
		if (edgeSelectionColor == null){ // FIXME FIXME: temporary hack, shouldn't be needed!!
			System.out.println("error: having to force default edgeSelectionColor");
			edgeSelectionColor = Color.black;
		}
		for (EdgeView ev: network_view.getEdgeViewsList()){
			ev.setSelectedPaint(edgeSelectionColor );
		}
	}
	
	/** Adds given Calculator*/
	public void setCalculator(Calculator c){
		calculators.put(c.getVisualProperty(), c);
	
	}
	public Calculator getCalculator(VisualProperty vp){
		return calculators.get(vp);
	}
	public void removeCalculator(VisualProperty vp){
		calculators.remove(vp);
	}
	/** need a way to iterate over the calculators */
	public HashMap<VisualProperty, Calculator> getCalculators(){
		return calculators;
	}

	/** need a way to iterate over the calculators */
	public List<Calculator> getNodeCalculators(){
		List<Calculator> result = new ArrayList<Calculator>();
		for (Calculator calc: calculators.values()){
			if (calc.getVisualProperty().isNodeProp()){
				result.add(calc);
			}
		}
		return result;
	}

	public List<Calculator> getEdgeCalculators(){
		List<Calculator> result = new ArrayList<Calculator>();
		for (Calculator calc: calculators.values()){
			if (!calc.getVisualProperty().isNodeProp()){
				result.add(calc);
			}
		}
		return result;
	}

	/** Sets the default value for a given VisualProperty */
	public void setGlobalProperty(String key, Object value){
		globalVisualProperties.put(key, value);
	}
	public Object getGlobalProperty(String key){
		return globalVisualProperties.get(key);
	}
	public HashMap<String, Object> copyGlobalVisualProperties(){
		return (HashMap<String, Object>) globalVisualProperties.clone();
	}

	/** Sets the default value for a given VisualProperty */
	public void setDefaultValue(VisualProperty vp, Object value){
		defaultValues.put(vp, value);
	}
	public Object getDefaultValue(VisualProperty vp){
		Object o = defaultValues.get(vp);
		if (o == null){
			o = vp.getDefaultAppearanceObject();
		}
		return o;
	}
}
