package com.agilent.labs.excentricLabelsPlugin;

import infovis.visualization.magicLens.ExcentricLabels;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.Collections;
import java.util.ArrayList;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.Cytoscape;

public class ExcentricLabelsConfigPanel extends JPanel {
    private ExcentricLabels excentric;
    private CyExcentricLabelsWrapper wrapper;

    public ExcentricLabelsConfigPanel(ExcentricLabels excentric, CyExcentricLabelsWrapper wrapper) {
        this.excentric = excentric;
        this.wrapper = wrapper;
        initUi();
    }

    private void initUi() {
        GridLayout gridLayout = new GridLayout(20,1);
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

        JLabel attributeLabel = new JLabel("Set Attribute: ");
        this.add(attributeLabel);
        
        Vector attributeList = createAttributeList();
        final JComboBox attributeComboBox = new JComboBox(attributeList);
        attributeComboBox.setSelectedItem(Semantics.CANONICAL_NAME);
        this.add(attributeComboBox);
        attributeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                String attribute = (String) attributeComboBox.getSelectedItem();
                GlobalLabelConfig.setCurrentAttributeName(attribute);
            }
        });

    }

    private Vector createAttributeList() {
        Vector attributeList = new Vector();
        CyAttributes attributes = Cytoscape.getNodeAttributes();
        String[] attributeNames = attributes.getAttributeNames();

        if (attributeNames != null) {
            //  Get all String attributes
            for (int i = 0; i < attributeNames.length; i++) {
                int type = attributes.getType(attributeNames[i]);

                //  only show user visible attributes
                if (attributes.getUserVisible(attributeNames[i])) {
                    if (type == CyAttributes.TYPE_STRING) {
                        attributeList.add(attributeNames[i]);
                    }
                }
            }

            //  Alphabetical sort
            Collections.sort(attributeList);
        }
        return attributeList;
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