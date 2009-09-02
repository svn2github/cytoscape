/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package structureViz.ui;

// System imports
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.Position;
import javax.swing.WindowConstants.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.*;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import giny.view.NodeView;

// StructureViz imports
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraTreeModel;
import structureViz.model.Structure;
import structureViz.actions.CyChimera;
import structureViz.actions.Chimera;
import structureViz.ui.PopupMenuListener;

/**
 * The ModelNavigatorDialog class is the class that implements the main interface for
 * structureViz.
 */
public class ModelNavigatorDialog 
			extends JDialog 
			implements TreeSelectionListener, TreeExpansionListener, TreeWillExpandListener {

	private Chimera chimeraObject;
	private boolean status;
	// These must be > ChimeraResidue.FULL_NAME
	private static final int COMMAND = 10;
	private static final int EXIT = 11;
	private static final int REFRESH = 12;
	private static final int CLEAR = 13;
	private static final int ALIGNBYMODEL = 14;
	private static final int ALIGNBYCHAIN = 15;
	private static final int FINDCLASH = 16;
	private boolean ignoreSelection = false;
	private int residueDisplay = ChimeraResidue.THREE_LETTER;
	private boolean isCollapsing = false;
	private TreePath collapsingPath = null;
	private boolean isExpanding = false;
	private ArrayList selectedObjects = null;

	// Dialog components
	private JLabel titleLabel;
	private JTree navigationTree;
	private ChimeraTreeModel treeModel;
	private JMenu alignMenu;

	/**
	 * Create a new ModelNavigatorDialog.
	 *
	 * @param parent the Frame that acts as a parent for the dialog
	 * @param object the Chimera interface object associated with
	 * this dialog
	 */
	public ModelNavigatorDialog (Frame parent, Chimera object) {
		// super(parent, false);
		super(Cytoscape.getDesktop(), "Cytoscape Molecular Structure Navigator");
		chimeraObject = object;
		initComponents();
		status = false;
		selectedObjects = new ArrayList();
	}

	/**
	 * Call this method when something significant has changed in the model
	 * such as a new model opened or closed
	 */
	public void modelChanged() {
		// Something significant changed in the model (new open/closed structure?)
		ignoreSelection = true;
		treeModel.reload();
		int modelCount = chimeraObject.getChimeraModels().size();
		if (modelCount > 1)
			alignMenu.setEnabled(true);
		else
			alignMenu.setEnabled(false);
		// Re-select the paths
		selectedObjects.clear();
		chimeraObject.updateSelection();
		ignoreSelection = false;
	}

	/**
	 * This method is called when a tree is expanded
	 *
	 * @param e the TreeExpansionEvent
	 */
	public void treeExpanded(TreeExpansionEvent e) {
		TreePath ePath = e.getPath();
		// Get the path we are expanding
		DefaultMutableTreeNode node = 
			(DefaultMutableTreeNode)ePath.getLastPathComponent();
		ChimeraStructuralObject nodeInfo = 
			(ChimeraStructuralObject)node.getUserObject();
		// Check and see if our object is selected
		if (!nodeInfo.isSelected()) {
			// Its not -- deselect
			navigationTree.removeSelectionPath(ePath);
		}
		// Get the selected children of that path
		List<ChimeraStructuralObject>children = nodeInfo.getChildren();
		// Add them to our selection
		if (children != null) {
			for (ChimeraStructuralObject o: children) {
				if (o.isSelected()) {
					TreePath path = (TreePath)o.getUserData();
					navigationTree.addSelectionPath(path);
				}
			}
		}
	}

	/**
	 * This method is called when a tree is collapsed
	 *
	 * @param e the TreeExpansionEvent
	 */
	public void treeCollapsed(TreeExpansionEvent e) {
		// Sort of a hack.  By default when a tree is collapsed, 
		// the selection passes to the parent.  We don't what to 
		// do that, because it prevents us from remembering
		// our residue selections, which may be important.  
		// So, we need to set a flag.

		// Get the path we are collapsing
		collapsingPath = e.getPath();
		DefaultMutableTreeNode node = 
			(DefaultMutableTreeNode)collapsingPath.getLastPathComponent();
		ChimeraStructuralObject nodeInfo = 
			(ChimeraStructuralObject)node.getUserObject();

		// Is the object we're collapsing already selected?
		if (!nodeInfo.isSelected()) {
			// No, see if it has selected children
			if (hasSelectedChildren(nodeInfo)) {
				// It does, we need to disable selection
				isCollapsing = true;
			}
		}
	}

	/**
	 * This method is called before the tree is actually collapsed
	 *
	 * @param e the TreeExpansionEvent
	 */
	public void treeWillCollapse(TreeExpansionEvent e) 
			throws ExpandVetoException {
		TreePath path = e.getPath();
		DefaultMutableTreeNode node = 
			(DefaultMutableTreeNode) path.getLastPathComponent();
		if (!ChimeraStructuralObject.class.isInstance(node.getUserObject())) 
			throw new ExpandVetoException(e);
		return;
	}

	/**
	 * This method is called before the tree is actually expanded
	 *
	 * @param e the TreeExpansionEvent
	 */
	public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
		return;
	}

	/**
	 * This method is called when a path in the tree is selected or deselected
	 *
	 * @param e the TreeSelectionsionEvent
	 */
	public void valueChanged(TreeSelectionEvent e) {

		// System.out.println("TreeSelectionEvent: "+e);

		// Get the paths that are changing
		TreePath[] cPaths = e.getPaths();
		if (cPaths == null) return;

		if (isCollapsing) {
			// System.out.println("  isCollapsing: "+cPaths[0]);
			if (cPaths[0] == collapsingPath) {
				isCollapsing = false;
				navigationTree.removeSelectionPath(collapsingPath);
			}
			return;
		}	

		for (int i = 0; i < cPaths.length; i++) {
			DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode) cPaths[i].getLastPathComponent();
			if (!ChimeraStructuralObject.class.isInstance(node.getUserObject())) 
				continue;
			ChimeraStructuralObject nodeInfo = 
					(ChimeraStructuralObject)node.getUserObject();
			if (!e.isAddedPath(cPaths[i])) {
				nodeInfo.setSelected(false);
				selectedObjects.remove(nodeInfo);
			} else {
				if (!selectedObjects.contains(nodeInfo))
					selectedObjects.add(nodeInfo);
			}
			// System.out.println("  Path: "+((DefaultMutableTreeNode) cPaths[i].getLastPathComponent()));
		}

		String selSpec = "sel ";
		boolean selected = false;
		HashMap modelsToSelect = new HashMap();

		for (int i = 0; i < selectedObjects.size(); i++) {
			ChimeraStructuralObject nodeInfo = 
					(ChimeraStructuralObject) selectedObjects.get(i);
			nodeInfo.setSelected(true);
			selected = true;
			ChimeraModel model = nodeInfo.getChimeraModel();
			selSpec = selSpec.concat(nodeInfo.toSpec());
			modelsToSelect.put(model,model);
			if (i < selectedObjects.size()-1) selSpec.concat("|");
			// Add the model to be selected (if it's not already)
		}
		if (!ignoreSelection && selected)
			chimeraObject.select(selSpec);
		else if (!ignoreSelection && selectedObjects.size() == 0) {
			chimeraObject.select("~sel");
		}

		CyChimera.selectCytoscapeNodes(chimeraObject.getNetworkView(), 
																		modelsToSelect, 
												 						chimeraObject.getChimeraModels());
	}

	/**
	 * This method is called to update the selected items in the navigation
	 * tree.
	 *
	 * @param selectionList the List of ChimeraStructuralObjects to be selected
	 */
	public void updateSelection(List<ChimeraStructuralObject> selectionList) {
		TreePath path = null;
		this.ignoreSelection = true;
		clearSelectionState();
		navigationTree.clearSelection();
		// Need to clear currently selected objects
		for (ChimeraStructuralObject selectedObject: selectionList) {
			path = (TreePath)selectedObject.getUserData();
			navigationTree.addSelectionPath(path);
			navigationTree.makeVisible(path);
		}
		int row = navigationTree.getMaxSelectionRow();
		navigationTree.scrollRowToVisible(row);
		this.ignoreSelection = false;
	}

	/**
	 * This method is called to clear the selection state of
	 * all ChimeraStructuralObjects.  It is accomplished by
	 * iterating over all models and recursively decending
	 * through the chains to the residues.
	 */
	private void clearSelectionState() {
		selectedObjects.clear();
		List<ChimeraModel>models = chimeraObject.getChimeraModels();
		if (models == null) return;
		for (ChimeraModel m: models) {
			m.setSelected(false);
			Collection<ChimeraChain>chains = m.getChains();
			if (chains == null) continue;
			for (ChimeraChain c: chains) {
				c.setSelected(false);
				Collection<ChimeraResidue>residues = c.getResidues();
				if (residues == null ) continue;
				for (ChimeraResidue r: residues) {
					if (r != null) r.setSelected(false);
				}
			}
		}
	}

	/**
	 * This method determines if a given ChimeraStructuralObject has
	 * any selected children.
	 *
	 * @param obj the ChimeraStructuralObject to test
	 * @return "true" if <b>obj</b> is selected itself or has 
	 * any selected children
	 */
	public boolean hasSelectedChildren(ChimeraStructuralObject obj) {
		if (obj.isSelected()) {
			return true;
		}
		if (obj.getClass() == ChimeraResidue.class)
			return false;

		for (ChimeraStructuralObject child:(List<ChimeraStructuralObject>)obj.getChildren()) {
			if (hasSelectedChildren(child))
				return true;
		}
		return false;
	}

	/*************************************************
	 *                Private methods                *
	 *************************************************/

	/**
	 * This method initializes all of the graphical components
	 * in the dialog.
	 */
	private void initComponents() {
		int modelCount = chimeraObject.getChimeraModels().size();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Initialize the menus
		JMenuBar menuBar = new JMenuBar();

		// Chimera menu
		JMenu chimeraMenu = new JMenu("Chimera");
		alignMenu = new JMenu("Align Structures");
		{
			// Should this be "model vs. model", "chain vs. chain", and "chain vs. model"?
			addMenuItem(alignMenu, "by model", ALIGNBYMODEL, null);
			addMenuItem(alignMenu, "by chain", ALIGNBYCHAIN, null);
		}

		int totalModels = modelCount;
		for (ChimeraModel model: chimeraObject.getChimeraModels()) {
			Structure s = model.getStructure();
			if (s != null && s.getType() == Structure.StructureType.SMILES) totalModels--;
		}

		if (totalModels > 1)
			alignMenu.setEnabled(true);
		else
			alignMenu.setEnabled(false);
		chimeraMenu.add(alignMenu);

		JMenu clashMenu = new JMenu("Clash Detection");
		addMenuItem(clashMenu, "Find Clashes", FINDCLASH, "findclash sel continuous true");
		// addMenuItem(clashMenu, "Find Clashes", FINDCLASH, "findclash sel");
		addMenuItem(clashMenu, "Clear Clash", COMMAND, "~findclash");
		chimeraMenu.add(clashMenu);

		JMenu presetMenu = new JMenu("Presets");
		if (buildPresetMenu(presetMenu))
			chimeraMenu.add(presetMenu);

		addMenuItem(chimeraMenu, "Exit", EXIT, null);
		menuBar.add(chimeraMenu);

		// View menu
		JMenu viewMenu = new JMenu("View");
		addMenuItem(viewMenu, "Refresh", REFRESH, null);
		addMenuItem(viewMenu, "Refocus", COMMAND, "focus");

		JMenu viewResidues = new JMenu("Residues as..");
		addMenuItem(viewResidues, "single letter", 
								ChimeraResidue.SINGLE_LETTER, null);
		addMenuItem(viewResidues, "three letters", 
								ChimeraResidue.THREE_LETTER, null);
		addMenuItem(viewResidues, "full name", 
								ChimeraResidue.FULL_NAME, null);
		viewMenu.add(viewResidues);
		menuBar.add(viewMenu);

		// Select menu
		JMenu selectMenu = new JMenu("Select");
		addMenuItem(selectMenu, "Protein", COMMAND, "select protein");
		addMenuItem(selectMenu, "Nucleic Acid", COMMAND, "select nucleic acid");
		addMenuItem(selectMenu, "Ligand", COMMAND, "select ligand");
		addMenuItem(selectMenu, "Ions", COMMAND, "select ions");
		addMenuItem(selectMenu, "Solvent", COMMAND, "select solvent");
		JMenu secondaryMenu = new JMenu("Secondary Structure");
		addMenuItem(secondaryMenu, "Helix", COMMAND, "select helix");
		addMenuItem(secondaryMenu, "Strand", COMMAND, "select strand");
		addMenuItem(secondaryMenu, "Turn", COMMAND, "select turn");
		selectMenu.add(secondaryMenu);
		addMenuItem(selectMenu, "Invert selection", COMMAND, "select invert");
		addMenuItem(selectMenu, "Clear selection", CLEAR, null);
		menuBar.add(selectMenu);

		setJMenuBar(menuBar);

		// Initialize the tree
		navigationTree = new JTree();
		treeModel = new ChimeraTreeModel(chimeraObject, navigationTree);

		navigationTree.setModel(treeModel);
		navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		navigationTree.addTreeSelectionListener(this);
		navigationTree.addTreeExpansionListener(this);
		navigationTree.addTreeWillExpandListener(this);
		navigationTree.setShowsRootHandles(false);

		navigationTree.setCellRenderer(new ObjectRenderer());

		navigationTree.addMouseListener(new PopupMenuListener(chimeraObject, navigationTree));

		JScrollPane treeView = new JScrollPane(navigationTree);

		setContentPane(treeView);
	}

	/**
	 * Add a menu item to a menu
	 *
	 * @param menu the menu to add the item to
	 * @param label the label for the menu
	 * @param type the command type
	 * @param command the command to execute when this item is selected
	 * @return the JMenuItem that was created
	 */
	private JMenuItem addMenuItem (JMenu menu, String label, 
																	int type, String command) {
		JMenuItem menuItem = new JMenuItem(label);
		{
			MenuActionListener va = new MenuActionListener(this, type, command);
			menuItem.addActionListener(va);
		}
		menu.add(menuItem);
		return menuItem;
	}

	/**
	 * Build the preset menu by inquiring the list of available presets
	 * from Chimera
	 *
	 * @param menu the menu to add the preset items to
	 * @return false if there are no presets
	 */
	private boolean buildPresetMenu(JMenu menu) {
		List<String>presetList = chimeraObject.getPresets();
		if (presetList == null || presetList.size() == 0)
			return false;

		// OK, now we have the list, create the menu list
		for (String label: presetList) {
			String [] com = label.split("[(]");
			JMenuItem menuItem = new JMenuItem(label);
			{
				String command = "preset apply "+com[0];
				MenuActionListener va = new MenuActionListener(this, COMMAND, command);
				menuItem.addActionListener(va);
			}
			menu.add(menuItem);
		}
		return true;
	}

	// Embedded classes

	/**
 	 * The MenuActionListener class is used to attach a listener
	 * to menu items to handle commands
	 */
	private class MenuActionListener extends AbstractAction {
		int type;
		String command = null;
		ModelNavigatorDialog dialog;

		/**
	 	 * Create a new MenuActionListener
		 *
		 * @param type the listener type (COMMAND, CLEAR, EXIT, REFRESH, ALIGN,
		 * or the residue display type
		 * @param command the command to execute when this menu is selected
		 */
		public MenuActionListener (ModelNavigatorDialog dialog, int type, String command) { 
			this.type = type; 
			this.command = command;
			this.dialog = dialog;
		}

		/**
		 * This method is called when a menu item is selected
		 *
		 * @param ev the ActionEvent for this
		 */
		public void actionPerformed(ActionEvent ev) {
			if (type == COMMAND) {
				// System.out.println("Command: "+command);
				chimeraObject.select(command);
			} else if (type == CLEAR) {
				chimeraObject.select("~select");
				navigationTree.clearSelection();
				clearSelectionState();
			} else if (type == EXIT) {
				chimeraObject.exit();
				setVisible(false);
				if (chimeraObject.getAlignDialog() != null)
					chimeraObject.getAlignDialog().setVisible(false);
				return;
			} else if (type == REFRESH) {
				chimeraObject.refresh();
			} else if (type == ALIGNBYMODEL) {
				launchAlignDialog(false);
			} else if (type == ALIGNBYCHAIN) {
				launchAlignDialog(true);
			} else if (type == FINDCLASH) {
				if (selectedObjects.size() > 0) {
					chimeraObject.select(command);
				} else {
 					JOptionPane.showMessageDialog(dialog, "You must select something to find classes", 
					                              "Nothing Selected", JOptionPane.ERROR_MESSAGE); 
				}
			} else {
				residueDisplay = type;
				treeModel.setResidueDisplay(type);
			}
			modelChanged();
		}

		/**
		 * Create and instantiate the align dialog
		 */
		private void launchAlignDialog(boolean useChains)
		{
			AlignStructuresDialog alDialog;
			if (chimeraObject.getAlignDialog() != null) {
				alDialog = chimeraObject.getAlignDialog();
				alDialog.setVisible(false);
				alDialog.dispose();
			}
			List objectList = new ArrayList();
			if (!useChains) {
				for (ChimeraModel model: chimeraObject.getChimeraModels()) {
					objectList.add(model.getStructure());
				}		
			} else {
				for (ChimeraModel model: chimeraObject.getChimeraModels()) {
					for (ChimeraChain chain: model.getChildren()) {
						objectList.add(chain);
					}
				}
			}
			// Bring up the dialog
			alDialog = new AlignStructuresDialog(chimeraObject.getDialog(), 
																					 chimeraObject, objectList);
			alDialog.pack();
			alDialog.setVisible(true);
			chimeraObject.setAlignDialog(alDialog);
		}
	}

	/**
	 * The ObjectRenderer class is used to provide special rendering
	 * capabilities for each row of the tree.  We use this to provide
	 * a colored border around the model rows of the tree that matches
	 * the Chimera model color and to set the color of collapsed models
	 * and chains that have selected children.
	 */
	private class ObjectRenderer extends DefaultTreeCellRenderer {

		/**
		 * Create a new ObjectRenderer
		 */
		public ObjectRenderer() {
		}

		/**
		 * This is the method actually called to render the tree cell
		 *
		 * @see DefaultTreeCellRenderer
		 */
		public Component getTreeCellRendererComponent( JTree tree, Object value,
																									boolean sel, boolean expanded,
																									boolean leaf, int row, 
																									boolean hasFocus) 
		{
			ChimeraStructuralObject chimeraObj = null;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object object =  node.getUserObject();
			Class objClass = object.getClass();
			boolean selectIt = sel;

			// Is this a Chimera class?
			if (ChimeraStructuralObject.class.isInstance(object)) {
				// Yes, get the object
				chimeraObj = (ChimeraStructuralObject)object;
				if (selectIt && !chimeraObj.isSelected())
					selectIt = false;
			} else {
				// No, we don't want to select the root
				selectIt = false;
			}

			// Call the DefaultTreeCellRender's method to do most of the work
			super.getTreeCellRendererComponent(tree, value.toString(), selectIt,
                            						 expanded, leaf, row,
                            						 hasFocus);

			// Initialize our border setting
			setBorder(null);
			if (chimeraObj != null) {
				// System.out.println("Sel = "+sel+", "+chimeraObj+".isSelected = "+chimeraObj.isSelected());
				// Finally, if we're selected, but the underlying object
				// isn't selected, change the background paint
				if (sel == false && 
						hasSelectedChildren(chimeraObj) && 
						expanded == false) {
					Color bg = Color.blue;
					setForeground(bg);
				}
				// If we're a model, use the model color as a border
				if (chimeraObj.getClass() == ChimeraModel.class) {
					Color color = ((ChimeraModel)object).getModelColor();
					if (color != null) {
						Border border = new LineBorder(color);
						setBorder(border);
					}
				}
			} 

			return this;
		}
	}
}

