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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.ChangeListener;

import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.ShapePaletteInfo;
import cytoscape.editor.ShapePaletteInfoFilter;
import cytoscape.editor.ShapePaletteInfoGenerator;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;


/**
 * Implementation of ShapePaletteInfoGenerator.
 */
public class ShapePaletteInfoGeneratorImpl implements ShapePaletteInfoGenerator {
	protected ShapePaletteInfoGeneratorImpl() {
	}

	/**
	 * {@inheritDoc}
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

		if (dm == null) {
			return new HashSet<String>(0);
		}

		Map<String, Object> keyValuePairs = (Map<String, Object>) dm.getAll();

		// don't know if the map can ever be null, but check anyway:
		if (keyValuePairs == null) {
			return new HashSet<String>(0);
		}

		return keyValuePairs.keySet();
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
