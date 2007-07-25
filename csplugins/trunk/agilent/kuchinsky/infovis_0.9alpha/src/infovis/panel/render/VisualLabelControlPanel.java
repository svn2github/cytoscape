/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.visualization.render.AbstractVisualColumn;
import infovis.visualization.render.VisualLabel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class VisualLabelControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * 
 * @infovis.factory VisualPanelFactory infovis.visualization.render.VisualLabel
 */
public class VisualLabelControlPanel extends AbstractVisualColumnPanel {
    protected JCheckBox               labelItems;
    protected VisualLabel vcd;
    
    public VisualLabelControlPanel(AbstractVisualColumn vc) {
        super(vc);
        vcd = (VisualLabel)vc;
        createCombo();
        Box checkBox = new Box(BoxLayout.X_AXIS);                          
        labelItems = new JCheckBox("Label all items");
        labelItems.setSelected(vcd.isShowingLabel());
        labelItems.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                vcd.setShowingLabel(labelItems.isSelected());
            }
        });        
        checkBox.add(labelItems);
        checkBox.setAlignmentX(LEFT_ALIGNMENT);
        add(checkBox);
        update();
    }

    public void update() {
        super.update();
        labelItems.setSelected(vcd.isShowingLabel());
    }
}
