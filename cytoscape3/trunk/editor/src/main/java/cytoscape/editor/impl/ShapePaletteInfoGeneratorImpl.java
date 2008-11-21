/* -*-Java-*-
********************************************************************************
*
* File:         ShapePaletteInfoGeneratorImpl.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Sun Dec 03 19:19:49 2006
* Modified:     Fri May 11 16:56:57 2007 (Michael L. Creech) creech@w235krbza760
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
* Fri May 11 16:56:45 2007 (Michael L. Creech) creech@w235krbza760
*  Updated to Cytoscape 2.5 VizMap API.
********************************************************************************
*/
package cytoscape.editor.impl;

import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.ShapePaletteInfo;
import cytoscape.editor.ShapePaletteInfoFilter;
import cytoscape.editor.ShapePaletteInfoGenerator;
import org.cytoscape.vizmap.EdgeAppearanceCalculator;
import org.cytoscape.vizmap.NodeAppearanceCalculator;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;

import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * Implementation of ShapePaletteInfoGenerator.
 */
public class ShapePaletteInfoGeneratorImpl implements ShapePaletteInfoGenerator {
	protected ShapePaletteInfoGeneratorImpl() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param appearanceCalc DOCUMENT ME!
	 * @param calcsToUse DOCUMENT ME!
	 * @param controllingAttribute DOCUMENT ME!
	 * @param listener DOCUMENT ME!
	 * @param filter DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Iterator<ShapePaletteInfo> buildShapePaletteInfo(Object appearanceCalc,
	                                                        VisualPropertyType [] calcsToUse,
	                                                        String controllingAttribute,
	                                                        ChangeListener listener,
	                                                        ShapePaletteInfoFilter filter) {
		// setup listening for changes when needed:
		if (listener != null) {
		    for (VisualPropertyType calcType : calcsToUse) {
				DiscreteMapping dm = getDiscreteMapping(getCalculator(appearanceCalc, calcType),
				                                        controllingAttribute);
				if (dm != null) {
					dm.removeChangeListener(listener);
					dm.addChangeListener(listener);
				}
			}
		}

		// get union of all DiscreteMapping keys:
		List<ShapePaletteInfo> spInfos = new ArrayList<ShapePaletteInfo>();
		Set<String> mappingKeys = computeAllMappingKeys(appearanceCalc, calcsToUse,
		                                                controllingAttribute);

		for (String key : mappingKeys) {
			ShapePaletteInfo pi = CytoscapeEditorFactory.INSTANCE.createShapePaletteInfo(controllingAttribute,
			                                                                             key);

			for (VisualPropertyType calcType : calcsToUse) {
				Object value = null;
				DiscreteMapping dm = getDiscreteMapping(getCalculator(appearanceCalc, calcType),
				                                        controllingAttribute);

				if (dm != null) {
					value = dm.getMapValue(key);
				}

				if (value == null) {
					value = getDefaultAppearanceValue(appearanceCalc, calcType);
				}

				pi.add(calcType, value);
			}

			if ((filter == null) || (filter.useEntry(pi))) {
				spInfos.add(pi);
			}
		}

		return spInfos.iterator();
	}

	
	

	/**
	 * Across a set of given calculators types, create the set
	 * of all the unique discrete mapping keys from
	 * calculators with the given type from the given Node or Edge
	 * AppearanceCalculator.
	 * @return a non-null Set of the String mapping keys
	 */
	private Set<String> computeAllMappingKeys(Object appearanceCalc, VisualPropertyType[] calculatorTypes,
	                                          String controllingAttribute) {
		Set<String> mappingKeys = new HashSet<String>();

		for (VisualPropertyType calcType : calculatorTypes) {
			mappingKeys.addAll(getMappingKeys(getCalculator(appearanceCalc, calcType),
			                                  controllingAttribute));
		}

		return mappingKeys;
	}

	private Calculator getCalculator(Object appearanceCalc, VisualPropertyType calcType) {
		// The Cytoscape API should be changed to have an AppearanceCalculator that
		// underlies NodeAppearanceCalculator and EdgeAppearanceCalculator. For now,
		// check type:
		if (appearanceCalc instanceof NodeAppearanceCalculator) {
			return ((NodeAppearanceCalculator) appearanceCalc).getCalculator(calcType);
		} else if (appearanceCalc instanceof EdgeAppearanceCalculator) {
			return ((EdgeAppearanceCalculator) appearanceCalc).getCalculator(calcType);
		}

		return null;
	}

	private Object getDefaultAppearanceValue(Object appearanceCalc, VisualPropertyType calcType) {
		// The Cytoscape API should be changed to have an AppearanceCalculator that
		// underlies NodeAppearanceCalculator and EdgeAppearanceCalculator. For now,
		// check type:
		if (appearanceCalc instanceof NodeAppearanceCalculator) {
			return ((NodeAppearanceCalculator) appearanceCalc).getDefaultAppearance().get(calcType);
		} else if (appearanceCalc instanceof EdgeAppearanceCalculator) {
			return ((EdgeAppearanceCalculator) appearanceCalc).getDefaultAppearance().get(calcType);
		}

		return null;
	}
    // MLC 05/09/07 END.

	private Set<String> getMappingKeys(Calculator calc, String controllingAttribute) {
		DiscreteMapping dm = getDiscreteMapping(calc, controllingAttribute);

		Set<String> keys = new HashSet<String>();

		if (dm == null) {
			return keys; 
		}

		Map<Object, Object> keyValuePairs = (Map<Object, Object>) dm.getAll();

		if (keyValuePairs == null) {
			return keys;
		}

		for ( Object k : keyValuePairs.keySet() ) {
			keys.add( k.toString() );
		}

		return keys; 
	}

	private DiscreteMapping getDiscreteMapping(Calculator calc, String controllingAttribute) {
		if (calc != null) {
			// Vector edgeMappings = calc.getMappings();
			// for (int i = 0; i < edgeMappings.size(); i++) {
			for (ObjectMapping possibleMatch : (Vector<ObjectMapping>) calc.getMappings()) {
				if ((possibleMatch instanceof DiscreteMapping)
				    && controllingAttribute.equals(((DiscreteMapping) possibleMatch)
				                                                                                                                                                                          .getControllingAttributeName())) {
					return (DiscreteMapping) possibleMatch;
				}
			}
		}

		return null;
	}
}
