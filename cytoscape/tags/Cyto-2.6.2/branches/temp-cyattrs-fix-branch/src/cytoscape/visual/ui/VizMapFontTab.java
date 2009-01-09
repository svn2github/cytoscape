// VizMapFontTab.java
//--------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------
package cytoscape.visual.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import java.util.*;

/**
 * Create a tab for the Set Visual Properties dialog for Node and Edge Font.
 * Because font size and face are mapped separately, the UI should integrate the
 * UI of the font size and font face calculators to present a unified UI for
 * these closely related attributes.
 */
public class VizMapFontTab extends VizMapTab {
    private VizMapAttrTab faceTab, sizeTab;

    /**
     *	create a new tab for font face and size. Retrieve current
     *	calculator and default settings from the VMM.
     *
     *	@param	VMM	VisualMappingManager for the window
     *  @param	tabContainer	The containing JTabbedPane
     *  @param	tabIndex	index of this tab in tabContainer
     *	@param	n	Underlying network
     *	@param	type	Must be {@link VizMapUI#NODE_LABEL_FONT} or
     *                  {@link VizMapUI#EDGE_LABEL_FONT}
     *
     *  @throws IllegalArgumentException if type is not {@link VizMapUI#NODE_LABEL_FONT} or {@link VizMapUI#EDGE_LABEL_FONT}
     */
    public VizMapFontTab (VizMapUI mainUI, JTabbedPane tabContainer, int tabIndex, VisualMappingManager VMM, byte type) throws IllegalArgumentException {
	super(false);
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
	//set the name of this component appropriately
	switch(type) {
	case VizMapUI.NODE_LABEL_FONT:
	    setName("Node Font");
	    this.faceTab = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.NODE_FONT_FACE);
	    this.sizeTab = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.NODE_FONT_SIZE);
	    break;
	case VizMapUI.EDGE_LABEL_FONT:
	    setName("Edge Font");
	    this.faceTab = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.EDGE_FONT_FACE);
	    this.sizeTab = new VizMapAttrTab(mainUI, tabContainer, tabIndex, VMM, VizMapUI.EDGE_FONT_SIZE);
	    break;
	default:
	    throw new IllegalArgumentException("You can only create a VizMapFontTab for the Node/Edge Font attribute, called with " + type);
	}

	this.faceTab.setBorder(BorderFactory.createTitledBorder("Font Face"));
	this.sizeTab.setBorder(BorderFactory.createTitledBorder("Font Size"));

	this.add(this.faceTab);
	this.add(this.sizeTab);
    }

    public void refreshUI() {
	this.faceTab.refreshUI();
	this.sizeTab.refreshUI();
	validate();
    }

    public void visualStyleChanged() {
	this.faceTab.visualStyleChanged();
	this.sizeTab.visualStyleChanged();
    }

    VizMapTab checkCalcSelected(Calculator c) {
	// calculators not shared, just return null
	return null;
    }
}
