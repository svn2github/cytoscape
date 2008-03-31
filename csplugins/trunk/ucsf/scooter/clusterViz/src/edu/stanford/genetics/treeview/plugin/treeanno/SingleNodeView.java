/*
 * Created on Mar 13, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.treeanno;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;

/**
 * This View displays an editable representation of the currently selected Tree Node
 */
public class SingleNodeView extends ModelView {
	private TreeSelectionI selection;
	private HeaderInfo headerInfo;
	/**
	 * index of node currently being edited
	 */
	private int editingIndex;
	
	public void setSelection(TreeSelectionI sel) {
		if (selection != null) {
			selection.deleteObserver(this);	
		}
		selection = sel;
		selection.addObserver(this);
		if (selection != null) {
			update(selection, null);
		}
	}
	private JTextField nameF = new JTextField(10);
	private JTextField annoF = new JTextField(20);
	/**
	 * @param nodeInfo
	 */
	public SingleNodeView(HeaderInfo nodeInfo) {
		headerInfo = nodeInfo;
		headerInfo.addObserver(this);
		
		annoF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				headerInfo.setHeader(editingIndex,"ANNOTATION", annoF.getText());
			}
		});
		nameF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				headerInfo.setHeader(editingIndex,"NAME", nameF.getText());
			}
		});
		
		

		JPanel nameP = new JPanel();
		nameP.add(new JLabel("Name"));
		nameP.add(nameF);
		
		JPanel annoP = new JPanel();
		annoP.add(new JLabel("Annotation"));
		annoP.add(annoF);
		
		JPanel mainP = new JPanel();
		mainP.add(nameP);
		mainP.add(annoP);
		
		setLayout(new BorderLayout());
		add(mainP);
	}
	
	public String viewName() {
		return "Single Node Editor";
	}

	protected void updateBuffer(Graphics g) {
		// no buffer here.
	}
	public void update(Observable o, Object arg) {
		update((Object) o, arg);
	}
	public void update(Object o, Object arg) {

		if (o == selection) {
			String node = selection.getSelectedNode();
			if (node != null) {
				int i = headerInfo.getHeaderIndex(node);
				if (i >= 0) {
					editingIndex = i;
					synchronizeFrom();
				}
			}
		} else if (o == headerInfo) {
			synchronizeFrom();
		}
			
	}
	
	/**
	 * copies values from node into fields for editing.
	 */
	private void synchronizeFrom() {
		nameF.setText(headerInfo.getHeader(editingIndex,"NAME"));
		annoF.setText(headerInfo.getHeader(editingIndex,"ANNOTATION"));
	}

	
}
