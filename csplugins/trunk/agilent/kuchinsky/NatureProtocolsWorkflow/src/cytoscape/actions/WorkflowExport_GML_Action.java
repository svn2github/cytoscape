package cytoscape.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;

/**
 * Imports node attributes
 * 
 * OY GEVALT! This is cloned from the code in cytoscape.actions. figure out a
 * better way to do this without duplicating code.
 * 
 */
public class WorkflowExport_GML_Action extends WorkflowPanelAction {

	private static final String EXPORT_MENU_TEXT = "Export";

	private static final String GML_MENU_TEXT = "Network as GML...";

	private ExportAsGMLAction exportGML = new ExportAsGMLAction();


	/**
	 * Constructor.
	 * 
	 * @param windowMenu
	 *            WindowMenu Object.
	 */
	public WorkflowExport_GML_Action(String myString) {
		super(myString);

	}
	
	public String getToolTipText()
	{
		return new String ("<html> Export a network in GML format. </html>");
	}

	public void actionPerformed(ActionEvent e) {
		if (Cytoscape.getCurrentNetwork() == Cytoscape.getNullNetwork())
		{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Sorry, you must first have a network to export");
			return;
		}
		exportGML.actionPerformed(e);
	}
}
