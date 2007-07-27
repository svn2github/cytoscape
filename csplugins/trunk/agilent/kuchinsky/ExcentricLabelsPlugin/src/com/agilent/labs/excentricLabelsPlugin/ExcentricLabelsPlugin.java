package com.agilent.labs.excentricLabelsPlugin;

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
 * @author Jean-Daniel Fekete (INRIA), Allan Kuchinsky (Agilent) 
 * @version 0.1
 * 
 * 
 */
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import ding.view.DGraphView;
import infovis.visualization.magicLens.DefaultExcentricLabels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class ExcentricLabelsPlugin extends CytoscapePlugin {

    //  Currently stores a HashMap of network IDs -->  CyExcentricLabelsWrapper Objects.
    private HashMap viewWrapperMap = new HashMap();

    /**
     * PlugIn Constructor.
     */
    public ExcentricLabelsPlugin () {

        //  Create PlugIn Menu Action, and add to PlugIns Menu
        CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
        JMenu plugInMenu = cyMenus.getViewMenu();
        JCheckBoxMenuItem menuItem1 = new JCheckBoxMenuItem("Show excentric labels");
        menuItem1.setSelected(false);
        plugInMenu.add(menuItem1);
        MainPluginAction mpa = new MainPluginAction(menuItem1);
        menuItem1.addActionListener(mpa);
    }

    /**
     * This class gets attached to the menu item.
     */
    public class MainPluginAction extends AbstractAction {
        private JCheckBoxMenuItem menuItem;
        private boolean excentricEnabled = false;
        private DefaultExcentricLabels excentric;
        private CyExcentricLabelsWrapper wrapper;
        private ExcentricLabelsConfigPanel configPanel;

        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public MainPluginAction (JCheckBoxMenuItem menuItem) {
            super("Show excentric labels");
            this.menuItem = menuItem;
        }

        /**
         * Gives a description of this plugin.
         */
        public String describe () {
            StringBuffer sb = new StringBuffer();
            sb.append("Show excentric labels");
            return sb.toString();
        }

        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed (ActionEvent ae) {
            boolean errorOccurred = false;

            if (!excentricEnabled) {
                // Get Current Network View
                CyNetworkView newView = Cytoscape.getCurrentNetworkView();

                if (newView != null) {
                    //  Create Default InfoViz Excentric Label Handler
                    excentric = new CustomExcentricLabels();
                    excentric.setOpaque(true);

                    //  Create the LabeledComponent.
                    //  Given rectangular coordinates, the LabeledComponent returns a set of "things"
                    //  that appear within this rectangle.
                    CyLabeledComponent labeledComponent = new CyLabeledComponent(newView);
                    excentric.setVisualization(labeledComponent);

                    //  Get the FOREGROUND_CANVAS, so we can draw labels on it.
                    JComponent component =
                            ((DGraphView) newView).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);

                    //  Create the CyWrapper class
                    wrapper = new CyExcentricLabelsWrapper(excentric, component, labeledComponent);
                    viewWrapperMap.put(newView.getIdentifier(), wrapper);

                    //  Add the CyWrapper class to the FOREGROUND_CANVAS.
                    ((DGraphView) newView).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(wrapper);
                    wrapper.getInteractor().install(wrapper);
                    wrapper.setInstalled(true);
                    wrapper.addMouseMotionListener(wrapper.getInteractor());
                    wrapper.setVisible(true);
                    wrapper.setSize(component.getWidth(), component.getHeight());

                    //  Create the config panel in the WEST
                    CytoPanel cytoPanelSouth = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
                    configPanel = new ExcentricLabelsConfigPanel(excentric,
                            wrapper);
                    cytoPanelSouth.add("Excentric Labels", configPanel);
                    int index = cytoPanelSouth.indexOfComponent(configPanel);
                    cytoPanelSouth.setSelectedIndex(index);
                } else {
                    errorOccurred = true;
                }
            } else {
                CyNetworkView newView = Cytoscape.getCurrentNetworkView();
                if (newView != null) {
                    ((DGraphView) newView).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).remove(wrapper);
                    ((DGraphView) newView).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).repaint();
                }
                CytoPanel cytoPanelSouth = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
                cytoPanelSouth.remove(configPanel);
                cytoPanelSouth.setSelectedIndex(0);
            }
            if (!errorOccurred) {
                this.excentricEnabled = ! excentricEnabled;
                menuItem.setSelected(excentricEnabled);
            }
        }
    }
}
