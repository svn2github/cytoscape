/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.panel.color.ColorVisualizationControlPanel;
import infovis.panel.color.ColorVisualizationControlPanelFactory;
import infovis.visualization.ColorVisualization;
import infovis.visualization.render.AbstractVisualColumn;
import infovis.visualization.render.VisualColor;

import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class VisualColorControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * 
 * @infovis.factory VisualPanelFactory infovis.visualization.render.VisualColor
 */
public class VisualColorControlPanel extends AbstractVisualColumnPanel {
    protected VisualColor vcd;
    JComponent                     controlHolder;
    ColorVisualizationControlPanel control;
    protected JCheckBox               smooth;
    
    public VisualColorControlPanel(AbstractVisualColumn vc) {
        super(vc);
        vcd = (VisualColor)vc;
        
        createCombo();
        controlHolder = Box.createHorizontalBox();        
        controlHolder.setAlignmentX(LEFT_ALIGNMENT);
        add(controlHolder);

        smooth = new JCheckBox("Smooth");
        smooth.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                vcd.setSmooth(smooth.isSelected());
            }
        });
        smooth.setAlignmentX(LEFT_ALIGNMENT);
        add(smooth);
        update();
    }
    
    public void update() {
        super.update();
        ColorVisualization vis = vcd.getColorVisualization();
        if (control == null || vis != control.getColorVisualization()) {
            if (control != null)
                controlHolder.remove(control);
            control =
                ColorVisualizationControlPanelFactory.createColorVisualisationControlPanel(getVisualization());
            if (control != null) {
                control.setAlignmentX(LEFT_ALIGNMENT);
                controlHolder.add(control);
                controlHolder.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 
                            control.getPreferredSize().height));
                
            }
        }
        smooth.setSelected(vcd.isSmooth());
    }

}
