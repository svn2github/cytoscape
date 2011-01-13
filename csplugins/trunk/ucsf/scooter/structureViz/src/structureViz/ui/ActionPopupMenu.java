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
import java.util.Iterator;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.WindowConstants.*;

import java.awt.*;
import java.awt.event.*;

import structureViz.model.ChimeraStructuralObject;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraChain;
import structureViz.model.Structure;

import structureViz.actions.Chimera;

/**
 * This class implements all of the actions for the popup menu as well
 * as providing the MouseListener itself. This is used to implement the
 * context menu in the ModelNavigatorDialog.
 */

public class ActionPopupMenu extends JPopupMenu {
	private Chimera chimeraObject;
	private List<ChimeraModel> modelList;
	private List<ChimeraChain> chainList;
	private List<ChimeraResidue> residueList;
	private List<ChimeraStructuralObject> objectList;
	private JTree navTree;
	private int context = GENERIC_CONTEXT;

	public static final int GENERIC_CONTEXT = 1;
	public static final int MODEL_CONTEXT = 2;
	public static final int CHAIN_CONTEXT = 3;
	public static final int RESIDUE_CONTEXT = 4;

	/**
	 * Create a new ActionPopupMenu
	 *
	 * @param co the Chimera interface object
	 * @param tree the JTree we are adding the context menu to
	 * @param models the List of selected ChimeraModels
	 * @param chains the List of selected ChimeraChains
	 * @param residues the List of selected ChimeraResidues
	 * @param context the context of the selection
	 */
	public ActionPopupMenu (Chimera co, JTree tree, List models,
													List chains, List residues, int context) 
	{
		super("Chimera Actions");
		this.chimeraObject = co;
		this.navTree = tree;
		this.modelList = models;
		this.chainList = chains;
		this.residueList = residues;

		// Create a global list
		this.objectList = new ArrayList();
		objectList.addAll(modelList);
		objectList.addAll(chainList);
		objectList.addAll(residueList);
		this.context = context;

		createGenericMenu();
		if (modelList.size() > 0) {
			createModelMenu();
		}
		if (chainList.size() > 0) {
			createChainMenu();
		}
		if (residueList.size() > 0) {
			createResidueMenu();
		}
	}

