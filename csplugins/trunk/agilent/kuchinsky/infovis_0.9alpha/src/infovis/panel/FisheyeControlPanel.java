/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Visualization;
import infovis.panel.dqinter.RangeSlider;
import infovis.visualization.magicLens.DefaultFisheye;
import infovis.visualization.magicLens.Fisheye;
import infovis.visualization.render.VisualFisheye;

import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * Control panel to configure the Fisheye associated
 * with the specified visualization.
 *  
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class FisheyeControlPanel extends AbstractControlPanel {
    protected Fisheye fisheye;
    protected JCheckBox enable;
    protected Box metrics;
    protected ButtonGroup metricsGroup;
    protected JRadioButton metricsL1;
    protected JRadioButton metricsL2;
    protected JRadioButton metricsLinf;
    protected JComboBox lens;
    protected JSlider maxScale;
    protected RangeSlider radius;
    protected JSlider tolerance;

    /**
     * Constructor for FisheyesControlPanel.
     * @param vis the visualization
     */
    public FisheyeControlPanel(Visualization vis) {
        super(vis);
        final VisualFisheye fir = VisualFisheye.get(vis);
        if (fir == null) {
            return; // No VisualFisheye so cannot control Fisheye
        }
        fisheye = fir.getFisheye();
        DefaultFisheye def = null;
        
        if (fisheye == null) {
            def = new DefaultFisheye();
            fisheye = def;
            fisheye.setEnabled(false);
            VisualFisheye.setFisheye(getVisualization(), fisheye);
        }
        else if (fisheye instanceof DefaultFisheye) {
            def = (DefaultFisheye)fisheye;
        }
        
        enable = new JCheckBox("Enable Fisheyes");
        enable.setSelected(fisheye.isEnabled());
        enable.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    fisheye.setEnabled(true);
                    metricsL1.setEnabled(true);
                    metricsL2.setEnabled(true);
                    metricsLinf.setEnabled(true);
                    lens.setEnabled(true);
                    maxScale.setEnabled(true);
                    radius.setEnabled(true);
                    tolerance.setEnabled(true);
                }
                else {
                    fisheye.setEnabled(false);
                    metricsL1.setEnabled(false);
                    metricsL2.setEnabled(false);
                    metricsLinf.setEnabled(false);
                    lens.setEnabled(false);
                    maxScale.setEnabled(false);
                    radius.setEnabled(false);
                    tolerance.setEnabled(false);
                }
                getVisualization().repaint();
            }
        });
        add(enable);

        if (def == null) {
            return;
        }
        metrics = new Box(BoxLayout.X_AXIS);
        setTitleBorder(metrics, "Metrics");
        
        metricsGroup = new ButtonGroup();
        
        metricsL1 = new JRadioButton("L1");
        addMetrics(metricsL1);
        metricsL1.setSelected(def.getDistanceMetric() == DefaultFisheye.DISTANCE_L1);
        
        metricsL2 = new JRadioButton("L2");
        addMetrics(metricsL2);
        metricsL2.setSelected(def.getDistanceMetric() == DefaultFisheye.DISTANCE_L2);
        
        metricsLinf = new JRadioButton("Linf");
        addMetrics(metricsLinf);
        metricsLinf.setSelected(def.getDistanceMetric() == DefaultFisheye.DISTANCE_LINF);
        
        add(metrics);
        
        lens = new JComboBox(getLensTypes());
        lens.setSelectedIndex(def.getLensType());
        setTitleBorder(lens, "Lens Shape");
        lens.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        lens.addActionListener(this);
        add(lens);
        
        int scale = (int)Math.round(def.getMaximumScale());
        maxScale = new JSlider(JSlider.HORIZONTAL, 1,10,
            scale);
        maxScale.setMajorTickSpacing(1);
        maxScale.setPaintTicks(true);
        maxScale.setPaintLabels(true);
        setTitleBorder(maxScale, "Maximum Scale");
        maxScale.addChangeListener(this);
        add(maxScale);
        
        radius = new RangeSlider(0, 200,
            (int)fisheye.getLensRadius(), (int)fisheye.getLensRadius());
        radius.getModel().addChangeListener(this);
    
        setMaximumSize(radius);
        setTitleBorder(radius, "Lens Radius");
        radius.setEnabled(true);
        add(radius);
        
        tolerance = new JSlider(JSlider.HORIZONTAL, 1, 10,
            (int)def.getTolerance());
        
        tolerance.setMajorTickSpacing(1);
        tolerance.setPaintTicks(true);
        tolerance.setPaintLabels(true);
        setTitleBorder(tolerance, "Shape Tolerance");
        tolerance.addChangeListener(this);
        add(tolerance);
    }
    
    protected void addMetrics(JRadioButton button) {
        String name = button.getName();
        button.setActionCommand(name);
        button.addActionListener(this);
        metricsGroup.add(button);
        metrics.add(button);
    }
    
    
    public String[] getLensTypes() {
        String[] str = { "Gaussian", "Cosine", "Hemisphere", "Linear" };
        return str;
    }
    
    /**
     * @see infovis.panel.AbstractControlPanel#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        DefaultFisheye def = null;
        if (fisheye instanceof DefaultFisheye) {
            def = (DefaultFisheye)def;
        }
        if (e.getActionCommand().equals("L1")) {
            def.setDistanceMetric(DefaultFisheye.DISTANCE_L1);
            getVisualization().repaint();
        }
        else if (e.getActionCommand().equals("L2")) {
            def.setDistanceMetric(DefaultFisheye.DISTANCE_L2);
            getVisualization().repaint();
        }
        else if (e.getActionCommand().equals("Linf")) {
            def.setDistanceMetric(DefaultFisheye.DISTANCE_LINF);
            getVisualization().repaint();
        }
        else if (e.getSource() == lens) {
            def.setLensType((short)lens.getSelectedIndex());
            getVisualization().repaint();
        }
        else
            super.actionPerformed(e);
    }

    /**
     * @see infovis.panel.AbstractControlPanel#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        DefaultFisheye def;
        if (fisheye instanceof DefaultFisheye) {
            def = (DefaultFisheye)fisheye;
        }
        else {
            super.stateChanged(e);
            return;
        }
        if (e.getSource() == maxScale) {
            int scale = maxScale.getValue();
            def.setMaximumScale(scale);
            getVisualization().repaint();
        }
        else if (e.getSource() == radius.getModel()) {
            BoundedRangeModel model = radius.getModel();
            def.setRadii(model.getValue(), model.getValue()+model.getExtent());
            getVisualization().repaint();
        }
        else if (e.getSource() == tolerance) {
            int t = tolerance.getValue();
            def.setTolerance(t);
            getVisualization().repaint();
        }
        else
            super.stateChanged(e);
    }

}
