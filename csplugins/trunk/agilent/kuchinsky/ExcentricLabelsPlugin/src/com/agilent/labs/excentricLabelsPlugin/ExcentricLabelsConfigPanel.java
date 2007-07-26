package com.agilent.labs.excentricLabelsPlugin;

import infovis.visualization.magicLens.ExcentricLabels;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class ExcentricLabelsConfigPanel extends JPanel {
    private ExcentricLabels excentric;
    private CyExcentricLabelsWrapper wrapper;

    public ExcentricLabelsConfigPanel(ExcentricLabels excentric, CyExcentricLabelsWrapper wrapper) {
        this.excentric = excentric;
        this.wrapper = wrapper;
        initUi();
    }

    private void initUi() {
        GridLayout gridLayout = new GridLayout(2,2);
        this.setLayout(gridLayout);
        JLabel radiusLabel = new JLabel("Set Radius: ");
        float lensRadius = excentric.getLensRadius();
        JSlider radiusSlider = new JSlider(5, 100);
        radiusSlider.setValue((int) lensRadius);
        this.add(radiusLabel);
        this.add(radiusSlider);
        radiusSlider.addChangeListener(new RadiusSliderListener(excentric, wrapper));

        int maxLabels = excentric.getMaxLabels();
        JSlider maxLabelsSlider = new JSlider (5, 100);
        maxLabelsSlider.setValue(maxLabels);
        maxLabelsSlider.addChangeListener(new MaxNumLabelsListener(excentric, wrapper));
        JLabel maxNumLabelsLabel = new JLabel("Set Max Number of Labels: ");
        this.add(maxNumLabelsLabel);
        this.add(maxLabelsSlider);
    }
}

class RadiusSliderListener implements ChangeListener {
    private ExcentricLabels excentric;
    private CyExcentricLabelsWrapper wrapper;

    public RadiusSliderListener (ExcentricLabels excentric, CyExcentricLabelsWrapper wrapper) {
        this.excentric = excentric;
        this.wrapper = wrapper;
    }
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
    	    int radius = source.getValue();
            excentric.setLensRadius((float) radius);
            excentric.setVisible(false);
            excentric.setEnabled(false);
            wrapper.repaint();
        }
    }
}

class MaxNumLabelsListener implements ChangeListener {
    private ExcentricLabels excentric;
    private CyExcentricLabelsWrapper wrapper;

    public MaxNumLabelsListener (ExcentricLabels excentric, CyExcentricLabelsWrapper wrapper) {
        this.excentric = excentric;
        this.wrapper = wrapper;
    }
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
    	    int maxNumLabels = source.getValue();
            excentric.setMaxLabels(maxNumLabels);
            excentric.setVisible(false);
            excentric.setEnabled(false);
            wrapper.repaint();
        }
    }
}