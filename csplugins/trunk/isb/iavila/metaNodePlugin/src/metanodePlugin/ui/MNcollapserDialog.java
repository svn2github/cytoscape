/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
package metanodePlugin.ui;

import metanodePlugin.MetaNodeViewerCytoPlugin;
import org.isb.metanodes.actions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import org.isb.metanodes.data.*;

/**
 * A simple dialog for creating/destroying/collapsing/expanding meta-nodes.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org,
 *         iliana.avila@gmail.com
 * @since 2.0
 */

// TODO: Add a tab for meta-node attribute settings.

public class MNcollapserDialog extends JFrame {

	public static final String title = "Meta-Node Plugin " + MetaNodeViewerCytoPlugin.VERSION;

	protected JCheckBox recursiveCheckBox;

	protected JCheckBox multipleEdgesCheckBox;
	
	protected JCheckBox metaRelationshipEdgesCheckBox;

	protected JCheckBox useDefaultMetanodeSizerCheckBox;

	protected CollapseSelectedNodesAction createMetaNodeAction;

	protected CollapseSelectedNodesAction collapseAction;

	protected CollapseSelectedNodesAction collapseAllAction;

	protected UncollapseSelectedNodesAction expandAction;

	protected UncollapseSelectedNodesAction expandToNewAction;

	protected UncollapseSelectedNodesAction destroyMetaNodeAction;

	protected static final String CREATE_MN_TITLE = "Create Meta-Node";

	protected static final String DESTROY_MN_TITLE = "Destroy Meta-Node(s)";

	protected static final String COLLAPSE_MN_TITLE = "Collapse to Meta-Node(s)";

	protected static final String COLLAPSE_ALL_MN_TITLE = "Collapse all to Meta-Node(s)";

	protected static final String EXPAND_MN_TITLE = "Expand Children";

	protected static final String EXPAND_NEW_MN_TITLE = "Expand Children into New Network";

	protected boolean useDefaultMetanodeSizer = true;


	/**
	 * Constructor, sets these defaults: recursive = false, create multiple edges = true
	 */
	public MNcollapserDialog() {
		super(title);
		initialize();
		setRecursiveOperations(false);
		setMultipleEdges(true);
		setCreateMetaRelationshipEdges(true);
		setUseDefaultMetanodeSizer(true);
		AbstractMetaNodeMenu.setCollapserDialog(this);
	}// MNcollapserDialog

	/**
	 * Sets whether or not the operations (that apply) should be performed
	 * recursively or not, false by default.
	 */
	public void setRecursiveOperations(boolean recursive_operations) {
		if (this.recursiveCheckBox == null) {
			this.recursiveCheckBox = new JCheckBox(
					"Apply operations recursively");
		}
		this.recursiveCheckBox.setSelected(recursive_operations);
	}// setRecursiveOperations

	/**
	 * Sets whether or not multiple edges between meta-nodes and other nodes
	 * should be created
	 */
	public void setMultipleEdges(boolean multiple_edges) {
		if (this.multipleEdgesCheckBox == null) {
			this.multipleEdgesCheckBox = new JCheckBox("Create Multiple Edges");
		}
		this.multipleEdgesCheckBox.setSelected(multiple_edges);
	}// setMultipleEdges
	
	/**
	 * Whether or not edges between metanodes that have a shared member, and edges between parent metanodes and their
	 * children, should be created.
	 * 
	 * @param meta_edges
	 */
	public void setCreateMetaRelationshipEdges (boolean meta_edges){
		if(this.metaRelationshipEdgesCheckBox == null){
			this.metaRelationshipEdgesCheckBox = new JCheckBox("Create \"childOf\" and \"sharedChild\" edges");
		}
		this.metaRelationshipEdgesCheckBox.setSelected(meta_edges);
	}

	/**
	 * Whether or not metanodes are sized based on the sum of the area of their children
	 * 
	 * @param useDefault
	 */
	public void setUseDefaultMetanodeSizer (boolean useDefault){
		if(this.useDefaultMetanodeSizerCheckBox == null){
			this.useDefaultMetanodeSizerCheckBox = new JCheckBox("Use default metanode sizer");
			this.useDefaultMetanodeSizerCheckBox.setToolTipText("Set metanode size proportional to the area of all children");
		}
		this.useDefaultMetanodeSizerCheckBox.setSelected(useDefault);
	}
	
	/**
	 * @return whether or not the operations are to be performed recursively
	 */
	public boolean areOperationsRecursive() {
		return this.recursiveCheckBox.isSelected();
	}// areOperationsRecursive

	/**
	 * @return whether or not multiple edges between meta-nodes and other nodes
	 *         should be created
	 */
	public boolean getMultipleEdges() {
		return this.multipleEdgesCheckBox.isSelected();
	}// getMultipleEdges
	
	/**
	 * Whether or not metanodes are sized based on the sum of the area of their children
	 * 
	 * @return whether metanodes are using the default sizer
	 */
	public boolean getUseDefaultMetanodeSizer (){
		return this.useDefaultMetanodeSizerCheckBox.isSelected();
	}
	
