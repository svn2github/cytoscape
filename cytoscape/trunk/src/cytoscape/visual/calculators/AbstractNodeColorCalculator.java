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

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ColorParser;

import cytoscape.visual.ui.VizMapUI;

import giny.model.Node;

import java.awt.Color;

//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;

import javax.swing.JPanel;


//----------------------------------------------------------------------------
abstract class AbstractNodeColorCalculator extends NodeCalculator {
	/** @deprecated This only exists to support deprecated code. DO NOT USE!!!
	    will be removed 10/2007 */
	protected byte colType;

	/** @deprecated This only exists to support deprecated code. DO NOT USE!!!
	    will be removed 10/2007 */
	protected String propertyLabel;

	/** @deprecated This only exists to support deprecated code. DO NOT USE!!!
	    will be removed 10/2007 */
	protected String typename;

	/** @deprecated This only exists to support deprecated code. DO NOT USE!!!
	    will be removed 10/2007 */
	public void set(byte b, String p, String n) {
		colType = b;
		propertyLabel = p;
		typename = n;
	}

	protected byte FILL = 1;
	protected byte BORDER = 2;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public abstract byte getType();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public abstract String getPropertyLabel();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public abstract String getTypeName();

	AbstractNodeColorCalculator() {
		super();
	}

	/**
	 * Creates a new AbstractNodeColorCalculator object.
	 *
	 * @param name  DOCUMENT ME!
	 * @param m  DOCUMENT ME!
	 */
	public AbstractNodeColorCalculator(String name, ObjectMapping m) {
		super(name, m, Color.class);
	}

	/**
	 * Creates a new AbstractNodeColorCalculator object.
	 *
	 * @param name  DOCUMENT ME!
	 * @param props  DOCUMENT ME!
	 * @param baseKey  DOCUMENT ME!
	 */
	public AbstractNodeColorCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey, new ColorParser(), Color.WHITE);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param appr DOCUMENT ME!
	 * @param node DOCUMENT ME!
	 * @param network DOCUMENT ME!
	 */
	public abstract void apply(NodeAppearance appr, Node node, CyNetwork network);

	protected void apply(NodeAppearance appr, Node node, CyNetwork network, byte type) {
		Color c = (Color) getRangeValue(node);

		// default has already been set - no need to do anything
		if (c == null)
			return;

		if (type == FILL)
			appr.setFillColor(c);

		if (type == BORDER)
			appr.setBorderColor(c);
	}
}
