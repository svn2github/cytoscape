/*
 File: GenericNodeLineTypeCalculator.java

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
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.LineType;
import cytoscape.visual.Appearance;

import static cytoscape.visual.VisualPropertyType.NODE_LINETYPE;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineTypeParser;

import giny.model.Node;

import java.util.Properties;

/**
 * @deprecated Use BasicCalculator(VisualPropertyType,...) instead. 
 * Will be hidden, although probably not removed, in 5/2008.
  */
@Deprecated
public class GenericNodeLineTypeCalculator extends NodeCalculator
    implements NodeLineTypeCalculator {
    /**
     * Creates a new GenericNodeLineTypeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericNodeLineTypeCalculator(String name, ObjectMapping m) {
        super(name, m, LineType.class, NODE_LINETYPE);
    }

    /**
     * Creates a new GenericNodeLineTypeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericNodeLineTypeCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new LineTypeParser(), LineType.LINE_1, NODE_LINETYPE);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * 
     * @deprecated Will be removed 5/2008
     */
    @Deprecated
    public LineType calculateNodeLineType(Node e, CyNetwork n) {
        final Appearance ea = new Appearance();
        apply(ea, e, n);

        return (LineType)ea.get(type);
    }
}
