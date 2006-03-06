
/*
  File: GenericEdgeFontFaceCalculator.java 
  
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
import java.awt.Font;
import javax.swing.*;

import giny.model.Edge;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.FontParser;
//--------------------------------------------------------------------------
public class GenericEdgeFontFaceCalculator extends EdgeCalculator
    implements EdgeFontFaceCalculator{
    
    public GenericEdgeFontFaceCalculator(String name, ObjectMapping m) {
	super(name, m);
	if (!(Font.class.isAssignableFrom(m.getRangeClass()))) {
	    throw new ClassCastException("Invalid Calculator: Expected class Font, got " + 
					 m.getRangeClass().toString());
	}
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericEdgeFontFaceCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new FontParser(), new Font(null, Font.PLAIN, 12));
    }

    public Font calculateEdgeFontFace(Edge edge, CyNetwork network) {
        String canonicalName = edge.getIdentifier();
        Map attrBundle = getAttrBundle(canonicalName);
	return (Font) super.getMapping().calculateRangeValue(attrBundle);
    }
}
