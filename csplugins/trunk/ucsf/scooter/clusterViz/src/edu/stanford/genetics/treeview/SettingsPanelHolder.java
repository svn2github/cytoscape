/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: SettingsPanelHolder.java,v $
* $Revision: 1.3 $
* $Date: 2006/03/26 23:24:44 $
* $Name:  $
*
* This file is part of Java TreeView
* Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
*
* This software is provided under the GNU GPL Version 2. In particular,
*
* 1) If you modify a source file, make a comment in it containing your name and the date.
* 2) If you distribute a modified version, you must do it under the GPL 2.
* 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
*
* A full copy of the license can be found in gpl.txt or online at
* http://www.gnu.org/licenses/gpl.txt *
* END_HEADER
*/
package edu.stanford.genetics.treeview;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author aloksaldanha
 *
 * Makes a JPanel with Save and Cancel buttons.
 * The buttons will hide the window if the window is not null.
 */
public class SettingsPanelHolder extends JPanel {

	private Window window = null;
	private ConfigNode configNode = null;
	
	/**
	 * Please use this constructor.
	 * @param w Window to close on Save or Cancel
	 * @param c ConfigNode to store on a save.
	 */
	public SettingsPanelHolder(Window w, ConfigNode c) {
		super();
		window = w;
		configNode = c;
		setLayout(new BorderLayout());
		add(new ButtonPanel(), BorderLayout.SOUTH);
	}
	
	public void synchronizeTo() {
		int n = this.getComponentCount();
		for (int i = 0; i < n; i++) {
			synchronizeTo(i);
		}
	}
	public void synchronizeTo(int i) {
		try {
			((SettingsPanel) getComponent(i)).synchronizeTo();
		} catch (ClassCastException e) {
			// ignore
		}
	}
	
	public void synchronizeFrom() {
		int n = this.getComponentCount();
		for (int i = 0; i < n; i++) {
			synchronizeFrom(i);
		}
	}
	public void synchronizeFrom(int i) {
		try {
			((SettingsPanel) getComponent(i)).synchronizeFrom();
		} catch (ClassCastException e) {
			// ignore
		}
	}
	public void addSettingsPanel(SettingsPanel sP) {
		add((Component) sP, BorderLayout.CENTER);
	}
	  class ButtonPanel extends JPanel {
	  	private void hideWindow() {
	  		if (window == null) {
	  			LogBuffer.println("SettingsPanelHolder.hideWindow(): window is null");
	  		} else {
			  window.hide();
	  		}
	  	}
		ButtonPanel() {
		  JButton save_button = new JButton("Save");
		  save_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  synchronizeTo();
			  if (configNode == null) {
			  	LogBuffer.println("SettingsPanelHolder.Save: configNode is null");
			  } else {
			  	configNode.store();
			  }
			  hideWindow();
			}
		  });
		  add(save_button);
		  
		  JButton cancel_button = new JButton("Cancel");
		  cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  synchronizeFrom();
			  hideWindow();
			}
		  });
		  add(cancel_button);
		}
	  }

}
