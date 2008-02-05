package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import cytoscape.Cytoscape;

/**
 * Imports node attributes
 * 
 * OY GEVALT! This is cloned from the code in cytoscape.actions. figure out a
 * better way to do this without duplicating code.
 * 
 */
public class WorkflowExport_XGMML_Action extends WorkflowPanelAction {

	private static final String EXPORT_MENU_TEXT = "Export";

	private static final String XGMML_MENU_TEXT = "Network and attributes as XGMML...";

	private String menuItemText;
	
	private ExportAsXGMMLAction exportXGMML = new ExportAsXGMMLAction();

	/**
	 * Constructor.
	 * 
	 * @param windowMenu
	 *            WindowMenu Object.
	 */
	public WorkflowExport_XGMML_Action(String myString) {
		super(myString);

	}

	public String getToolTipText() {
		return new String(
				"<html> Export a network and attributes in XGMML format. </html>");
	}

	public void actionPerformed(ActionEvent e) {
		String menuItemText = "";
		if (Cytoscape.getCurrentNetwork() == Cytoscape.getNullNetwork()) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"Sorry, you must first have a network to export");
			return;
		}
		exportXGMML.actionPerformed(e);
	}
}