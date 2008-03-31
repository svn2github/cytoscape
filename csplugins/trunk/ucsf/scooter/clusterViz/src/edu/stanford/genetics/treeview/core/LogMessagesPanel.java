/*
 * Created on Dec 4, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.core;

import java.awt.LayoutManager;
import java.util.*;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import edu.stanford.genetics.treeview.LogBuffer;

public class LogMessagesPanel extends JTextArea implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LogBuffer logBuffer;
	public LogMessagesPanel(LogBuffer buffer) {
		super(null, 20, 50);
		logBuffer = buffer;
		logBuffer.addObserver(this);
		synchronizeFrom();
	}
	private void synchronizeFrom() {
		Enumeration elements = logBuffer.getMessages();
		while (elements.hasMoreElements()) {
			append((String) elements.nextElement());
			append("\n");
		}
	}
	public void update(Observable arg0, Object arg1) {
		if (arg1 != null) {
			this.append((String) arg1);
			this.append("\n");
		}
	}
	
}
