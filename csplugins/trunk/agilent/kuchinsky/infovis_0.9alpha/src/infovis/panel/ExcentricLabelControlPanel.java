/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Visualization;
import infovis.visualization.magicLens.ExcentricLabelVisualization;
import infovis.visualization.magicLens.ExcentricLabels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class ExcentricLabelControlPanel extends AbstractControlPanel {
    protected ExcentricLabels excentricLabels;
    protected JCheckBox enable;
    protected JSlider radiusSlider;
    protected JSlider maxCountSlider;
    protected JCheckBox opaque;

    /**
     * Constructor for ExcentricLabelControlPanel.
     *
     * @param vis the visualization
     */
    public ExcentricLabelControlPanel(Visualization vis) {
        super(vis);
        excentricLabels = findExcentric();
        
        enable = new JCheckBox("Enable Excentric Labels");
        enable.setSelected(getExcentricVisualization().isEnabled());
        enable.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    getExcentricVisualization().setEnabled(false);
                    radiusSlider.setEnabled(false);
                    maxCountSlider.setEnabled(false);
                    opaque.setEnabled(false);
                }
                else {
                    radiusSlider.setEnabled(true);
                    maxCountSlider.setEnabled(true);
                    opaque.setEnabled(true);
                    getExcentricVisualization().setEnabled(true);
                }
            }
        });
        add(enable);
        
        radiusSlider = new JSlider(1, 150);
        radiusSlider.setValue((int)(excentricLabels.getLensRadius()));
        radiusSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                excentricLabels.setLensRadius(radiusSlider.getValue());
            }
        });
        radiusSlider.setBorder(BorderFactory.createTitledBorder("Radius"));
        add(radiusSlider);
        
        maxCountSlider = new JSlider(10, 100);
        maxCountSlider.setValue(excentricLabels.getMaxLabels());
        maxCountSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                excentricLabels.setMaxLabels(maxCountSlider.getValue());
            }
        });
        maxCountSlider.setBorder(BorderFactory.createTitledBorder("Max Labels"));
        add(maxCountSlider);
        
        opaque = new JCheckBox("Opaque Labels");
        opaque.setSelected(excentricLabels.isOpaque());
        opaque.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                excentricLabels.setOpaque(opaque.isSelected());
            }
        });
        add(opaque);
    }
    
    public ExcentricLabelVisualization getExcentricVisualization() {
    	return (ExcentricLabelVisualization)getVisualization().
        findVisualization(ExcentricLabelVisualization.class);
    }
    
    public ExcentricLabels findExcentric() {
        ExcentricLabelVisualization el = getExcentricVisualization();
        if (el == null)
            return null;
        return el.getExcentric();
    }

}
