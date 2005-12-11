/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;


/**
 * NOTE: THE CYTOSCAPE EDITOR FUNCTIONALITY IS STILL BEING EVOLVED AND IN A STATE OF TRANSITION TO A 
 * FULLY EXTENSIBLE EDITING FRAMEWORK FOR CYTOSCAPE VERSION 2.3.  
 * 
 * THE JAVADOC COMMENTS ARE OUT OF DATE IN MANY PLACES AND ARE BEING UPDATED.  
 * THE APIs WILL CHANGE AND THIS MAY IMPACT YOUR CODE IF YOU 
 * MAKE EXTENSIONS AT THIS POINT.  PLEASE CONTACT ME (mailto: allan_kuchinsky@agilent.com) 
 * IF YOU ARE INTENDING TO EXTEND THIS CODE AND I WILL WORK WITH YOU TO HELP MINIMIZE THE IMPACT TO YOUR CODE OF 
 * FUTURE CHANGES TO THE FRAMEWORK
 *
 * PLEASE SEE http://www.cytoscape.org/cgi-bin/moin.cgi/CytoscapeEditorFramework FOR 
 * DETAILS ON THE EDITOR FRAMEWORK AND PLANNED EVOLUTION FOR CYTOSCAPE VERSION 2.3.
 *
 */

/**
 * creates a new network and associates an editor with it
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * 
 */
public class NewNetworkAction extends CytoscapeAction {


	/**
	 * the name (and type) of the editor to be created
	 */
	private String editorName;

	/**
	 * TODO: not used, delete this.
	 */
	private CytoscapeEditorFactory factory;

	private static int counter = 0;

	public NewNetworkAction(String editorName, CytoscapeEditorFactory factory) {
		super(editorName);
		this.editorName = editorName;
		this.factory = factory;
		setPreferredMenu("File.New");
	}

	public NewNetworkAction(boolean label) {
		super(); 
	}


	/**
	 * create the new network and assign a CytosapeEdtor
	 * note that an editor must be first initialized before a new network can be added and the editor assigned to it.
	 * in Cytoscape version 2.2, this editor initializatoin is performed upon startup.
	 */
	public void actionPerformed(ActionEvent e) {

		CytoscapeEditor cyEditor = CytoscapeEditorManager.getCurrentEditor();
		if (cyEditor == null) {

			String expDescript = "You must first set up an editor for Cytoscape via the File->SetEditor menu item.";
			String title = "Cytoscape Editor not yet set";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
		}

		else {
			CyNetwork _newNet = Cytoscape.createNetwork("Net:"
					+ CytoscapeEditorManager.getNetworkNameCounter());
			CytoscapeEditorManager.incrementNetworkNameCounter();

			CyNetworkView newView = Cytoscape.createNetworkView(_newNet);

			CytoscapeEditorManager.setEditorForNetwork(_newNet, cyEditor);
			CytoscapeEditorManager.setEditorForView(newView, cyEditor); 
		}
	}

}