	/**
	 * Whether or not edges between metanodes that have a shared member, and edges between parent metanodes and their
	 * children, should be created.
	 * 
	 * @return whether meta-relationship edges are being created
	 */
	public boolean getCreateMetaRelationshipEdges (){
		return this.metaRelationshipEdgesCheckBox.isSelected();
	}

	protected void initialize() {

		// Create the buttons and their tooltips
		JButton createMetaNodeButton = new JButton(CREATE_MN_TITLE);
		createMetaNodeButton
				.setToolTipText("Creates a new meta-node with selected nodes as its children and collapses it.");

		JButton destroyMetaNodeButton = new JButton(DESTROY_MN_TITLE);
		destroyMetaNodeButton
				.setToolTipText("Permanently removes the selected meta-nodes and expands them.");

		JButton collapseButton = new JButton(COLLAPSE_MN_TITLE);
		collapseButton
				.setToolTipText("Finds existing parent meta-nodes of selected nodes and collapses them.");

		JButton collapseAllButton = new JButton(COLLAPSE_ALL_MN_TITLE);
		collapseAllButton
				.setToolTipText("Finds all existing meta-nodes collapses them.");

		JButton expandButton = new JButton(EXPAND_MN_TITLE);
		expandButton
				.setToolTipText("Displays the children of selected meta-nodes.");

		JButton expandToNewButton = new JButton(EXPAND_NEW_MN_TITLE);
		expandToNewButton
				.setToolTipText("Displays the children of selected meta-nodes in new networks.");

		if (this.recursiveCheckBox == null) {
			this.recursiveCheckBox = new JCheckBox(
					"Apply operations recursively");
		}
		this.recursiveCheckBox
				.setToolTipText("Meta-nodes can have meta-nodes as children.");

		if (this.multipleEdgesCheckBox == null) {
			this.multipleEdgesCheckBox = new JCheckBox("Create Multiple Edges");
		}
		this.multipleEdgesCheckBox.setSelected(true);
		this.multipleEdgesCheckBox
				.setToolTipText("Multiple edges between meta-nodes and other nodes vs. single edges.");

		if(this.metaRelationshipEdgesCheckBox == null){
			this.metaRelationshipEdgesCheckBox = new JCheckBox("Create \"childOf\" and \"sharedMember\" edges");
		}
		this.metaRelationshipEdgesCheckBox.setSelected(true);
		this.metaRelationshipEdgesCheckBox.setToolTipText("<HTML>childOf: an edge between a child-node and its parent meta-node<br>"+
				"sharedMember:an edge between two meta-nodes that share a child node</HTML>");

		if(this.useDefaultMetanodeSizerCheckBox == null){
			this.useDefaultMetanodeSizerCheckBox = new JCheckBox("Use default metanode sizer");
		}
		this.useDefaultMetanodeSizerCheckBox.setSelected(true);
		this.useDefaultMetanodeSizerCheckBox.setToolTipText("Set metanode size proportional to the area of all children");
		
		// Set the desired MetaNodeAttributesHandler in ActionFactory.
		ActionFactory
				.setMetaNodeAttributesHandler(new AbstractMetaNodeAttsHandler());

		// Attach action listeners to the buttons
		this.createMetaNodeAction = (CollapseSelectedNodesAction) ActionFactory
				.createCollapseSelectedNodesAction(false, false,
						areOperationsRecursive(),getCreateMetaRelationshipEdges(), CREATE_MN_TITLE);
		createMetaNodeButton.addActionListener(this.createMetaNodeAction);

		this.destroyMetaNodeAction = (UncollapseSelectedNodesAction) ActionFactory
				.createUncollapseSelectedNodesAction(areOperationsRecursive(),
						false, false, DESTROY_MN_TITLE);
		destroyMetaNodeButton.addActionListener(this.destroyMetaNodeAction);

		this.collapseAction = (CollapseSelectedNodesAction) ActionFactory
				.createCollapseSelectedNodesAction(true, false,
						areOperationsRecursive(), getCreateMetaRelationshipEdges(), COLLAPSE_MN_TITLE);
		collapseButton.addActionListener(this.collapseAction);

		this.collapseAllAction = (CollapseSelectedNodesAction) ActionFactory
				.createCollapseSelectedNodesAction(true, true,
						areOperationsRecursive(), getCreateMetaRelationshipEdges(), COLLAPSE_ALL_MN_TITLE);
		collapseAllButton.addActionListener(this.collapseAllAction);

		this.expandAction = (UncollapseSelectedNodesAction) ActionFactory
				.createUncollapseSelectedNodesAction(areOperationsRecursive(),
						true, false, EXPAND_MN_TITLE);
		expandButton.addActionListener(this.expandAction);

		this.expandToNewAction = (UncollapseSelectedNodesAction) ActionFactory
				.createUncollapseSelectedNodesAction(areOperationsRecursive(),
						true, true, EXPAND_NEW_MN_TITLE);
		expandToNewButton.addActionListener(this.expandToNewAction);

		this.recursiveCheckBox.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				boolean recursive = MNcollapserDialog.this.recursiveCheckBox
						.isSelected();
				MNcollapserDialog.this.destroyMetaNodeAction
						.setRecursiveUncollapse(recursive);
				MNcollapserDialog.this.expandAction
						.setRecursiveUncollapse(recursive);
				MNcollapserDialog.this.collapseAction
						.setCollapseRecursively(recursive);
			}// actionPerformed
		}// AbstractAction
				);// addActionListener

		this.multipleEdgesCheckBox.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				boolean createMultipleEdges = MNcollapserDialog.this.multipleEdgesCheckBox
						.isSelected();
				MNcollapserDialog.this.collapseAction
						.setMultipleEdges(createMultipleEdges);
				MNcollapserDialog.this.createMetaNodeAction
						.setMultipleEdges(createMultipleEdges);
			}// actionPerformed
		}// AbstractAction

				);// addActionListener
		
		this.metaRelationshipEdgesCheckBox.addActionListener(new AbstractAction (){
			public void actionPerformed (ActionEvent event){
				boolean createMetaRelEdges = MNcollapserDialog.this.getCreateMetaRelationshipEdges();
				MNcollapserDialog.this.createMetaNodeAction.setCreateMetaRelationshipEdges(createMetaRelEdges);
				MNcollapserDialog.this.collapseAction.setCreateMetaRelationshipEdges(createMetaRelEdges);
			}
		});
		
		this.useDefaultMetanodeSizerCheckBox.addActionListener(new AbstractAction (){
			public void actionPerformed (ActionEvent event){
				boolean useDefaultSizer = MNcollapserDialog.this.getUseDefaultMetanodeSizer();
				MNcollapserDialog.this.collapseAction.setUseDefaultMetanodeSizer(useDefaultSizer);
				MNcollapserDialog.this.createMetaNodeAction.setUseDefaultMetanodeSizer(useDefaultSizer);
			}
		});

		// Layout of dialog
		
		JPanel cdPanel = new JPanel();
		cdPanel.setBorder(BorderFactory.createTitledBorder("Create/Destroy Operations"));
		cdPanel.setLayout(new GridLayout(2,1));
		cdPanel.add(createMetaNodeButton);
		cdPanel.add(destroyMetaNodeButton);
		
		JPanel collPanel = new JPanel();
		collPanel.setBorder(BorderFactory.createTitledBorder("Collapse Operations"));
		collPanel.setLayout(new GridLayout(2,1));
		collPanel.add(collapseButton);
		collPanel.add(collapseAllButton);
		
		JPanel expandPanel = new JPanel();
		expandPanel.setBorder(BorderFactory.createTitledBorder("Expand Operations"));
		expandPanel.setLayout(new GridLayout(2,1));
		expandPanel.add(expandButton);
		expandPanel.add(expandToNewButton);
		
		JPanel opsPanel = new JPanel();
		opsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
		opsPanel.setLayout(new GridLayout(4,1));
		opsPanel.add(this.recursiveCheckBox);
		opsPanel.add(this.multipleEdgesCheckBox);
		opsPanel.add(this.metaRelationshipEdgesCheckBox);
		opsPanel.add(this.useDefaultMetanodeSizerCheckBox);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton closeWinButton = new JButton("Close");
		closeWinButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				MNcollapserDialog.this.dispose();
			}
		});
		buttonsPanel.add(closeWinButton);

		JPanel allPanels = new JPanel();
		allPanels.setLayout(new GridLayout(3,1));
		allPanels.add(cdPanel);
		allPanels.add(collPanel);
		allPanels.add(expandPanel);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(allPanels,BorderLayout.NORTH);
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel,BoxLayout.Y_AXIS));
		southPanel.add(opsPanel);
		southPanel.add(buttonsPanel);
		mainPanel.add(southPanel,BorderLayout.SOUTH);
		
		setContentPane(mainPanel);
		

	}// initialize

	public AbstractAction getCreateMetaNodeAction() {
		return this.createMetaNodeAction;
	}// getCreateMetaNodeAction

	public AbstractAction getDestroyMetaNodeAction() {
		return this.destroyMetaNodeAction;
	}// getDestroyMetaNodeAction

	public AbstractAction getCollapseMetaNodesAction() {
		return this.collapseAction;
	}// getCollapseMetaNodesAction

	public AbstractAction getCollapseAllMetaNodesAction() {
		return this.collapseAllAction;
	}// getCollapseAllMetaNodesAction

	public AbstractAction getExpandChildrenAction() {
		return this.expandAction;
	}// getExpandChildrenAction

	public AbstractAction getExpandToNewAction() {
		return this.expandToNewAction;
	}// getExpandToNewAction

	/**
	 * @return a JMenu with operations to create new meta-nodes, destroy
	 *         meta-nodes, collapse to meta-nodes and expand children.
	 */
	public JMenu getMenu(Object nodeView) {
		JMenu menu = new JMenu("Meta-Node Operations");
		menu.add(this.createMetaNodeAction);
		menu.add(this.destroyMetaNodeAction);
		menu.add(this.collapseAction);
		menu.add(this.expandAction);
		menu.add(this.expandToNewAction);
		return menu;
	}// getMenu
}// class MNcollapserDialog
