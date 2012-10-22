package cytoscape.coreplugins.biopax.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import giny.view.NodeView;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.BioPaxVisualStyleUtil;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;

import ding.view.NodeContextMenuListener;

public class BiopaxNodeCtxMenuListener implements NodeContextMenuListener {

	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
//		JMenuItem myMenuItem = new JMenuItem("Show OWL");
//		myMenuItem.addActionListener(new DisplayBiopaxXmlAction(nodeView));
//		if (menu == null) {
//			menu = new JPopupMenu();
//		}
//		menu.add(myMenuItem);

	}

	
	
	class DisplayBiopaxXmlAction extends CytoscapeAction {
		private NodeView nodeView;
		
		public DisplayBiopaxXmlAction(NodeView nodeView) {
			this.nodeView = nodeView;
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			String nodeId = nodeView.getNode().getIdentifier();
			
			String owl = Cytoscape.getNodeAttributes()
					.getStringAttribute(nodeId, BioPaxUtil.BIOPAX_DATA);	
			
			String label = Cytoscape.getNodeAttributes()
					.getStringAttribute(nodeId, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);			
			
			Component component = Cytoscape.getDesktop()
					.findComponentAt((int)nodeView.getXPosition(), (int)nodeView.getYPosition());
			
			JOptionPane.showMessageDialog(component, owl, label, JOptionPane.PLAIN_MESSAGE);
		}
		
	}
	
	
}
