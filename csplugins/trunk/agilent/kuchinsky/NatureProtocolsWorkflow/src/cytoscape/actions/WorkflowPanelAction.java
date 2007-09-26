package cytoscape.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

public class WorkflowPanelAction extends CytoscapeAction {
	
	private String _name;
	
	public WorkflowPanelAction(String name)
	{
		super();
		_name = name;
	}
	
	public String toString()
	{
		return _name;
	}
	
	
	/**
	 * ActionPerformed should be overriden by all extending classes
	 * @param e
	 */
	public void actionPerformed (ActionEvent e)
	{
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
				new String("Sorry, " + _name + " is not yet implemented."));

	}

}
