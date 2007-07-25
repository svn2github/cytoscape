/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Visualization;
import infovis.panel.render.VisualAlphaControlPanel;
import infovis.visualization.render.VisualAlpha;
import infovis.visualization.ruler.RulerVisualization;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JSlider;

public class RulersControlPanel extends AbstractControlPanel 
    implements PropertyChangeListener {
    protected RulerVisualization rulerVisualization;
    protected JCheckBox visible;
    protected VisualAlphaControlPanel alphaControl;
    protected JSlider hSize;
    protected JSlider vSize;
    
    public RulersControlPanel(Visualization vis) {
        super(vis);
        rulerVisualization = RulerVisualization.find(vis);
        if (rulerVisualization == null) {
            return;
        }
        visible = new JCheckBox("Visible");
        visible.setSelected(rulerVisualization.isVisible());
        visible.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                rulerVisualization.setVisible(
                        e.getStateChange() == ItemEvent.SELECTED);
            }
        
        });
        rulerVisualization.addPropertyChangeListener(
                RulerVisualization.PROPERTY_VISIBLE, 
                this);
        add(visible);
        alphaControl = new VisualAlphaControlPanel(
                VisualAlpha.get(rulerVisualization));
        add(alphaControl);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(RulerVisualization.PROPERTY_VISIBLE)) {
            visible.setSelected(rulerVisualization.isVisible());
        }
    }
}
