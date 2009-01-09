/*
 File: GenericNodeColorCalculator.java

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
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import giny.model.Node;

import java.awt.Color;

import java.util.Properties;


/**
 * Doubly Deprecated! Use NodeFillColor or NodeBorderColor instead. 
 * @deprecated Use BasicCalculator(VisualPropertyType,...) instead. Will be removed 5/2008.
 * Will be hidden, although probably not removed, in 5/2008.
 */
@Deprecated
public class GenericNodeColorCalculator extends AbstractNodeColorCalculator
    implements NodeColorCalculator {

    protected String getClassName() {
        if (type == VisualPropertyType.NODE_FILL_COLOR)
            return "cytoscape.visual.calculators.GenericNodeFillColorCalculator";

        if (type == VisualPropertyType.NODE_BORDER_COLOR)
            return "cytoscape.visual.calculators.GenericNodeBorderColorCalculator";

        return getClass().getName();
    }

    /**
     * Creates a new GenericNodeColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    @Deprecated
    public GenericNodeColorCalculator(String name, ObjectMapping m) {
        this(name, m, VisualPropertyType.NODE_FILL_COLOR);
    }

    /**
     * Creates a new GenericNodeColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericNodeColorCalculator(String name, ObjectMapping m, VisualPropertyType type) {
        super(name, m, type);
    }

    /**
     * Creates a new GenericNodeColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    @Deprecated
    public GenericNodeColorCalculator(String name, Properties props, String baseKey) {
        this(name, props, baseKey, VisualPropertyType.NODE_FILL_COLOR);
    }

    /**
     * Creates a new GenericNodeColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericNodeColorCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, type);
    }
}
