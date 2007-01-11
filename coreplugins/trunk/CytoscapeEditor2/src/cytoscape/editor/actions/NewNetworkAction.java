/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;

import cytoscape.util.CytoscapeAction;

import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;


/**
 * creates a new network and associates an editor with it
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 */
public class NewNetworkAction extends CytoscapeAction {
    // MLC 09/14/06:
    private static final long serialVersionUID = -5729080768973677821L;

    public NewNetworkAction(String editorName, CytoscapeEditorFactory factory) {
        super(editorName);
        setPreferredMenu("File.New.Network");
    }

    public NewNetworkAction(boolean label) {
        super();
    }

    /**
     * create the new network and assign a CytosapeEdtor
     * note that an editor must be first initialized before a new network can be added and the editor assigned to it.
     * in Cytoscape version 2.3, this editor initializatoin is performed upon startup.
     */
    public void actionPerformed(ActionEvent e) {
        CytoscapeEditor cyEditor = CytoscapeEditorManager.getCurrentEditor();

        if (cyEditor == null) {
            String expDescript = "You must first set up an editor for Cytoscape via the File->SetEditor menu item.";
            String title = "Cytoscape Editor not yet set";
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                          expDescript,
                                          title,
                                          JOptionPane.PLAIN_MESSAGE);
        }
        else {
            CyNetwork _newNet = Cytoscape.createNetwork(
                CytoscapeEditorManager.createUniqueNetworkName());

            CyNetworkView newView = Cytoscape.createNetworkView(_newNet);

            CytoscapeEditorManager.setEditorForView(newView, cyEditor);

            // AJK: 06/05/06 BEGIN
            //    switch to Editor cytopanel when loading a new network
            int idx = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).indexOfComponent(
                "Editor");

            //	        CytoscapeEditorManager.log ("index of current palette = " + idx);
            if (idx >= 0) {
                Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(
                        Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).indexOfComponent(
                        "Editor"));
            }

            // AJK: 06/05/06 END
        }
    }
}
