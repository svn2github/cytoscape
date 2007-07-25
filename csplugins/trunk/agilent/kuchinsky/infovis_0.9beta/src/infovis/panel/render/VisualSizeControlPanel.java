/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.visualization.render.AbstractVisualColumn;
import infovis.visualization.render.VisualSize;

import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class VisualSizeControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 * @infovis.factory VisualPanelFactory infovis.visualization.render.VisualSize
 */
public class VisualSizeControlPanel extends AbstractVisualColumnPanel {
    protected JSlider                 defaultSizeSlider;
    protected VisualSize              vcd;
    
    public VisualSizeControlPanel(AbstractVisualColumn vc) {
        super(vc);
        vcd = (VisualSize)vc;
        createCombo();
        defaultSizeSlider = new JSlider(0, 100, (int)(vcd.getDefaultSize()));
        Hashtable labels = new Hashtable();
        labels.put(new Integer(0), new JLabel("0=Fit Labels"));
        labels.put(new Integer(50), new JLabel("50"));
        labels.put(new Integer(100), new JLabel("100"));
        
        defaultSizeSlider.setLabelTable(labels);
        defaultSizeSlider.setPaintLabels(true);
        setTitleBorder(defaultSizeSlider, "Default");
        defaultSizeSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                           (int)defaultSizeSlider.getPreferredSize()
                                                     .getHeight()));
        defaultSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                vcd.setDefaultSize(defaultSizeSlider.getValue());
            }
        });
        defaultSizeSlider.setAlignmentX(LEFT_ALIGNMENT);
        add(defaultSizeSlider);
        update();
    }

    public void update() {
        super.update();
        defaultSizeSlider.setValue((int)vcd.getDefaultSize());
    }
}
