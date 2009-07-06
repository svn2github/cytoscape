package org.cytoscape.groups.results.internal.ui;

// System imports
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import metaNodePlugin2.model.MetaNode;

//import metaNodePlugin2.model.MetaNode;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;

//Cytoscape group system imports
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupViewer;
import cytoscape.groups.CyGroupViewer.ChangeType;

import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupChangeListener;

import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
/**
 * The GroupPanel is the implementation for the Cytopanel that presents the
 * group list mechanism to the user.
 */
public class GroupPanel extends JPanel implements ListSelectionListener,
		ActionListener, CyGroupChangeListener, CyGroupViewer {

	private CytoPanel cytoPanel; 
	public String viewerName = "moduleFinderViewer";
	private static boolean registeredWithGroupPanel = false;

	// Controlling variables
	public static boolean multipleEdges = false;
	public static boolean recursive = true;
	
	private Method updateMethod = null;
	private CyGroupViewer namedSelectionViewer = null;
	
	// State values
	public static final int EXPANDED = 1;
	public static final int COLLAPSED = 2;

	private String COLLAPSE_AS_MTEANODE = "Collapse as metanode";
	private String EXPAND_METANODE = "Expand metanode";

	public GroupPanel() {
		super();

		initComponents();
		btnCollapseAsMetaNode.setEnabled(false);
		btnCreateNetworkView.setEnabled(false);

		btnCollapseAsMetaNode.addActionListener(this);
		btnCreateNetworkView.addActionListener(this);
		resultPanel.getTable().getSelectionModel().addListSelectionListener(
				this);
		cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		CyGroupManager.addGroupChangeListener(this);
		
		//register groupViewer
		CyGroupManager.registerGroupViewer(this);
		
		// register it to namedSelection
		registerWithGroupPanel();
	}

	// copy of metaNodePlugin2.MetaNodePlugin2.registerWithGroupPanel()
	private void registerWithGroupPanel() {
		if (registeredWithGroupPanel) {
			try {
				updateMethod.invoke(namedSelectionViewer);
			} catch (Exception e) {
				System.err.println(e);
			}
			return;
		}

		namedSelectionViewer = CyGroupManager.getGroupViewer("namedSelection");
		if (namedSelectionViewer == null)
			return;

		if (namedSelectionViewer.getClass().getName().equals("namedSelection.NamedSelection")) {
			// Get the addViewerToGroupPanel method

			try {
				updateMethod = namedSelectionViewer.getClass().getMethod("updateGroupPanel");
				Method regMethod = namedSelectionViewer.getClass().getMethod("addViewerToGroupPanel", CyGroupViewer.class);
				regMethod.invoke(namedSelectionViewer, (CyGroupViewer)this);
				registeredWithGroupPanel = true;
			} catch (Exception e) {
				System.err.println(e);
				return;
			}
			// Invoke it
		}
	}

	
	//required by CyGroupChangeListener
	public void groupChanged(CyGroup group, CyGroupChangeListener.ChangeType change) { 
		if ( change == CyGroupChangeListener.ChangeType.GROUP_CREATED ) {
			resultPanel.addGroup(group);
			// set visible
			if ( cytoPanel.getState() == CytoPanelState.HIDE ){
				cytoPanel.setState( CytoPanelState.DOCK );
			}
		} else if ( change == CyGroupChangeListener.ChangeType.GROUP_DELETED ) {
			resultPanel.removeGroup(group);
		} else if ( change == CyGroupChangeListener.ChangeType.GROUP_MODIFIED ) {
			//System.out.println("groupChanged event received, should update resltPanel " + group.toString());
		} else {
			//System.err.println("unsupported change type: " + change);
		}
	}
	
	//
	// These are required by the CyGroupViewer interface
	/**
	 * Return the name of our viewer
	 *
	 * @return viewer name
	 */
	public String getViewerName() { return viewerName; }
	

	/**
	 * This is called when a new group has been created that
	 * we care about.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that was just created
	 */
	public void groupCreated(CyGroup group) { 

		//System.out.println("groupCreated()");
	}

	/**
	 * This is called when a new group has been created that
	 * we care about.  This version of the groupCreated
	 * method is called by XGMML and provides the CyNetworkView
	 * that is in the process of being created.
	 *
	 * @param group the CyGroup that was just created
	 * @param view the CyNetworkView that is being created
	 */
	public void groupCreated(CyGroup group, CyNetworkView myview) { 
		//System.out.println("groupCreated() Apple");

	}

	/**
	 * This is called when a group we care about is about to 
	 * be deleted.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) {
		//System.out.println("groupWillBeRemoved()");
	}


	
	/**
	 * This is called when a group we care about has been
	 * changed (usually node added or deleted).
	 *
	 * @param group the CyGroup that has changed
	 * @param node the CyNode that caused the change
	 * @param change the change that occured
	 */
	public void groupChanged(CyGroup group, CyNode node, CyGroupViewer.ChangeType change) { 
		//System.out.println("groupChanged()");
	}


	
	// handle button-click event
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj instanceof JButton) {
			JButton btn = (JButton) obj;
			int selected = resultPanel.getTable().getSelectedRow();
			
			if (selected == -1){
				// No network is selected
				return;
			}
			
			CyGroup group = (CyGroup) resultPanel.getTable().getModel()
					.getValueAt(selected, 1);
			if (btn == btnCollapseAsMetaNode)
				collapseAsMetaNode(group);
			else if (btn == btnCreateNetworkView)
				createNetworkView(group);
		}
	}

	private void collapseAsMetaNode(CyGroup pGroup) {
		
		// check if the metaNode already existed
		MetaNode theMetaNode = MetaNode.getMetaNode(pGroup);
		if (theMetaNode != null){
			if (theMetaNode.isCollapsed(Cytoscape.getCurrentNetworkView())){
				// if the metaNode is collapsed, expand it	
				theMetaNode.expand(recursive, Cytoscape.getCurrentNetworkView(), true);
			}
			else {
				// if the metaNode is expanded, collapse it
				theMetaNode.collapse(recursive, multipleEdges, true, Cytoscape.getCurrentNetworkView());				
			}			
		}
		else {
			// MetaNode does not exist yet, create one and collapse it

			// from metaNodePlugin2.MetaNodePlugin2		
			// Careful!  If one of the nodes is an expanded (but not hidden) metanode,
			// we need to collapse it first
			for (CyNode node: (List<CyNode>)new ArrayList(pGroup.getNodes())) {
				MetaNode mn = MetaNode.getMetaNode(node);
				if (mn == null) continue;
				// Is this an expanded metanode?
				if (mn.getCyGroup().getState() == EXPANDED) {
					// Yes, collapse it
					mn.collapse(recursive, multipleEdges, true, Cytoscape.getCurrentNetworkView());
				}
			}

			MetaNode newNode = new MetaNode(pGroup);
			//groupCreated(pGgroup);
			newNode.collapse(recursive, multipleEdges, true, Cytoscape.getCurrentNetworkView());

			registerWithGroupPanel();			
		}
		
		// update the buttonText
		btnCollapseAsMetaNode.setText(determineButtonText(pGroup));
	}

	
	private void createNetworkView(CyGroup pGroup) {

		List<CyNode> nodes = pGroup.getNodes();
		List edges = pGroup.getInnerEdges();

		String newNetworkTitle = CyNetworkNaming
				.getSuggestedNetworkTitle(pGroup.getGroupName());
		CyNetwork new_network = Cytoscape.createNetwork(nodes, edges,
				newNetworkTitle);

		// Should we keep VS and node positions, if yes, we need a reference to
		// the parent network
		// CyNetworkView new_view =
		// Cytoscape.getNetworkView(new_network.getIdentifier());

		/*
		 * 
		 * if (new_view == Cytoscape.getNullNetworkView()) { return; }
		 * 
		 * String vsName = "default";
		 * 
		 * // keep the node positions Iterator i = new_network.nodesIterator();
		 * 
		 * while (i.hasNext()) { CyNode node = (CyNode) i.next();
		 * new_view.getNodeView(node)
		 * .setOffset(new_view.getNodeView(node).getXPosition(),
		 * new_view.getNodeView(node).getYPosition()); }
		 * 
		 * new_view.fitContent();
		 * 
		 * // Set visual style VisualStyle newVS = new_view.getVisualStyle();
		 * 
		 * if (newVS != null) { vsName = newVS.getName(); }
		 * 
		 * Cytoscape.getVisualMappingManager().setVisualStyle(vsName);
		 */

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		GridBagConstraints gridBagConstraints;

		pnlButtons = new javax.swing.JPanel();
		btnCollapseAsMetaNode = new javax.swing.JButton();
		btnCreateNetworkView = new javax.swing.JButton();

		resultPanel = new GroupViewerPanel();

		setLayout(new java.awt.GridBagLayout());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(resultPanel, gridBagConstraints);

		pnlButtons.setLayout(new java.awt.GridBagLayout());

		btnCollapseAsMetaNode.setText("collapse as metanode");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 20);
		pnlButtons.add(btnCollapseAsMetaNode, gridBagConstraints);

		btnCreateNetworkView.setText("Create Subnetwork");
		pnlButtons.add(btnCreateNetworkView, new java.awt.GridBagConstraints());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(pnlButtons, gridBagConstraints);

	}// </editor-fold>

	// Variables declaration - do not modify
	private javax.swing.JButton btnCollapseAsMetaNode;
	private javax.swing.JButton btnCreateNetworkView;
	private javax.swing.JPanel pnlButtons;

	private GroupViewerPanel resultPanel;

	// End of variables declaration

	/**
	 * This is called when the user changes the selection
	 * 
	 * @param e
	 *            the event that caused us to be called
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			CyNetwork network = Cytoscape.getCurrentNetwork();
			// Reset selection state
			network.unselectAllNodes();
			int selected = resultPanel.getTable().getSelectedRow();
			if (selected == -1){
				btnCollapseAsMetaNode.setEnabled(false);
				btnCreateNetworkView.setEnabled(false);
				return;
			}
			else {
				btnCollapseAsMetaNode.setEnabled(true);
				btnCreateNetworkView.setEnabled(true);
								
				
				CyGroup group = (CyGroup) resultPanel.getTable().getModel()
						.getValueAt(selected, 1);
				
				// Set button text based on the metaNode state
				// change the text of button, either as  "Expand metaNode" or "Collapse as metaNode"
				String buttonText = determineButtonText(group);
				btnCollapseAsMetaNode.setText(buttonText);
			}
			
			CyGroup group = (CyGroup) resultPanel.getTable().getModel()
					.getValueAt(selected, 1);
			network.setSelectedNodeState(group.getNodes(), true);
			Cytoscape.getCurrentNetworkView().updateView();
		}
	}
	

	private String determineButtonText(CyGroup pGroup){
		
		String retText = this.COLLAPSE_AS_MTEANODE;
		MetaNode theMetaNode = MetaNode.getMetaNode(pGroup);
		
		if (theMetaNode != null){
			// If the metaNode already existed, change the text
			//System.out.println("metaNode already existed");
			if (theMetaNode.isCollapsed(Cytoscape.getCurrentNetworkView())){
				retText = this.EXPAND_METANODE;				
			}
			else {
				retText = this.COLLAPSE_AS_MTEANODE;
			}
		}

		return retText;
	}
}
