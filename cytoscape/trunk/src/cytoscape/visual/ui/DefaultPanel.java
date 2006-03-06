
/*
  File: DefaultPanel.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
package cytoscape.visual.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.dialogs.MiscGB;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.GlobalAppearanceCalculator;
//----------------------------------------------------------------------------
/**
 * Defines a class to provide the interface for specifying global defaults such as background color.
 */
public class DefaultPanel extends JPanel implements ChangeListener {
    //private DefaultBackgroundRenderer bgRender;
    private VisualMappingManager vmm;
    private ValueDisplayer backColor;
    private VizMapUI parentDialog;

    public DefaultPanel(VizMapUI parentDialog, VisualMappingManager vmm) {
	super(false);
    this.parentDialog = parentDialog;
    this.vmm = vmm;


    //  Register class to receive notifications of changes in the
    //  GlobalAppearance Calculator.
    VisualStyle vs = vmm.getVisualStyle();
    GlobalAppearanceCalculator gCalc =
            vs.getGlobalAppearanceCalculator();
    gCalc.addChangeListener(this);

    //  Also, get notifications is user changes to a different
    //  visual style.
    vmm.addChangeListener(this);

    addColorButton();
    }

    private void addColorButton() {
        // this is really really evil
        GridBagGroup def = new GridBagGroup();
        def.panel = this;
        setLayout(def.gridbag);
        MiscGB.pad(def.constraints, 2, 2);
        MiscGB.inset(def.constraints, 3);

        // background color
        VisualStyle vs = vmm.getVisualStyle();
        GlobalAppearanceCalculator gCalc =
                vs.getGlobalAppearanceCalculator();
        Color initColor = gCalc.getDefaultBackgroundColor();
        this.backColor = ValueDisplayer.getDisplayFor(parentDialog, "Background Color", initColor);
        backColor.addItemListener(new BackColorListener());
        JButton backColorBut = new JButton("Background Color");
        backColorBut.addActionListener(backColor.getInputListener());
        MiscGB.insert(def, backColorBut, 0, 0);
        MiscGB.insert(def, backColor, 1, 0);
    }

    public void stateChanged(ChangeEvent e) {
        VisualStyle vs = vmm.getVisualStyle();
        GlobalAppearanceCalculator gCalc =
                vs.getGlobalAppearanceCalculator();
        Color color = gCalc.getDefaultBackgroundColor();
        backColor.setBackground(color);
        this.repaint();
    }

    private class BackColorListener implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED) {
		Color newBG = (Color) backColor.getValue();
                VisualStyle vs = vmm.getVisualStyle();
                vs.getGlobalAppearanceCalculator().setDefaultBackgroundColor(newBG);
	    }
	}
    }

}
