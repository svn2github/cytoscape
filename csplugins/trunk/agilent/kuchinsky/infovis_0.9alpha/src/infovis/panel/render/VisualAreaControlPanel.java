/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.panel.OrientationPanel;
import infovis.visualization.render.AbstractVisualColumn;
import infovis.visualization.render.VisualArea;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class VisualAreaControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * 
 * @infovis.factory VisualPanelFactory infovis.visualization.render.VisualArea
 */
public class VisualAreaControlPanel extends AbstractVisualColumnPanel {
    protected JSlider                 defaultAreaSlider; 
    protected OrientationPanel        orientation;
    protected VisualArea vcd;
    
    public VisualAreaControlPanel(AbstractVisualColumn vc) {
        super(vc);
        vcd = (VisualArea)vc;
        createCombo();
        defaultAreaSlider = new JSlider(0, 100, 100);
        setTitleBorder(defaultAreaSlider, "Default %");
        defaultAreaSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                (int)defaultAreaSlider.getPreferredSize()
                          .getHeight()));
        defaultAreaSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                vcd.setDefaultScale(defaultAreaSlider.getValue()/100.0);
            }
        });
        defaultAreaSlider.setAlignmentX(LEFT_ALIGNMENT);

        add(defaultAreaSlider);
        orientation = new OrientationPanel(true) {
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                vcd.setOrientation(orientation);
            }
        };
        setTitleBorder(orientation, "Orientation");
        orientation.setOrientation(vcd.getOrientation());
        orientation.setAlignmentX(LEFT_ALIGNMENT);
        add(orientation);
        update();
    }

    public void update() {
        super.update();
        defaultAreaSlider.setValue((int)(vcd.getDefaultScale()*100.0));
    }
}
