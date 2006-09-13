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
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.WindowConstants.*;

import java.awt.*;
import java.awt.event.*;

import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraChain;
import structureViz.model.Structure;

import structureViz.Chimera;

public class ActionPopupMenu extends JPopupMenu {
	private Chimera chimeraObject;
	private ArrayList modelList;
	private ArrayList chainList;
	private ArrayList residueList;
	private JTree navTree;

	public ActionPopupMenu (Chimera co, JTree tree, ArrayList models,
													ArrayList chains, ArrayList residues) 
	{
		super("Chimera Actions");
		this.chimeraObject = co;
		this.modelList = models;
		this.chainList = chains;
		this.residueList = residues;
		this.navTree = tree;

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

	private void createGenericMenu() {
		addHeader("Generic Actions");
		JMenu submenu = null;
		JMenu sub2 = null;
		JMenuItem item = null;

		// Show
		submenu = new JMenu("Show");
		{
			addItem(submenu, "All", "disp sel",0);
			addItem(submenu, "Backbone only", "~disp sel; disp sel & @n,ca,c",0);
		}
		add(submenu);
		// Hide
		addItem(null, "Hide", "~disp sel",0);
		// Focus
		addItem(null, "Focus", "focus sel",0);
		// Color
		submenu = new JMenu("Color");
		{
			addItem(submenu, "By element", "color byelement sel",0);
			addColorMenu(submenu, "Residues", "color ", ",a sel");
			addColorMenu(submenu, "Ribbons", "color ", ",r sel");
			addColorMenu(submenu, "Surfaces", "color ", ",s sel");
			addColorMenu(submenu, "Labels", "color ", ",l sel");
		}
		add(submenu);
		// Depict
		submenu = new JMenu("Depict");
		{
			addItem(submenu, "Wire", "repr wire sel",0);
			addItem(submenu, "Stick", "repr stick sel",0);
			addItem(submenu, "Ball & Stick", "repr bs sel",0);
			addItem(submenu, "Sphere", "repr cpk sel",0);
			addItem(submenu, "CPK", "repr cpk sel",0);
			sub2 = new JMenu("Ribbon");
			{
				addItem(sub2, "Hide", "~ribbon sel",0);
				addItem(sub2, "Flat", "ribrepr flat sel;ribbon sel",0);
				addItem(sub2, "Edged", "ribrepr edged sel;ribbon sel",0);
				addItem(sub2, "Round", "ribrepr round sel;ribbon sel",0);
			}
			submenu.add(sub2);
			sub2 = new JMenu("Surface");
			{
				addItem(sub2, "Hide", "~surface sel",0);
				addItem(sub2, "Solid", "surface sel;surfrepr solid",0);
				addItem(sub2, "Mesh", "surface sel;surfrepr mesh",0);
				addItem(sub2, "Dot", "surface sel;surfrepr dot",0);
				JMenu sub3 = new JMenu("Tansparency");
				{
					addItem(sub3, "0%", "surftransparency 0% sel",0);
					addItem(sub3, "10%", "surftransparency 10% sel",0);
					addItem(sub3, "20%", "surftransparency 20% sel",0);
					addItem(sub3, "30%", "surftransparency 30% sel",0);
					addItem(sub3, "40%", "surftransparency 40% sel",0);
					addItem(sub3, "50%", "surftransparency 50% sel",0);
					addItem(sub3, "60%", "surftransparency 60% sel",0);
					addItem(sub3, "70%", "surftransparency 70% sel",0);
					addItem(sub3, "80%", "surftransparency 80% sel",0);
					addItem(sub3, "90%", "surftransparency 90% sel",0);
				}
				sub2.add(sub3);
			}
			submenu.add(sub2);
		}
		add(submenu);
		// Label
		submenu = new JMenu("Label");
		{
			addItem(submenu, "Hide", "~label sel;~rlabel sel",0);
			addItem(submenu, "Atom name", "labelopt info name; label sel",0);
			addItem(submenu, "Element", "labelopt info element; label sel",0);
			addItem(submenu, "IDATM type", "labelopt info idatmType; label sel",0);
			addItem(submenu, "Residue", "rlabel sel",0);
		}
		add(submenu);
		// Clear Selection
		addItem(null, "Clear selection", "~select", PopupActionListener.CLEAR_SELECTION);
	}

	private void createModelMenu() { return; }

	private void createChainMenu() { return; }

	private void createResidueMenu() { return; }

	private void addHeader(String header) {
		add(new JMenuItem(header));
		add(new JPopupMenu.Separator());
	}

	private JMenuItem addItem(JMenu menu, String text, String command, int postCommand) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(new PopupActionListener(command,postCommand));
		if (menu == null)
			this.add(item);
		else
			menu.add(item);
		return item;
	}

	private void addColorMenu(JMenu menu, String text, String prefix, String suffix) {
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
	}

	private class PopupActionListener implements ActionListener {
		String[] commandList;
		public static final int NO_POST = 0;
		public static final int CLEAR_SELECTION = 1;
		public static final int CLOSE = 2;
		int postCommand = NO_POST;

		PopupActionListener (String command) {
			commandList = command.split("\n");
			if (commandList.length == 0) {
				commandList = new String[1];
				commandList[0] = command;
			}
			this.postCommand = 0;
		}

		PopupActionListener (String command, int postCommand) {
			commandList = command.split("\n");
			if (commandList.length == 0) {
				commandList = new String[1];
				commandList[0] = command;
			}
			this.postCommand = postCommand;
		}

		public void actionPerformed(ActionEvent ev) {
			for (int i=0; i<commandList.length; i++) {
					chimeraObject.command(commandList[i]);
			}
			if (postCommand == CLEAR_SELECTION) {
				navTree.clearSelection();
			} else if (postCommand == CLOSE) {
			}
		}
	}
}
