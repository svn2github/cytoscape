/*
 File: AbstractNodeColorCalculator.java

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
// $Revision: 8522 $
// $Date: 2006-10-19 18:15:21 -0700 (Thu, 19 Oct 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.NodeAppearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ColorParser;

//----------------------------------------------------------------------------
import giny.model.Node;

import java.awt.Color;

import java.util.Properties;


//----------------------------------------------------------------------------
abstract class AbstractNodeColorCalculator extends NodeCalculator {
    /**
     * @deprecated This only exists to support deprecated code. DO NOT USE!!!
     *             will be removed 10/2007
     */
    protected byte colType;

    /**
     * @deprecated This only exists to support deprecated code. DO NOT USE!!!
     *             will be removed 10/2007
     */
    protected String propertyLabel;

    /**
     * @deprecated This only exists to support deprecated code. DO NOT USE!!!
     *             will be removed 10/2007
     */
    protected String typename;

    /**
     * @deprecated This only exists to support deprecated code. DO NOT USE!!!
     *             will be removed 10/2007
     */
    public void set(byte b, String p, String n) {
        colType = b;
        propertyLabel = p;
        typename = n;
    }

    /**
     * No longer necessary since enum VisualPropertyType contains sorce/target
     * informaiton.
     */
    @Deprecated
    protected static final byte FILL = 1;

    /**
     * No longer necessary since enum VisualPropertyType contains sorce/target
     * informaiton.
     */
    @Deprecated
    protected static final byte BORDER = 2;

    /**
     *
     * Use public AbstractNodeColorCalculator(String name, ObjectMapping m,
     * VisualPropertyType type) instead.
     *
     * @param name
     * @param m
     */
    @Deprecated
    public AbstractNodeColorCalculator(String name, ObjectMapping m) {
        super(name, m, Color.class, null);
    }

    /**
     * Creates a new AbstractNodeColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractNodeColorCalculator(String name, ObjectMapping m,
        VisualPropertyType type) {
        super(name, m, Color.class, type);
    }

    /**
     * Use public AbstractNodeColorCalculator(String name, Properties props,
     * String baseKey, VisualPropertyType type) instead.
     *
     * @param name
     * @param props
     * @param baseKey
     */
    @Deprecated
    public AbstractNodeColorCalculator(String name, Properties props,
        String baseKey) {
        this(name, props, baseKey, null);
    }

    /**
     * Creates a new AbstractNodeColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractNodeColorCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, new ColorParser(), Color.WHITE, type);
    }

    /**
     * Use public void apply(NodeAppearance appr, Node node, CyNetwork network)
     * instead.<br>
     * byte type is no longer necessary.
     *
     * @param appr
     * @param node
     * @param network
     * @param type
     */
    @Deprecated
    protected void apply(NodeAppearance appr, Node node, CyNetwork network,
        byte type) {
        apply(appr, node, network);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    public void apply(NodeAppearance appr, Node node, CyNetwork network) {
        if (node == null)
            return;

        final Color c = (Color) getRangeValue(node);

        // default has already been set - no need to do anything
        if (c == null)
            return;

        if (type == VisualPropertyType.NODE_FILL_COLOR)
            appr.setFillColor(c);
        else if (type == VisualPropertyType.NODE_BORDER_COLOR)
            appr.setBorderColor(c);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color calculateNodeColor(Node e, CyNetwork n) {
        final NodeAppearance ea = new NodeAppearance();
        apply(ea, e, n);

        if (type == VisualPropertyType.NODE_FILL_COLOR)
            return ea.getFillColor();
        else
            return ea.getBorderColor();
    }
}
