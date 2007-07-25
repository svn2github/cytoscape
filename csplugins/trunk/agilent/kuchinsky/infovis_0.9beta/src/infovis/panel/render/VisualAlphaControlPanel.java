/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.visualization.render.AbstractVisualColumn;
import infovis.visualization.render.VisualAlpha;

import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class VisualAlphaControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * 
 * @infovis.factory VisualPanelFactory infovis.visualization.render.VisualAlpha
 */
public class VisualAlphaControlPanel extends AbstractVisualColumnPanel {
    protected JSlider                 defaultAlphaSlider;
    protected VisualAlpha vcd;
    
    public VisualAlphaControlPanel(AbstractVisualColumn vc) {
        super(vc);
        vcd = (VisualAlpha)vc;
        
        createCombo();
        defaultAlphaSlider = 
            new JSlider(0, 100, (int)(vcd.getDefaultAlpha()*100));
        setTitleBorder(defaultAlphaSlider, "Default %");
        defaultAlphaSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                           (int)defaultAlphaSlider.getPreferredSize()
                                                     .getHeight()));
        defaultAlphaSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                vcd.setDefaultAlpha(defaultAlphaSlider.getValue()/100.0);
            }
        });
        defaultAlphaSlider.setAlignmentX(LEFT_ALIGNMENT);
        add(defaultAlphaSlider);
        update();
    }

    public void update() {
        super.update();
        defaultAlphaSlider.setValue((int)(100*vcd.getDefaultAlpha()));
    }
}
