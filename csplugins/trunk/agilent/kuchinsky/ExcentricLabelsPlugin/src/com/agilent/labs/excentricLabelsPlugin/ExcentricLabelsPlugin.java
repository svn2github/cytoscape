package com.agilent.labs.excentricLabelsPlugin;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeToolBar;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.InternalFrameComponent;
import cytoscape.view.cytopanels.CytoPanel;
import ding.view.DGraphView;
import infovis.visualization.magicLens.DefaultExcentricLabels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 *
 * Cytoscape plugin that implements dynamic neighborhood labeling,
 * also known as Excentric Labels, a visualization that enables
 * dynamic labeling of the neighborhood of Nodes located around the cursor.
 *
 * as described in
 * J.-D. Fekete and C. Plaisant. Excentric labeling: Dynamic neighborhood labeling
 * for data visualization. In K. Ehrlich and W. Newman, editors, Proceedings of the International Conference
 * on Human Factors in Computing Systems (CHI 99), pages 512--519. ACM, May 1999
 *
 * and as implemented in the InfoViz Toolkit (http://ivtk.sourceforge.net/)
 *
 * @author Jean-Daniel Fekete (INRIA), Allan Kuchinsky (Agilent), Ethan Cerami (MSKCC).
 * @version 0.1
 *
 *
 */
public class ExcentricLabelsPlugin extends CytoscapePlugin {
    private JButton excentricButton = new JButton("Configure Excentric Labels");
    private DefaultExcentricLabels excentric;
    private CyExcentricLabelsWrapper wrapper;
    private ExcentricLabelsConfigPanel configPanel;

    /**
     * PlugIn Constructor.
     */
    public ExcentricLabelsPlugin () {

        //  Create PlugIn Menu Action, and add to PlugIns Menu
        CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
        CytoscapeToolBar toolBar = cyMenus.getToolBar();
        excentricButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                initExcentricLabelConfig();
            }
        });
        toolBar.add(excentricButton);
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);
    }

    /**
     * Property change listener - to get network/network view destroy events.
     *
     * @param event PropertyChangeEvent
     */
    public void propertyChange (PropertyChangeEvent event) {
        if (event.getPropertyName() != null) {
            if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        initExcentricLabels ();
                    }
                });
            } else if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_DESTROYED)) {
            } else if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUS)) {
            }
        }
    }

    /**
     * Initializes Excentric Labels.
     */
    public void initExcentricLabels () {
        System.out.println("Activating Excentric labels");
        // Get Current Network View
        CyNetworkView newView = Cytoscape.getCurrentNetworkView();

        if (newView != null) {
            //  Create Default InfoViz Excentric Label Object
            excentric = new CustomExcentricLabels();
            excentric.setOpaque(true);

            //  Create the LabeledComponent.
            //  Given rectangular coordinates, the LabeledComponent returns a set of "things"
            //  that appear within this rectangle.
            CyLabeledComponent labeledComponent = new CyLabeledComponent(newView);
            excentric.setVisualization(labeledComponent);

            //  Get the FOREGROUND_CANVAS, so we can draw labels on it.
            JComponent foregroundCanvas = ((DGraphView) newView).getCanvas
                    (DGraphView.Canvas.FOREGROUND_CANVAS);

            //  Create the CyWrapper class
            wrapper = new CyExcentricLabelsWrapper(excentric, false);

            //  Add the CyWrapper class to the FOREGROUND_CANVAS.
            foregroundCanvas.add(wrapper);

            //  Set size of wrapper and default location of lens
            wrapper.setSize(foregroundCanvas.getWidth(), foregroundCanvas.getHeight());

            final InternalFrameComponent internalFrame = Cytoscape.getDesktop().getNetworkViewManager().
                    getInternalFrameComponent(newView);
            internalFrame.addComponentListener(new ComponentListener() {
                public void componentResized (ComponentEvent e) {
                    wrapper.setSize(internalFrame.getSize());
                }

                public void componentMoved (ComponentEvent e) {
                }

                public void componentShown (ComponentEvent e) {
                }

                public void componentHidden (ComponentEvent e) {
                }
            });
            wrapper.getExcentric().setLens(foregroundCanvas.getWidth() / 2,
                    foregroundCanvas.getHeight() / 2);

        }
    }

    /**
     * Initializes Config Panel
     */
    private void initExcentricLabelConfig() {
        CytoPanel cytoPanelSouth = Cytoscape.getDesktop().getCytoPanel
                (SwingConstants.WEST);
        if (configPanel == null) {
            configPanel = new ExcentricLabelsConfigPanel(excentric, wrapper);
            cytoPanelSouth.add("Excentric Labels", configPanel);
        }
        int index = cytoPanelSouth.indexOfComponent(configPanel);
        cytoPanelSouth.setSelectedIndex(index);
    }
}
