package csplugins.quickfind.plugin;

import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import csplugins.quickfind.util.QuickFindListener;
import csplugins.quickfind.view.QuickFindPanel;
import csplugins.test.quickfind.test.TaskMonitorBase;
import csplugins.widgets.autocomplete.index.GenericIndex;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.widgets.autocomplete.view.TextIndexComboBox;
import csplugins.widgets.slider.JRangeSliderExtended;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeToolBar;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.InnerCanvas;
import giny.view.NodeView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import prefuse.data.query.NumberRangeModel;

/**
 * Quick Find PlugIn.
 *
 * @author Ethan Cerami.
 */
public class QuickFindPlugIn extends CytoscapePlugin
        implements PropertyChangeListener, QuickFindListener {
    private QuickFindPanel quickFindToolBar;

    /**
     * Constructor.
     */
    public QuickFindPlugIn() {
        initListeners();
        initToolBar();
        initIndex();
    }

    /**
     * Initializes All Cytoscape Listeners.
     */
    private void initListeners() {
        // to catch network creation / destruction events
        Cytoscape.getSwingPropertyChangeSupport().
                addPropertyChangeListener(this);

        // to catch network selection / focus events
        Cytoscape.getDesktop().getNetworkViewManager().
                getSwingPropertyChangeSupport().addPropertyChangeListener(this);

        QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
        quickFind.addQuickFindListener(this);
    }

    /**
     * Initalizes Tool Bar.
     */
    private void initToolBar() {
        CytoscapeDesktop desktop = Cytoscape.getDesktop();
        CyMenus cyMenus = desktop.getCyMenus();
        CytoscapeToolBar toolBar = cyMenus.getToolBar();
        quickFindToolBar = new QuickFindPanel();
        TextIndexComboBox comboBox = quickFindToolBar.getTextIndexComboBox();
        ActionListener listener = new UserSelectionListener(comboBox);
        comboBox.addFinalSelectionListener(listener);

        JRangeSliderExtended slider = quickFindToolBar.getSlider();
        RangeSelectionListener rangeSelectionListener =
                new RangeSelectionListener(slider);
        slider.addChangeListener(rangeSelectionListener);
        toolBar.add(quickFindToolBar);
        toolBar.validate();
    }

    /**
     * Initializes index, if network already exists.
     * This condition may occur if a user loads up a network from the command
     * line, and the network is already loaded prior to any plugins being loaded
     */
    private void initIndex() {
        final QuickFind quickFind =
                QuickFindFactory.getGlobalQuickFindInstance();
        //  If a network already exists within Cytoscape, index it
        final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
        if (cyNetwork != null && cyNetwork.getNodeCount() > 0) {
            //  Run Indexer in separate background daemon thread.
            Thread thread = new Thread() {
                public void run() {
                    quickFind.addNetwork(cyNetwork, new TaskMonitorBase());
                }
            };
            thread.start();
        }
    }

    /**
     * Property change listener - to get network/network view destroy events.
     *
     * @param event PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent event) {
        final QuickFind quickFind =
                QuickFindFactory.getGlobalQuickFindInstance();
        if (event.getPropertyName() != null) {
            if (event.getPropertyName().equals
                    (CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
                final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
                //  Run Indexer in separate background daemon thread.
                Thread thread = new Thread() {
                    public void run() {
                        quickFind.addNetwork(cyNetwork, new TaskMonitorBase());
                    }
                };
                thread.start();
            } else if (event.getPropertyName().equals
                    (CytoscapeDesktop.NETWORK_VIEW_DESTROYED)) {
                CyNetworkView networkView = (CyNetworkView) event.getNewValue();
                CyNetwork cyNetwork = networkView.getNetwork();
                quickFind.removeNetwork(cyNetwork);
                swapCurrentNetwork(quickFind);
            } else if (event.getPropertyName().equals
                    (CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
                swapCurrentNetwork(quickFind);
            }
        }
    }

    /**
     * Determine which network view now has focus.
     * If no network view has focus, disable quick find.
     */
    private void swapCurrentNetwork(QuickFind quickFind) {
        CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
        CyNetwork cyNetwork;
        boolean networkHasFocus = false;
        if (networkView != null) {
            if (networkView.getNetwork() != null) {
                cyNetwork = networkView.getNetwork();
                TextIndex textIndex = (TextIndex) quickFind.getIndex(cyNetwork);
                if (textIndex != null) {
                    quickFindToolBar.setIndex(textIndex);
                    networkHasFocus = true;
                }
            }
        }
        if (!networkHasFocus) {
            quickFindToolBar.noNetworkLoaded();
        }
    }

    /**
     * Event:  Network Added to Index.
     *
     * @param network CyNetwork Object.
     */
    public void networkAddedToIndex(CyNetwork network) {
        //  No-op
    }

    /**
     * Event:  Network Removed from Index.
     *
     * @param network CyNetwork Object.
     */
    public void networkRemovedfromIndex(CyNetwork network) {
        //  No-op
    }

    /**
     * Indexing started.
     */
    public void indexingStarted() {
        quickFindToolBar.indexingInProgress();
    }

    /**
     * Indexing ended.
     */
    public void indexingEnded() {
        QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
        CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
        GenericIndex index = quickFind.getIndex(cyNetwork);
        quickFindToolBar.setIndex(index);
        quickFindToolBar.enableAllQuickFindButtons();
    }
}

