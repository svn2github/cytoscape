/*
 File: NodeAppearance.java

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
package cytoscape.visual;

import static cytoscape.visual.VisualPropertyType.NODE_BORDER_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_FILL_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_FONT_FACE;
import static cytoscape.visual.VisualPropertyType.NODE_FONT_SIZE;
import static cytoscape.visual.VisualPropertyType.NODE_HEIGHT;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL_POSITION;
import static cytoscape.visual.VisualPropertyType.NODE_LINETYPE;
import static cytoscape.visual.VisualPropertyType.NODE_OPACITY;
import static cytoscape.visual.VisualPropertyType.NODE_SHAPE;
import static cytoscape.visual.VisualPropertyType.NODE_SIZE;
import static cytoscape.visual.VisualPropertyType.NODE_TOOLTIP;
import static cytoscape.visual.VisualPropertyType.NODE_WIDTH;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;


/**
 * Objects of this class hold data describing the appearance of a Node.
 * @deprecated Use Appearance instead. Will be removed 5/2008.
 */
@Deprecated
public class NodeAppearance extends Appearance {

	private boolean nodeSizeLocked = true;

	public NodeAppearance() {
		super();
	}

	public void applyAppearance(final NodeView nodeView) {
		for ( VisualPropertyType type : VisualPropertyType.values() )
			if ( type == NODE_SIZE ) {
				if ( nodeSizeLocked ) 
					type.getVisualProperty().applyToNodeView(nodeView,vizProps[type.ordinal()]);
				else
					continue;
			} else if ( type == NODE_WIDTH || type == NODE_HEIGHT ) {
				if ( nodeSizeLocked ) 
					continue;
				else
					type.getVisualProperty().applyToNodeView(nodeView,vizProps[type.ordinal()]);
			} else
				type.getVisualProperty().applyToNodeView(nodeView,vizProps[type.ordinal()]);
	}

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public Color getFillColor() {
        return (Color)(super.get(NODE_FILL_COLOR));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setFillColor(Color c) {
		set(NODE_FILL_COLOR,c);
    }
    
//    /**
//     * Use Appearance.get(VisualPropertyType) instead.
//     *
//     */
//    public Integer getFillOpacity() {
//        return (Integer)(super.get(NODE_OPACITY));
//    }
//
//    /**
//     * Use Appearance.set(VisualPropertyType,Object) instead.
//     *
//     */
//    public void setFillOpacity(Integer i) {
//		set(NODE_OPACITY,i);
//    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public Color getBorderColor() {
        return (Color)(get(NODE_BORDER_COLOR));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setBorderColor(Color c) {
		set(NODE_BORDER_COLOR,c);
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public LineType getBorderLineType() {
       	return (LineType)(get(NODE_LINETYPE));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setBorderLineType(LineType lt) {
		set(NODE_LINETYPE,lt);
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public byte getShape() {
		return (byte)(((NodeShape)(get(NODE_SHAPE))).ordinal());
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public NodeShape getNodeShape() {
       	return (NodeShape)(get(NODE_SHAPE));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setShape(byte s) {
		set(NODE_SHAPE,VisualPropertyType.getVisualPorpertyType(s));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setNodeShape(NodeShape s) {
		set(NODE_SHAPE,s);
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public double getWidth() {
        if (nodeSizeLocked)
            return ((Double)(get(NODE_SIZE))).doubleValue();
        else
            return ((Double)(get(NODE_WIDTH))).doubleValue();
    }

    /**
     * Sets only the height variable.
     */
    public void setJustWidth(double d) {
		set(NODE_WIDTH,new Double(d));
    }

    /**
     * Sets the width variable, but also the size variable if the node size is
     * locked. This is to support deprecated code that used setting width/height
     * for setting uniform size as well.
     */
    public void setWidth(double d) {
		set(NODE_WIDTH,new Double(d));

        if (nodeSizeLocked)
			set(NODE_SIZE,new Double(d));
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public double getHeight() {
        if (nodeSizeLocked)
            return ((Double)(get(NODE_SIZE))).doubleValue();
        else
            return ((Double)(get(NODE_HEIGHT))).doubleValue();
    }

    /**
     * Sets only the height variable.
     */
    public void setJustHeight(double d) {
		set(NODE_HEIGHT,new Double(d));
    }

    /**
     * Sets the height variable, but also the size variable if the node size is
     * locked. This is to support deprecated code that used setting width/height
     * for setting uniform size as well.
     */
    public void setHeight(double d) {
		set(NODE_HEIGHT,new Double(d));

        if (nodeSizeLocked)
			set(NODE_SIZE,new Double(d));
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public double getSize() {
        return ((Double)(get(NODE_SIZE))).doubleValue();
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setSize(double s) {
		set(NODE_SIZE,new Double(s));
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public String getLabel() {
        return (String)(get(NODE_LABEL));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setLabel(String s) {
		set(NODE_LABEL,s);
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public String getToolTip() {
        return (String)(get(NODE_TOOLTIP));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setToolTip(String s) {
		set(NODE_TOOLTIP,s);
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public Font getFont() {
        return (Font)(get(NODE_FONT_FACE));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setFont(Font f) {
		set(NODE_FONT_FACE,f);
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public float getFontSize() {
        return ((Number)(get(NODE_FONT_SIZE))).floatValue();
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setFontSize(float f) {
		set(NODE_FONT_SIZE,new Float(f));
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public Color getLabelColor() {
        return (Color)(get(NODE_LABEL_COLOR));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setLabelColor(Color c) {
		set(NODE_LABEL_COLOR,c);
    }

    /**
     * Use Appearance.get(VisualPropertyType) instead.
     *
     */
    public LabelPosition getLabelPosition() {
        return (LabelPosition)(get(NODE_LABEL_POSITION));
    }

    /**
     * Use Appearance.set(VisualPropertyType,Object) instead.
     *
     */
    public void setLabelPosition(LabelPosition c) {
		set(NODE_LABEL_POSITION,c);
    }

	public void copy(NodeAppearance na) {
		final boolean actualLockState = na.getNodeSizeLocked();

		// set everything to false so that it copy
		// correctly
		setNodeSizeLocked(false);
		na.setNodeSizeLocked(false);

		super.copy((Appearance)na);

		// now set the lock state correctly
		setNodeSizeLocked(actualLockState);
		na.setNodeSizeLocked(actualLockState);
	}

    public Object clone() {
		NodeAppearance ga = new NodeAppearance();
		ga.copy(this);
		return ga;
	}


    public Object get(byte b) {
	        return get(VisualPropertyType.getVisualPorpertyType(b));
    }

	public void set(byte b, Object o) {
		set(VisualPropertyType.getVisualPorpertyType(b),o);
	}

    public boolean getNodeSizeLocked() {
		return nodeSizeLocked;
	}

	public void setNodeSizeLocked(boolean b) {
		nodeSizeLocked = b;
	}
}
