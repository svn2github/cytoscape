/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: RegEditor.java,v $
* $Revision: 1.8 $
* $Date: 2006/09/25 22:02:02 $
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
* http://www.gnu.org/licenses/gpl.txt
*
* END_HEADER
*/
package edu.stanford.genetics.treeview.reg;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.*;

import edu.stanford.genetics.treeview.BrowserControl;
import edu.stanford.genetics.treeview.TreeViewApp;

/**
 * 
 * Allows users to edit their registration information prior to submission.
 * 
 *	Should allow editing of the user-specified keys from the RegEngine,
 * and display values of the auto-determined fields.
 * 
 * Note that if the key ends with Okay, it will be treated as a boolean for display and editing purposes.
 * 
 * @author aloksaldanha
 *
 */
public class RegEditor extends JPanel {

	Entry dataSource;
	GridBagLayout gridbag;
	GridBagConstraints gbc;
	/**
	 * @param entry
	 */
	public RegEditor(Entry entry) {
		dataSource = entry;
		addWidgets();
	}
	/**
	 * 
	 */
	private void addWidgets() {
//		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		gridbag = new GridBagLayout();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		setLayout(gridbag);
		for (int i = 0; i <dataSource.getNumRegKeys(); i++) {
			gbc.gridy = i;
			addAttribute(i, dataSource.isEditable(i));
		}
	}
	
	
	/**
	 * @param index - index into RegKeys array of Entry for key of the desired attribute
	 */
	private void addAttribute(int index) {
		addAttribute(index, false);
	}
	Hashtable attr2val = new Hashtable();
	/**
	 * @param i index of key corresponding to attribute
	 * @param isEditable indicates whether attribute should be editable.
	 */
	private void addAttribute(int i, boolean isEditable) {
		String key = dataSource.getRegKey(i);
		gbc.gridx = 0;
		if (isEditable) {
			JPanel inner = new JPanel();
			if (key.equals("contactOkay")) {
				Box inner2 = new Box(BoxLayout.Y_AXIS);
				inner2.add(new JLabel("May we contact you when new\n versions become available?"));
				inner2.add(new JLabel("(Note: a browser will open for mailing list signup)"));
/*
				inner2.add(new JLabel("To recieve annoucements about new versions, add yourself to"));
				inner2.add(new JLabel("the jtreeview-announce email list. This list is very  low"));
				inner2.add(new JLabel("volume (< 1 email/month)"));
				*/
				inner.add(inner2);
			} else {
				inner.add(new JLabel(key));
			}
			JLabel star = new JLabel("*");
			star.setForeground(Color.red);
			inner.add(star);
			gridbag.setConstraints(inner, gbc);
			add(inner);
		} else {
			JLabel label = new JLabel(key);
			gridbag.setConstraints(label, gbc);
			add(label);
		}
		gbc.gridx = 1;
		if (key.equals("contactOkay2")) {
			// this code is never executed, since I decided to have the browser window
			// open when the reg dialog closes.
			Box box = new Box(BoxLayout.Y_AXIS);
			box.add(new JTextField(TreeViewApp.getAnnouncementUrl()));
			JButton yesB = new JButton("Open in browser");
			yesB.addActionListener(new ActionListener () {

				public void actionPerformed(ActionEvent arg0) {
					BrowserControl bc = BrowserControl.getBrowserControl();
					try {
						bc.displayURL(TreeViewApp.getAnnouncementUrl());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
			box.add(yesB);
			gridbag.setConstraints(box, gbc);
			add(box);
		} else if (key.endsWith("Okay")) {
			JPanel box = new JPanel();
			ButtonGroup group = new ButtonGroup();
			JRadioButton yesB = new JRadioButton("Yes");
			JRadioButton noB = new JRadioButton("No");
			attr2val.put(key, yesB);
			group.add(yesB);
			group.add(noB);
			box.add(yesB);
			box.add(noB);
			if (dataSource.getRegValue(i).equals("N")) {
				noB.setSelected(true);
			} else {
				yesB.setSelected(true);
			}
			gridbag.setConstraints(box, gbc);
			add(box);
		} else {
			JTextField field =new JTextField(dataSource.getRegValue(i));
			attr2val.put(key, field);
			field.setEditable(isEditable);
			field.setEnabled(isEditable);
			gridbag.setConstraints(field, gbc);
			add(field);
		}
	}
	public String getAttribute(String attr) {
		Object control =  attr2val.get(attr);
		if (attr.endsWith("Okay")) {
			// boolean attribute
			if (((JRadioButton) control).isSelected()) {
				return "Y";
			} else {
				return "N";
			}
		} else {
			return ((JTextField) control).getText();
		}
	}


	/**
	 *
	 *Inner class to represent attributes.
	 * 
	 *  @author aloksaldanha
	 *
	 */
	class AttributePanel extends JPanel {

		/**
		 * @param i index of reg key to represent
		 * @param isEditable indicates whether the user should be able to edit this entry.
		 */
		public AttributePanel(int i, boolean isEditable) {
			add(new JLabel(dataSource.getRegKey(i)));
			add(new JTextField(dataSource.getRegValue(i)));
		}
	}

}
