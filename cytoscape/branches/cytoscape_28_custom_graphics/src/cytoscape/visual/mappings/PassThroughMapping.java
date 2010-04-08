/*
  File: PassThroughMapping.java

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

package cytoscape.visual.mappings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Map;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.border.DropShadowBorder;

import cytoscape.Cytoscape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.parsers.ValueParser;

/**
 * Defines a mapping from a bundle of data attributes to a visual attribute. The
 * returned value is simply the value of one of the data attributes, defined by
 * the controlling attribute name. This value is type-checked against the
 * expected range class; null is returned instead if the data value is of the
 * wrong type.
 */
public class PassThroughMapping extends AbstractMapping {

	// Legend UI theme
	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 14);
	private static final Color TITLE_COLOR = new Color(10, 200, 255);

	private RangeValueCalculator<?> rangeValueCalculator;

	private final Class<?>[] ACCEPTED_CLASS = { Object.class };

	/**
	 * Standard constructor for compatibility with new calculator creation in
	 * the UI.
	 * 
	 * @param defaultObj
	 *            Default object - provided only to establish mapping's range
	 *            class.
	 * @param mapType
	 *            unused.
	 * 
	 *            Will be removed in the next release (2.8). Map Type is not in
	 *            use.
	 */
	@Deprecated
	public PassThroughMapping(final Object defaultObj, final byte mapType) {
		this(defaultObj.getClass(), null);
	}

	/**
	 * Creates a new PassThroughMapping object.
	 * 
	 * @param defaultObj
	 *            DOCUMENT ME!
	 */
	@Deprecated
	public PassThroughMapping(final Object defaultObj) {
		this(defaultObj, null);	
	}
	
	
	@Deprecated
	public PassThroughMapping(Object defaultObj, String attrName) {
		this(defaultObj.getClass(), attrName);
	}

	/**
	 * Creates a new PassThroughMapping object.
	 * 
	 * @param defaultObj
	 *            DOCUMENT ME!
	 * @param attrName
	 *            DOCUMENT ME!
	 */
	public PassThroughMapping(final Class<?> rangeClass, final String attrName) {
		super(rangeClass, attrName);
		this.acceptedClasses = ACCEPTED_CLASS;
		System.out.println("@@@@@ PTh Mapping created: " + attrName);
	}

	/**
	 * Create clone of this instance.
	 * 
	 * @return DOCUMENT ME!
	 */
	public Object clone() {
		final PassThroughMapping copy = new PassThroughMapping(this.rangeClass, this.controllingAttrName);

		copy.controllingAttrName = new String(this.controllingAttrName);

		// don't need to explicitly clone rangeClass since cloned calculator
		// has same type as original.
		return copy;
	}


	/**
	 * Empty implementation because PassThroughMapping has no UI.
	 */
	public void addChangeListener(ChangeListener l) {
	}

	/**
	 * Empty implementation because PassThroughMapping has no UI.
	 */
	public void removeChangeListener(ChangeListener l) {
	}
	

	/**
	 * DOCUMENT ME!
	 * 
	 * @param attrBundle
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Object calculateRangeValue(final Map<String, Object> attrBundle) {
		if (attrBundle == null || controllingAttrName == null)
			return null;

		// extract the data value for our controlling attribute
		final Object attrValue = attrBundle.get(controllingAttrName);

		if (attrValue == null)
			return null;
		
		if(rangeValueCalculator == null)
			rangeValueCalculator = Cytoscape.getVisualMappingManager().
				getRangeValueCalculatorFactory().getRangeValueCalculator(rangeClass);
		if(rangeValueCalculator != null)
			return rangeValueCalculator.getRange(attrValue);
		return null;
	}

	/**
	 * Customize this object by applying mapping defintions described by the
	 * supplied Properties argument.
	 */
	public void applyProperties(Properties props, String baseKey,
			ValueParser<?> parser) {
		String contKey = baseKey + ".controller";
		String contValue = props.getProperty(contKey);

		if (contValue != null)
			setControllingAttributeName(contValue);
	}

	/**
	 * Return a Properties object with entries suitable for customizing this
	 * object via the applyProperties method.
	 */
	public Properties getProperties(String baseKey) {
		Properties newProps = new Properties();
		String contKey = baseKey + ".controller";
		String contValue = controllingAttrName;

		if ((contKey != null) && (contValue != null))
			newProps.setProperty(contKey, contValue);

		return newProps;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param vpt
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public JPanel getLegend(VisualPropertyType vpt) {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());

		JLabel title = new JLabel(vpt.getName() + " is displayed as "
				+ controllingAttrName);
		title.setFont(TITLE_FONT);
		title.setForeground(TITLE_COLOR);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setVerticalAlignment(SwingConstants.CENTER);
		title.setHorizontalTextPosition(SwingConstants.CENTER);
		title.setVerticalTextPosition(SwingConstants.CENTER);
		title.setPreferredSize(new Dimension(200, 50));
		title.setBorder(new DropShadowBorder());
		p.setBackground(Color.white);
		p.add(title, SwingConstants.CENTER);

		return p;
	}
}