	/**
	 * Create the Generic context menu
	 */
	private void createGenericMenu() {
		addHeader("Generic Actions");
		JMenu submenu = null;
		JMenu sub2 = null;
		JMenuItem item = null;

		// Show
		submenu = new JMenu("Show");
		{
			addItem(submenu, "All", "disp %sel",0);
			addItem(submenu, "Backbone only", "~disp %sel; disp %sel & @n,ca,c",0);
			if (modelList.size() > 0 || chainList.size() > 0) {
				addItem(submenu, "Sequence", "sequence %sel",0);
			}
		}
		add(submenu);
		// Hide
		addItem(null, "Hide", "~disp %sel",0);
		// Focus
		addItem(null, "Focus", "focus %sel",0);
		// Color
		submenu = new JMenu("Color");
		{
			JMenuItem rainbow = addItem(submenu, "Rainbow by chain", "rainbow chain %sel",0);
			if (modelList.size() == 0) {
				rainbow.setEnabled(false);
			}
			rainbow = addItem(submenu, "Rainbow by residue", "rainbow residue %sel",0);
			if (modelList.size() == 0 && chainList.size() == 0) {
				rainbow.setEnabled(false);
			}
			addColorMenu(submenu, "Atoms/Bonds", "color ", ",a %sel", true);
			addColorMenu(submenu, "Ribbons", "color ", ",r %sel", false);
			addColorMenu(submenu, "Surfaces", "color ", ",s %sel", false);
			addColorMenu(submenu, "Labels", "color ", ",l %sel", false);
		}
		add(submenu);
		// Depict
		submenu = new JMenu("Depict");
		{
			addItem(submenu, "Wire", "repr wire %sel",0);
			addItem(submenu, "Stick", "repr stick %sel",0);
			addItem(submenu, "Ball & Stick", "repr bs %sel",0);
			addItem(submenu, "Sphere", "repr cpk %sel",0);
			addItem(submenu, "CPK", "repr cpk %sel",0);
			sub2 = new JMenu("Ribbon");
			{
				addItem(sub2, "Hide", "~ribbon %sel",0);
				addItem(sub2, "Flat", "ribrepr flat %sel;ribbon %sel",0);
				addItem(sub2, "Edged", "ribrepr edged %sel;ribbon %sel",0);
				addItem(sub2, "Round", "ribrepr round %sel;ribbon %sel",0);
			}
			submenu.add(sub2);
			sub2 = new JMenu("Surface");
			{
				addItem(sub2, "Hide", "~surface %sel",0);
				addItem(sub2, "Solid", "surface %sel;surfrepr solid",0);
				addItem(sub2, "Mesh", "surface %sel;surfrepr mesh",0);
				addItem(sub2, "Dot", "surface %sel;surfrepr dot",0);
				JMenu sub3 = new JMenu("Transparency");
				{
					addItem(sub3, "0%", "surftransparency 0 %sel",0);
					addItem(sub3, "10%", "surftransparency 10 %sel",0);
					addItem(sub3, "20%", "surftransparency 20 %sel",0);
					addItem(sub3, "30%", "surftransparency 30 %sel",0);
					addItem(sub3, "40%", "surftransparency 40 %sel",0);
					addItem(sub3, "50%", "surftransparency 50 %sel",0);
					addItem(sub3, "60%", "surftransparency 60 %sel",0);
					addItem(sub3, "70%", "surftransparency 70 %sel",0);
					addItem(sub3, "80%", "surftransparency 80 %sel",0);
					addItem(sub3, "90%", "surftransparency 90 %sel",0);
				}
				sub2.add(sub3);
			}
			submenu.add(sub2);
		}
		add(submenu);
		// Label
		submenu = new JMenu("Label");
		{
			addItem(submenu, "Hide", "~label %sel;~rlabel %sel",0);
			addItem(submenu, "Atom name", "labelopt info name; label %sel",0);
			addItem(submenu, "Element", "labelopt info element; label %sel",0);
			addItem(submenu, "IDATM type", "labelopt info idatmType; label %sel",0);
			addItem(submenu, "Residue", "rlabel %sel",0);
		}
		add(submenu);
		// Clear Selection
		addItem(null, "Clear selection", "~select", PopupActionListener.CLEAR_SELECTION);
		add (new JSeparator());
		addItem(null, "Delete selection", "listen stop select; delete %sel; listen start select", PopupActionListener.DELETE);
	}

	/**
	 * Create the model menu
	 */
	private void createModelMenu() { 
		addHeader("Model Actions");
		addItem(null, "Close model(s)", null,PopupActionListener.CLOSE);
		JMenu selectMenu = new JMenu("Select");
		addItem(selectMenu, "Protein", "select %sel & protein", PopupActionListener.MODEL_SELECTION);
		addItem(selectMenu, "Ligand", "select %sel & ligand", PopupActionListener.MODEL_SELECTION);
		addItem(selectMenu, "Ions", "select %sel & ions", PopupActionListener.MODEL_SELECTION);
		addItem(selectMenu, "Solvent", "select %sel & solvent", PopupActionListener.MODEL_SELECTION);
		addItem(selectMenu, "Functional Residues", null, PopupActionListener.FUNCTIONAL_RESIDUES);
		JMenu secondaryMenu = new JMenu("Secondary Structure");
		addItem(secondaryMenu, "Helix", "select %sel & helix", PopupActionListener.MODEL_SELECTION);
		addItem(secondaryMenu, "Strand", "select %sel & strand", PopupActionListener.MODEL_SELECTION);
		addItem(secondaryMenu, "Coil", "select %sel & coil", PopupActionListener.MODEL_SELECTION);
		selectMenu.add(secondaryMenu);
		add(selectMenu);
		return; 
	}

	/**
	 * Create the chain menu (at this point, there aren't any special chain menu items)
	 */
	private void createChainMenu() { 
		return; 
	}

	/**
	 * Create the residue menu (at this point, there aren't any special residue menu items)
	 */
	private void createResidueMenu() { return; }

	/**
	 * Add a header to the menu
	 *
	 * @param header the String to use as the header
	 */
	private void addHeader(String header) {
		JMenuItem item = new JMenuItem(header);
		item.setBackground(Color.gray);
		item.setForeground(Color.white);
		add(item);
	}

