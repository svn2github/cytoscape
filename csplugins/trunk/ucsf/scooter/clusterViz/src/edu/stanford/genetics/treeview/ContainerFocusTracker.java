/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: ContainerFocusTracker.java,v $
 * $Revision: 1.5 $
 * $Date: 2004/12/21 03:28:13 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview;


import java.awt.*;
import java.awt.event.*;

/**
 * Note: I, Alok, didn't write this class.
 *
 * Class for Containers which won't take the keyboard focus.
 * Because of what appears to be a bug in Java 1.1, when you move the
 * focus on to a Frame, or other Container, it keeps the focus, rather
 * than handing it on to an appropriate tabstop inside it.
 * This class collects all focus events for the Container and its contents, 
 * and will correct the problem. It also monitors the Container, and keeps
 * track if the focussed component is removed from the container.
 * To use, just create a tracker, passing it the Container you want to
 * deal with. 
 */
public class ContainerFocusTracker implements FocusListener, ContainerListener {
	static RCSVersion version = new RCSVersion("$Id: ContainerFocusTracker.java,v 1.5 2004/12/21 03:28:13 alokito Exp $");
	//****************************
	// Constructors
	//****************************
	public ContainerFocusTracker(Container c) {
		if(debug)
			System.out.println("FocusTracker(" + c.getName() + ")");
		container = c;
		addComponent(c);
	}

	//****************************
	// Event handling
	//****************************

	public void componentAdded(ContainerEvent e) {
		if(debug)
			System.out.println(container.getName() + " - Adding...");
		addComponent(e.getChild());
	}

	public void componentRemoved(ContainerEvent e) {
		if(debug)
			System.out.println(container.getName() + " - Removing...");
		removeComponent(e.getChild());
	}

	public void focusGained(FocusEvent e) {
		Component c = e.getComponent();

		if(c == container) {
			if(debug)
				System.out.println("Container " + container.getName() + " got focus");
			if(focus != null) {
				if(debug)
					System.out.println("Returning focus to " + focus.getName());
				focus.requestFocus();
			} else {
				switchFocus(container);
			}
		} else if(c.isVisible() && c.isEnabled() && c.isFocusTraversable()) {
			if(debug)
				System.out.println(container.getName() + " - Tracking focus to " + e.getComponent().getName());
			focus = c;
		}
	}

	public void focusLost(FocusEvent e) {
	}

	//****************************
	// Package and private methods
	//****************************

	private boolean switchFocus(Container container) {
		synchronized (container.getTreeLock()) {
			for (int i = 0; i < container.countComponents(); i++) {
				Component c = container.getComponent(i);

				if(c == null)
					break;
				if (c.isVisible() && c.isEnabled() 
						&& c.isFocusTraversable()) {
					if(debug)
						System.out.println(this.container.getName() + " - Giving focus to " + c.getName());
					c.requestFocus();
					return true;
				} else if(c instanceof Container) {
					if(switchFocus((Container)c))
						return true;
				} else if(debug) {
					System.out.println("Not giving focus to " + c.getName()
						+ " vis:" + c.isVisible()
						+ " ena:" + c.isEnabled()
						+ " tab:" + c.isFocusTraversable());
				}
			}
		}
		return false;
	}

	private void addComponent(Component c) {
		if(debug)
			System.out.println(" " + c.getName());
		c.addFocusListener(this);
		if(c instanceof Container)
			addContainer((Container)c);
	}

	private void addContainer(Container container) {
		container.addContainerListener(this);
		synchronized (container.getTreeLock()) {
			for (int i = 0; i < container.countComponents(); i++) {
				Component c = container.getComponent(i);
				addComponent(c);
			}
		}
	}

	private void removeComponent(Component c) {
		if(debug)
			System.out.println(" " + c.getName());
		if(c == focus)
			focus = null;
		c.removeFocusListener(this);
		if(c instanceof Container)
			removeContainer((Container)c);
	}

	private void removeContainer(Container container) {
		container.removeContainerListener(this);
		synchronized (container.getTreeLock()) {
			for (int i = 0; i < container.countComponents(); i++) {
				Component c = container.getComponent(i);
				removeComponent(c);
			}
		}
	}

	//****************************
	// Variables
	//****************************

	Container container;
	Component focus;
	final static boolean debug = false;
}

