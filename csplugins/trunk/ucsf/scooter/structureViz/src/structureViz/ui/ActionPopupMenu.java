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
			addItem(submenu, "All", "show sel",0);
			addItem(submenu, "Backbone only", "show sel",0);
		}
		add(submenu);
		// Hide
		addItem(null, "Hide", "~show sel",0);
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
		// Label
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

	private void addItem(JMenu menu, String text, String command, int postCommand) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(new PopupActionListener(command,postCommand));
		if (menu == null)
			this.add(item);
		else
			menu.add(item);
	}

	private void addColorMenu(JMenu menu, String text, String prefix, String suffix) {
		String[] colorList = {"red", "orange red", "orange", "yellow"};
		JMenu colorMenu = new JMenu(text);
		for (int color=0; color < colorList.length; color++) {
			addItem(colorMenu, colorList[color], prefix+colorList[color]+suffix,0);
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