	/**
	 * Add a menu item to a menu
	 *
	 * @param menu the menu to add this item to
	 * @param text the label for the menu item
	 * @param command the command to execute when selected
	 * @param postCommand a flag to indicate which post command to process (if any)
	 * @return the JMenuItem that was created
	 */
	private JMenuItem addItem(JMenu menu, String text, String command, int postCommand) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(new PopupActionListener(command,postCommand));
		if (menu == null)
			this.add(item);
		else
			menu.add(item);
		return item;
	}

	/**
	 * Add a color menu to a menu.  This is essentially a special version of addMenuItem that
	 * puts up a color list to choose from.
	 *
	 * @param menu the menu to add the color list to
	 * @param text the name of the item in the menu
	 * @param prefix the prefix to use to pass this command to Chimera
	 * @param suffix the suffix to use to pass this command to Chimera
	 * @param addByElement add the two atom-specific coloring options
	 */
	private JMenuItem addColorMenu(JMenu menu, String text, String prefix, String suffix, boolean addByElement) {
		String[] colorList = {"red", "orange red", "orange", "yellow", "green", "forest green",
													"cyan", "light sea green", "blue", "cornflower blue", "medium blue",
													"purple", "hot pink", "magenta", "white", "light gray", "gray",
													"dark gray", "dim gray", "black"};
		int[] rgb = {-0xff0000, 0xff4500, 0xff7f00, 0xffff00, 0x00ff00, -0x228b22, 
								 0x00ffff, 0x20b2aa, -0x0000ff, 0x6495ed, -0x3232cd, 
								 0xa020f0, 0xff69b4, 0xff00ff, 0xffffff, 0xd3d3d3, 0xbebebe,
								 0xa9a9a9, -0x696969, 0x000000};

		String[] colorListAll = {"aquamarine", "black", "blue", "brown", "chartreuse", "coral",
													"cornflower blue", "cyan", "dark cyan", "dark gray", "dark green",
													"dark khaki", "dark magenta", "dark olive green", "dark red", 
													"dark slate blue", "dark slate gray", "deep pink", "deep sky blue",
													"dim gray", "dodger blue", "firebrick", "forest green", "gold",
													"goldenrod", "gray", "green", "hot pink", "khaki", "light blue",
													"light gray", "light green", "light sea green", "lime green",
													"magenta", "medium blue", "medium purple", "navy blue", "olive drab",
													"orange red", "orange", "orchid", "pink", "plum", "purple",
													"red", "rosy brown", "salmon", "sandy brown", "sea green",
													"sienna", "sky blue", "slate gray", "spring green", "steel blue", 
													"tan", "turquoise", "violet red", "white", "yellow"};
		JMenu colorMenu = new JMenu(text);
		JMenuItem colorItem;
		if (addByElement) {
			addItem(colorMenu, "By element", "color byelement %sel",0);
			addItem(colorMenu, "By heteroatom", "color byhetero %sel",0);
		}
		for (int color=0; color < colorList.length; color++) {
			colorItem = addItem(colorMenu, colorList[color], prefix+colorList[color]+suffix,0);
			if (rgb[color] > 0) {
				colorItem.setForeground(Color.BLACK);
				colorItem.setBackground(new Color(rgb[color]));
			} else {
				colorItem.setForeground(Color.WHITE);
				colorItem.setBackground(new Color(-rgb[color]));
			}
		}
		menu.add(colorMenu);
		return colorMenu;
	}

	/**
	 * This ActionListener is used to listen for commands selected from the 
	 * ModelNavigator popup menu
	 */
	private class PopupActionListener implements ActionListener {
		String[] commandList;

		/**
		 * The post processing commands
		 */

		/**
	 	 * No post processing
		 */
		public static final int NO_POST = 0;

		/**
	 	 * Clear the selection after executing the command
		 */
		public static final int CLEAR_SELECTION = 1;

		/**
	 	 * Close the selected model after executing the command
		 */
		public static final int CLOSE = 2;

		/**
	 	 * Special post processing for model selection
		 */
		public static final int MODEL_SELECTION = 3;

		/**
	 	 * Special post processing for functional residue selection
		 */
		public static final int FUNCTIONAL_RESIDUES = 4;

		/**
	 	 * Deleted the selected model after executing the command
		 */
		public static final int DELETE = 5;
		int postCommand = NO_POST;

		/**
		 * Create an ActionListener with only the command to execute
		 *
		 * @param command the command to execute
		 */
		PopupActionListener (String command) {
			commandList = command.split("\n");
			if (commandList.length == 0) {
				commandList = new String[1];
				commandList[0] = command;
			}
			this.postCommand = 0;
		}

		/**
		 * Create an ActionListener with only the command to execute
		 *
		 * @param command the command to execute
		 * @param postCommand the post processing command to execute
		 */
		PopupActionListener (String command, int postCommand) {
			// We need to be a little careful.  If the context is not generic,
			// then the object might not be selected.  To make things easy,
			// we selected it, execute our command, and then deselected
			this.postCommand = postCommand;
			if (command == null) {
				commandList = new String[0];
				return;
			}
			if (context != GENERIC_CONTEXT && objectList.size() == 1) {
				String spec = ((ChimeraStructuralObject)objectList.get(0)).toSpec();
				command = command.replaceAll("%sel",spec);
			} else if (postCommand == MODEL_SELECTION) {
				// Special case for chemistry selection commands
				String spec = new String();
				Iterator<ChimeraStructuralObject> modelIter = objectList.iterator();
				while (modelIter.hasNext()) {
					ChimeraStructuralObject obj = modelIter.next();
					spec = spec.concat(obj.toSpec());
					if (modelIter.hasNext()) spec = spec.concat(",");
				}
				command = command.replaceAll("%sel",spec);
			} else {
				command = command.replaceAll("%sel","sel");
			}
			commandList = command.split("\n");
			if (commandList.length == 0) {
				commandList = new String[1];
				commandList[0] = command;
			}
		}

		/**
		 * Execute the actual command and process the post command
		 *
		 * @param ev the ActionEvent (not used)
		 */
		public void actionPerformed(ActionEvent ev) {
			// Special case for chemistry selection commands
			if (postCommand == MODEL_SELECTION) {
				chimeraObject.select(commandList[0]);
				chimeraObject.modelChanged();
				return;
			} else if (postCommand == FUNCTIONAL_RESIDUES) {
				// Get the object
				for (ChimeraStructuralObject obj: objectList) {
					ChimeraModel model = obj.getChimeraModel();
					List<String> residueL = model.getStructure().getResidueList();
					if (residueL == null) return;
					// The residue list is of the form RRRnnn,RRRnnn.  We want
					// to reformat this to nnn,nnn
					String residues = new String();
					for (String residue: residueL) {
						residues = residues.concat(residue+",");
					}
					residues = residues.substring(0,residues.length()-1);
					String command = "select #"+model.getModelNumber()+":"+residues;
					chimeraObject.select(command);
				}
				chimeraObject.modelChanged();
				return;
			} else if (postCommand == DELETE) {
				String message;
				if (objectList.size() > 1) {
					message = "Are you sure you want to delete the selected items?";
				} else {
					message = "Are you sure you want to delete "+objectList.get(0).toString()+"?";
				}
				int answer = JOptionPane.showConfirmDialog(chimeraObject.getDialog(), 
												message, "Confirm", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.NO_OPTION) {
					return;
				}
			}
			for (int i=0; i<commandList.length; i++) {
					// System.out.println("To Chimera: "+commandList[i]);
					chimeraObject.chimeraSend(commandList[i]);
			}
			if (postCommand == CLEAR_SELECTION) {
				navTree.clearSelection();
			} else if (postCommand == CLOSE) {
				// Get the object
				for (ChimeraStructuralObject obj: objectList) {
					ChimeraModel model = obj.getChimeraModel();
					chimeraObject.close(model.getStructure());
				}
				chimeraObject.modelChanged();
			} else if (postCommand == DELETE) {
				// Delete triggers selection events that we need to let complete
				try { Thread.sleep(200); } catch (java.lang.InterruptedException e) {}
				// System.out.println("Chain delete -- calling refresh");
				chimeraObject.refresh();
				try { Thread.sleep(200); } catch (java.lang.InterruptedException e) {}
				// System.out.println("Chain delete -- calling modelChanged");
				chimeraObject.modelChanged();
			}
		}
	}
}
