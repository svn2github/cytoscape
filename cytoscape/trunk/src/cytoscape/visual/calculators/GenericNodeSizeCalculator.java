/*
 File: GenericNodeSizeCalculator.java

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

import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

//----------------------------------------------------------------------------
import giny.model.Node;

import java.util.Properties;


/**
 * @deprecated Use NodeWidth,NodeHeight, or NodeUniformSize instead. will be
 *             removed 10/2007
 */
public class GenericNodeSizeCalculator extends AbstractNodeSizeCalculator
    implements NodeSizeCalculator {

    protected String getClassName() {
        if (type == VisualPropertyType.NODE_SIZE)
            return "cytoscape.visual.calculators.GenericNodeUniformSizeCalculator";

        if (type == VisualPropertyType.NODE_WIDTH)
            return "cytoscape.visual.calculators.GenericNodeWidthCalculator";

        if (type == VisualPropertyType.NODE_HEIGHT)
            return "cytoscape.visual.calculators.GenericNodeHeightCalculator";

        return getClass().getName();
    }

    /**
     * Creates a new GenericNodeSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    @Deprecated
    public GenericNodeSizeCalculator(String name, ObjectMapping m) {
        this(name, m, VisualPropertyType.NODE_SIZE);
    }

    /**
     * Creates a new GenericNodeSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericNodeSizeCalculator(String name, ObjectMapping m, VisualPropertyType type) {
        super(name, m, type);
    }

    /**
     * Creates a new GenericNodeSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    @Deprecated
    public GenericNodeSizeCalculator(String name, Properties props, String baseKey) {
        this(name, props, baseKey, VisualPropertyType.NODE_SIZE);
    }

    /**
     * Creates a new GenericNodeSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericNodeSizeCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, type);
    }
}