/**
 * Listens for Final Selection from User.
 *
 * @author Ethan Cerami.
 */
class UserSelectionListener implements ActionListener {
    private TextIndexComboBox comboBox;
    private static final int NODE_SIZE_MULTIPLER = 10;

    /**
     * Constructor.
     *
     * @param comboBox TextIndexComboBox.
     */
    public UserSelectionListener(TextIndexComboBox comboBox) {
        this.comboBox = comboBox;
    }

    /**
     * User has made final selection.
     *
     * @param e ActionEvent Object.
     */
    public void actionPerformed(ActionEvent e) {

        //  Get Current Network
        final CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();

        //  Get Current User Selection
        //  If we have a hit, select matching nodes and fit content.
        Object o = comboBox.getSelectedItem();
        if (o != null && o instanceof Hit) {
            Hit hit = (Hit) comboBox.getSelectedItem();
            currentNetwork.unselectAllNodes();
            currentNetwork.unselectAllEdges();
            final Object graphObjects[] = hit.getAssociatedObjects();

            final ArrayList list = new ArrayList();
            for (int i = 0; i < graphObjects.length; i++) {
                list.add(graphObjects[i]);
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    currentNetwork.setSelectedNodeState(list, true);
                    ((DingNetworkView)
                            Cytoscape.getCurrentNetworkView()).fitSelected();
                    //  If only one node is selected, auto-adjust zoom factor
                    //  so that node does not take up whole screen.
                    if (graphObjects.length == 1) {
                        if (graphObjects[0] instanceof CyNode) {
                            CyNode node = (CyNode) graphObjects[0];

                            //  Obtain dimensions of current InnerCanvas
                            DGraphView graphView = (DGraphView)
                                    Cytoscape.getCurrentNetworkView();
                            InnerCanvas innerCanvas = graphView.getCanvas();

                            NodeView nodeView = Cytoscape.
                                    getCurrentNetworkView().getNodeView(node);

                            double width = nodeView.getWidth()
                                    * NODE_SIZE_MULTIPLER;
                            double height = nodeView.getHeight()
                                    * NODE_SIZE_MULTIPLER;
                            double scaleFactor = Math.min
                                    (innerCanvas.getWidth() / width,
                                            (innerCanvas.getHeight() / height));
                            Cytoscape.getCurrentNetworkView().setZoom
                                    (scaleFactor);
                        }
                    }
                    Cytoscape.getCurrentNetworkView().updateView();
                }
            }
            );
        }
    }
}

/**
 * Action to select a range of nodes.
 *
 * @author Ethan Cerami.
 */
class RangeSelectionListener implements ChangeListener {
    private JRangeSliderExtended slider;

    /**
     * Constructor.
     * @param slider JRangeSliderExtended Object.
     */
    public RangeSelectionListener(JRangeSliderExtended slider) {
        this.slider = slider;
    }

    /**
     * State Change Event.
     * @param e ChangeEvent Object.
     */
    public void stateChanged(ChangeEvent e) {
        QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
        final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
        GenericIndex index = quickFind.getIndex(cyNetwork);
        NumberRangeModel model = (NumberRangeModel) slider.getModel();
        if (slider.isVisible()) {
            if (index instanceof NumberIndex) {
                NumberIndex numberIndex = (NumberIndex) index;
                final java.util.List nodeList = numberIndex.getRange
                        ((Number) model.getLowValue(),
                        (Number) model.getHighValue());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        cyNetwork.unselectAllNodes();
                        cyNetwork.unselectAllEdges();
                        cyNetwork.setSelectedNodeState(nodeList, true);
                        Cytoscape.getCurrentNetworkView().updateView();
                    }
                }
                );

            }
        }
    }
}
