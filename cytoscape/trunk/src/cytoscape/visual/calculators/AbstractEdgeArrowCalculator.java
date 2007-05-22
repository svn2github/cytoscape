/*
 File: AbstractEdgeArrowCalculator.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute of Systems Biology
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
// $Revision: 8522 $
// $Date: 2006-10-19 18:15:21 -0700 (Thu, 19 Oct 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Arrow;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ArrowParser;

//----------------------------------------------------------------------------
import giny.model.Edge;

import java.util.Properties;


//----------------------------------------------------------------------------
@Deprecated
abstract class AbstractEdgeArrowCalculator extends EdgeCalculator {

    /**
     * @deprecated This only exists to support deprecated code. DO NOT USE!!!
     *             will be removed 10/2007
     */
    public void set(VisualPropertyType t) {
		type = t;
    }

    /**
     * Creates a new AbstractEdgeArrowCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractEdgeArrowCalculator(String name, ObjectMapping m, VisualPropertyType type) {
        super(name, m, Arrow.class, type);
    }

    /**
     * Creates a new AbstractEdgeArrowCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractEdgeArrowCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, new ArrowParser(), Arrow.NONE, type);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param edge DOCUMENT ME!
     * @param network DOCUMENT ME!
    public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
        Arrow a = (Arrow) getRangeValue(edge);

        // default has already been set - no need to do anything
        if (a == null)
            return;

        if (type == VisualPropertyType.EDGE_SRCARROW)
            appr.setSourceArrow(a);
        else if (type == VisualPropertyType.EDGE_TGTARROW)
            appr.setTargetArrow(a);
    }
     */

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Arrow calculateEdgeArrow(Edge e, CyNetwork n) {
        final EdgeAppearance ea = new EdgeAppearance();
        apply(ea, e, n);

		return (Arrow)(ea.get(type));
    }
}
