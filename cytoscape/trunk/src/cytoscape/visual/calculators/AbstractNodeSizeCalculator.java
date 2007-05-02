/*
 File: AbstractNodeSizeCalculator.java

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
// $Revision: 8550 $
// $Date: 2006-10-23 13:04:30 -0700 (Mon, 23 Oct 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.NodeAppearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.DoubleParser;

//----------------------------------------------------------------------------
import giny.model.Node;

import java.util.Properties;


//----------------------------------------------------------------------------
abstract class AbstractNodeSizeCalculator extends NodeCalculator {

	/**
	 * DO NOT USE THIS METHOD (unless you're an appearance calculator)!!!
     * @deprecated This only exists to support deprecated code. DO NOT USE!!!
     *             will be removed 10/2007
	 */
	public void set(VisualPropertyType t) {
		type = t;
	}

    /**
     * Creates a new AbstractNodeSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractNodeSizeCalculator(String name, ObjectMapping m,
        VisualPropertyType type) {
        super(name, m, Number.class, type);
    }

    /**
     * Creates a new AbstractNodeSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractNodeSizeCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, new DoubleParser(), new Double(0), type);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    public void apply(NodeAppearance appr, Node node, CyNetwork network) {
        final Object rangeValue = getRangeValue(node);

        // If null, don't set anything - the existing value in appr is already
        // the default.
        if (rangeValue == null)
            return;

        double ret = ((Number) rangeValue).doubleValue();

        if (type == VisualPropertyType.NODE_WIDTH)
            appr.setJustWidth(ret);

        if (type == VisualPropertyType.NODE_HEIGHT)
            appr.setJustHeight(ret);

        if (type == VisualPropertyType.NODE_SIZE)
            appr.setSize(ret);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public double calculateNodeSize(Node e, CyNetwork n) {
        final NodeAppearance ea = new NodeAppearance();
        apply(ea, e, n);

        if (type == VisualPropertyType.NODE_WIDTH)
            return ea.getWidth();
        else if (type == VisualPropertyType.NODE_HEIGHT)
            return ea.getHeight();
        else
            return ea.getSize();
    }
}
