package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
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
public class WorkflowExport_Edge_Attributes_Action extends WorkflowPanelAction {

	private static final String EXPORT_MENU_TEXT = "Export";

	private static final String EDGE_ATTRIBUTES_MENU_TEXT = "Edge Attributes";

	private ExportEdgeAttributesAction exportEdgeAttributes = new ExportEdgeAttributesAction();

	/**
	 * Constructor.
	 * 
	 * @param windowMenu
	 *            WindowMenu Object.
	 */
	public WorkflowExport_Edge_Attributes_Action(String myString) {
		super(myString);

	}
	
	public String getToolTipText()
	{
		return new String ("<html> Export Edge attributes to a file. </html>");
	}

	public void actionPerformed(ActionEvent e) {
		exportEdgeAttributes.actionPerformed(e);
	}
}
