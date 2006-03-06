
/*
  File: GenericNodeFontSizeCalculator.java 
  
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

//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.*;

import giny.model.Node;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.DoubleParser;
//--------------------------------------------------------------------------
public class GenericNodeFontSizeCalculator extends NodeCalculator
    implements NodeFontSizeCalculator{
    
    public GenericNodeFontSizeCalculator(String name, ObjectMapping m) {
	super(name, m);
	//All we need is some kind of Number
	if (!(Number.class.isAssignableFrom(m.getRangeClass()))) {
	    throw new ClassCastException("Invalid Calculator: Expected class Number, got " + 
					 m.getRangeClass().toString());
	}
    }
    
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeFontSizeCalculator(String name, Properties props, String baseKey) {
	super(name, props, baseKey, new DoubleParser(), new Double(12));
    }

    /** 
     *  calculateNodeFontSize returns -1 if there is no mapping;
     *  since a negative number has no meaning as a font size,
     *  this is a case that the caller of calculateNodeFontSize
     *  should expect to handle.  The usual caller is
     *  NodeAppearanceCalculator.
     */
    public float calculateNodeFontSize(Node node, CyNetwork network) {
        String canonicalName = node.getIdentifier();
        Map attrBundle = getAttrBundle(canonicalName);
	Object rangeValue = super.getMapping().calculateRangeValue(attrBundle);
	if (rangeValue != null)
	    return ((Number) rangeValue).floatValue();
	else
	    return -1;
    }
}
