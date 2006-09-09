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

import structureViz.Chimera;

public class actionPopupMenu extends JPopupMenu {
	private Chimera chimeraObject;
	private ArrayList modelList;
	private ArrayList chainList;
	private ArrayList residueList;
	private JTree navTree;

	public actionPopupMenu (Chimera co, JTree tree, ArrayList models,
													ArrayList chains, ArrayList residues) 
	{
		super("Chimera Actions");
		this.chimeraObject = co;
		this.modelList = models;
		this.chainList = chains;
		this.residueList = residues;
		this.navTree = tree;

		CreateGenericMenu();
		if (modelList.size() > 0) {
			CreateModelMenu();
		}
		if (chainList.size() > 0) {
			CreateChainMenu();
		}
		if (residueList.size() > 0) {
			CreateResidueMenu();
		}
	}

	private void CreateGenericMenu() {
		AddHeader("Generic Actions");
		JMenu submenu = null;
		JMenu sub2 = null;
		JMenuItem item = null;

		// Show
		submenu = new JMenu("Show");
		{
			AddItem(submenu, "All", "show sel",0);
			AddItem(submenu, "Backbone only", "show sel",0);
		}
		add(submenu);
		// Hide
		AddItem(null, "Hide", "~show sel",0);
		// Color
		submenu = new JMenu("Color");
		{
			AddItem(submenu, "By element", "color byelement sel",0);
			AddColorMenu(submenu, "Residues", "color ", ",a sel");
			AddColorMenu(submenu, "Ribbons", "color ", ",r sel");
			AddColorMenu(submenu, "Surfaces", "color ", ",s sel");
			AddColorMenu(submenu, "Labels", "color ", ",l sel");
		}
		add(submenu);
		// Depict
		// Label
		// Clear Selection
		AddItem(null, "Clear selection", "~select", popupActionListener.CLEAR_SELECTION);
	}

	private void CreateModelMenu() { return; }

	private void CreateChainMenu() { return; }

	private void CreateResidueMenu() { return; }

	private void AddHeader(String header) {
		add(new JMenuItem(header));
		add(new JPopupMenu.Separator());
	}

	private void AddItem(JMenu menu, String text, String command, int postCommand) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(new popupActionListener(command,postCommand));
		if (menu == null)
			this.add(item);
		else
			menu.add(item);
	}

	private void AddColorMenu(JMenu menu, String text, String prefix, String suffix) {
		String[] colorList = {"red", "orange red", "orange", "yellow"};
		JMenu colorMenu = new JMenu(text);
		for (int color=0; color < colorList.length; color++) {
			AddItem(colorMenu, colorList[color], prefix+colorList[color]+suffix,0);
		}
		menu.add(colorMenu);
	}

	private class popupActionListener implements ActionListener {
		String[] commandList;
		public static final int NO_POST = 0;
		public static final int CLEAR_SELECTION = 1;
		public static final int CLOSE = 2;
		int postCommand = NO_POST;

		popupActionListener (String command) {
			commandList = command.split("\n");
			if (commandList.length == 0) {
				commandList = new String[1];
				commandList[0] = command;
			}
			this.postCommand = 0;
		}

		popupActionListener (String command, int postCommand) {
			commandList = command.split("\n");
			if (commandList.length == 0) {
				commandList = new String[1];
				commandList[0] = command;
			}
			this.postCommand = postCommand;
		}

		public void actionPerformed(ActionEvent ev) {
			try {
				for (int i=0; i<commandList.length; i++) {
					chimeraObject.command(commandList[i]);
				}
			} catch (java.io.IOException ex) {}
			if (postCommand == CLEAR_SELECTION) {
				navTree.clearSelection();
			} else if (postCommand == CLOSE) {
			}
		}
	}
}
