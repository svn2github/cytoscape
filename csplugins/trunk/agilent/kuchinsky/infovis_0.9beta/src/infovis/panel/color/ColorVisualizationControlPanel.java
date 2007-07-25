/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.color;

import infovis.Visualization;
import infovis.visualization.ColorVisualization;
import infovis.visualization.render.VisualColor;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


/**
 * Base class for Color BasicVisualization control panels.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class ColorVisualizationControlPanel extends JPanel {
    protected Visualization visualization;
    protected ColorVisualization colorVisualization;
    protected JDialog            colorDialog;
    protected JColorChooser      colorChooser;
    protected Color              colorChosen;
    ActionListener     cancelAction = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        }
    };
    ActionListener okAction = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            colorChosen = colorChooser.getColor();
        }
    };

    protected ColorVisualizationControlPanel() {
        super(new FlowLayout());
    }
    /**
     * Creates a new ColorVisualizationControlPanel object.
     *
     * @param visualization the Visualization.
     */
    public ColorVisualizationControlPanel(Visualization visualization) {
        super(new FlowLayout());
        this.visualization = visualization;
        VisualColor vc = VisualColor.get(visualization);
        this.colorVisualization = vc.getColorVisualization();
    }

    /**
     * Returns the colorVisualization.
     * @return ColorVisualization
     */
    public ColorVisualization getColorVisualization() {
        return colorVisualization;
    }

    void createColorChooser() {
        if (colorChooser == null) {
            colorChooser = new JColorChooser(colorChosen);
            colorChooser.addChooserPanel(new DefaultAlphaChooserPanel());
            colorDialog = JColorChooser.createDialog(this, "Choose Color",
                                                     true, colorChooser,
                                                     okAction, cancelAction);
        }
    }

    /**
     * Returns the JColorChooser, creating it if necessary.
     *
     * @return the JColorChooser, creating it if necessary.
     */
    public JColorChooser getColorChooser() {
        createColorChooser();
        return colorChooser;
    }

    /**
     * Returns the JDialog containing the JColorChooser,
     * creating it if necessary.
     *
     * @return the JDialog containing the JColorChooser,
     * creating it if necessary.
     */
    public JDialog getColorDialog() {
        createColorChooser();
        return colorDialog;
    }

    /**
     * Trigger a color chooser to change a specified color.
     *
     * @param init the Color
     *
     * @return a color chooser to change a specified color.
     */
    public Color chooseColor(Color init) {
        colorChosen = init;
        getColorChooser().setColor(init);
//        getColorDialog().show();
        getColorDialog().setVisible(true);
        return colorChosen;
    }

    /**
     * Returns the visualization.
     * @return BasicVisualization
     */
    public Visualization getVisualization() {
        return visualization;
    }
}
