package com.agilent.labs.excentricLabelsPlugin;

import infovis.visualization.magicLens.ExcentricLabels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

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
            String firstLine = "Excentric Label Mode";
            //String secondLine = "Click to exit.  Drag to resize lens.";
            String secondLine = "Click to exit.";

            Rectangle2D boundsLine1 = g.getFontMetrics().getStringBounds(firstLine, g);
            Rectangle2D boundsLine2 = g.getFontMetrics().getStringBounds(secondLine, g);

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(10, 6, (int) boundsLine1.getWidth() + 15, (int) boundsLine2.getHeight() * 3);

            g.setColor(Color.BLACK);
            g.drawString(firstLine, 20, 20);
            g.drawString(secondLine, 20, 20 + (int) boundsLine1.getHeight() + 5);
            g.drawRect(5,5, getBounds().width - 10, getBounds().height - 10);
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