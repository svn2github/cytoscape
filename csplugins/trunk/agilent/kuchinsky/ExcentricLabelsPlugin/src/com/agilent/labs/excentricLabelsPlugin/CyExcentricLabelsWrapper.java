package com.agilent.labs.excentricLabelsPlugin;

import infovis.visualization.magicLens.ExcentricLabels;

import javax.swing.*;
import java.awt.*;

/**
 * AJK: 07/22/06 substitute for Visualization because I can't seem to be able to add
 * Visualization to a JComponent.  Extends JPanel.
 */
public class CyExcentricLabelsWrapper extends JPanel {
    private ExcentricLabels excentric;
    protected CyExcentricVisualizationInteractor interactor;
    protected JComponent parent;

    /**
     * Constructor.
     *
     * @param excentric        InfoViz Excentric Labels Object.
     */
    public CyExcentricLabelsWrapper (ExcentricLabels excentric, boolean excentricLabelVisible) {
        super();
        this.excentric = excentric;
        interactor = new CyExcentricVisualizationInteractor(this);
        setEnabled(true);
        setVisible(true);
        excentric.setEnabled(true);
        if (excentricLabelVisible) {
            excentric.setVisible(true);
        }
    }

    /**
     * Paint the JPanel.
     *
     * @param g Graphics Object.
     */
    public void paint (Graphics g) {
        if (excentric.isVisible()) {
            excentric.paint((Graphics2D) g, getBounds());
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(5, 5, 225, 25);
            g.setColor(Color.BLACK);
            g.drawString("Excentric Label Mode:  Click to Exit", 20, 20);
        }
    }

    /**
     * Gets the Excentric Label Object.
     *
     * @return InfoViz Excentric Label Object.
     */
    public ExcentricLabels getExcentric () {
        return excentric;
    }

    /**
     * Gets the CyExcentricVisualizationInteractor Object.
     *
     * @return CyExcentricVisualizationInteractor Object.
     */
    public CyExcentricVisualizationInteractor getInteractor () {
        return interactor;
    }
}