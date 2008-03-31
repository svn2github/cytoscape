/*
 * Created on Dec 4, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.core;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.stanford.genetics.treeview.LogBuffer;

public class LogSettingsPanel extends JPanel implements Observer {
	private LogBuffer logBuffer;
	private JCheckBox logBox = new JCheckBox("Log Messages");
	public LogSettingsPanel(LogBuffer buffer) {
		super();
		add(logBox);
		logBuffer = buffer;
		logBuffer.addObserver(this);
		logBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronizeTo();
			}

		});
		
		synchronizeFrom();	
	}
	private void synchronizeFrom() {
		if (logBox.isSelected() != logBuffer.getLog())
			logBox.setSelected(logBuffer.getLog());
	}
	private void synchronizeTo() {
		logBuffer.setLog(logBox.isSelected());
	}
	public void update(Observable arg0, Object arg1) {
		if (arg1 == null)
			synchronizeFrom();
	}
}
