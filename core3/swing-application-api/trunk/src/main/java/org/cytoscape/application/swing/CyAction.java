
/*
 File: CyAction.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.application.swing;

import javax.swing.Action;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * An interface that describes how an action should be placed within 
 * the menus and/or toolbars of the Swing application.
 */
public interface CyAction extends Action, MenuListener, PopupMenuListener {

	/**
	 * Returns the name of the action.
	 * @return the name of the action.
	 */
	String getName();

	/**
	 * Returns whether the action should be in the menu bar.
	 * @return whether the action should be in the menu bar.
	 */
	boolean isInMenuBar();

	/**
	 * Returns whether the action should be in the tool bar.
	 * @return whether the action should be in the tool bar.
	 */
	boolean isInToolBar();

	/**
	 * Returns the gravity used to place the menu item for this action.
	 * @return The gravity used to place the menu item for this action.
	 */
	float getMenuGravity();

	/**
	 * Returns the gravity used to place this action in the toolbar.
	 * @return The gravity used to place this action in the toolbar.
	 */
	float getToolbarGravity();

	/**
	 * Returns whether or not this action is accelerated.
	 * @return Whether or not this action is accelerated.
	 */
	boolean isAccelerated();

	/**
	 * Returns the key code used to identify this action.
	 * @return the key code used to identify this action.
	 */
	int getKeyCode();

	/**
	 * Returns the key modifiers used to identify this action.
	 * @return the key modifiers used to identify this action.
	 */
	int getKeyModifiers();

	/** 
	 * Returns the string identifying the preferred menu.
	 * @return the string identifying the preferred menu.
	 */
	String getPreferredMenu();

	/**
	 * Buttons will be grouped according to name and according to gravity
	 * within the group.  
	 * @return The name identifying the button group.
	 */
	String getPreferredButtonGroup();

	/** 
	 * Returns whether or not a checkbox menu item should be used.
	 * @return whether or not a checkbox menu item should be used.
	 */
	boolean useCheckBoxMenuItem();
}
