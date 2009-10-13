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
package commandTool.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.logger.CyLogger;

public class CommandToolDialog extends JDialog 
                             implements ActionListener {

	private CyLogger logger;
	private List<String> commandList;

	// Dialog components
	private JResultsPane resultsText;
	private JTextField inputField;

	public CommandToolDialog (Frame parent, CyLogger logger) {
		super(parent, false);
		this.logger = logger;
		commandList = new ArrayList();
		initComponents();
	}

	/**
	 * Initialize all of the graphical components of the dialog
	 */
	private void initComponents() {
		this.setTitle("Command Line Dialog");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create a panel for the main content
		JPanel dataPanel = new JPanel();
		BoxLayout layout = new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS);
		dataPanel.setLayout(layout);

		Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

		resultsText = new JResultsPane();
		resultsText.setEditable(false);
		resultsText.setPreferredSize(new Dimension(900, 200));
		JScrollPane scrollPane = new JScrollPane(resultsText);

		scrollPane.setBorder(BorderFactory.createTitledBorder(etchedBorder, "Reply Log"));
		dataPanel.add(scrollPane);

		inputField = new JTextField(80);
		inputField.setBorder(BorderFactory.createTitledBorder(etchedBorder, "Command"));
		inputField.addActionListener(this);
		inputField.requestFocusInWindow();
		dataPanel.add(inputField);
		inputField.setMaximumSize(new Dimension(1000,45));

		// Create the button box
		JPanel buttonBox = new JPanel();
		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		JButton clearButton = new JButton("Clear");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);

		buttonBox.add(clearButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonBox.setMaximumSize(new Dimension(1000,45));
		
		dataPanel.add(buttonBox);
		setContentPane(dataPanel);
		setMaximumSize(new Dimension(1000,1000));
	}

	public void actionPerformed(ActionEvent e) {
		if ("done".equals(e.getActionCommand())) {
			this.dispose();
		} else if ("clear".equals(e.getActionCommand())) {
			resultsText.setStyledDocument(new DefaultStyledDocument());
		} else {
			String input = inputField.getText();
			resultsText.appendCommand(input);
			commandList.add(input);

			handleCommand(input);

			inputField.selectAll();
		}
	}

	private void handleCommand(String input) {
		CyCommandResult results = null;
		try {
			String builtIn = null;
			CyCommandHandler comm = null;
			if ((comm = isCommand(input)) != null) {
				results = handleCommand(input, comm);
			} else {
				throw new CyCommandException("Unknown command: "+input);
			}
			// Get all of the messages from our results
			for (String s: results.getMessages()) {
				resultsText.appendMessage("  "+s+"\n");
			}
		} catch (CyCommandException e) {
			resultsText.appendError("  "+e.getMessage()+"\n");
		}
		resultsText.appendMessage("\n");
	}

	private CyCommandHandler isCommand(String input) {
		for (CyCommandHandler comm: CyCommandManager.getHandlerList()) {
			String s = comm.getHandlerName();
			if (input.toLowerCase().startsWith(s.toLowerCase()))
				return comm;
		}
		return null;
	}

	private CyCommandResult handleCommand(String inputLine, CyCommandHandler comm) throws CyCommandException {
		String sub = null;
		// Parse the input, breaking up the tokens into appropriate
		// commands, subcommands, and maps

		int subIndex = comm.getHandlerName().length();

		Map<String,String> settings = new HashMap();
		String subCom = parseInput(inputLine.substring(subIndex).trim(), settings);
		
		for (String command: comm.getCommands()) {
			if (command.toLowerCase().equals(subCom.toLowerCase())) {
				sub = command;
				break;
			}
		}

		if (sub == null && (subCom != null && subCom.length() > 0))
			throw new CyCommandException("Unknown argument: "+subCom);
		
		return comm.execute(sub, settings);
	}

	private String parseInput(String input, Map<String,String> settings) {
		String command = "";

		// Tokenize
		String[] tokens = input.split(" ");

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].indexOf('=') > 0) {
				String[] setting = tokens[i].split("=");
				settings.put(setting[0],setting[1]);
			} else {
				command += tokens[i].trim()+" ";
			}
		}

		// Now, the last token of the args goes with the first setting
		return command.trim();
	}

	class JResultsPane extends JTextPane {
		private SimpleAttributeSet commandAttributes;
		private SimpleAttributeSet messageAttributes;
		private SimpleAttributeSet errorAttributes;
		public JResultsPane() {
			super();

			commandAttributes = new SimpleAttributeSet();
			commandAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.BLUE);
			commandAttributes.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
			commandAttributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);

			messageAttributes = new SimpleAttributeSet();
			messageAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.GREEN);
			messageAttributes.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
			messageAttributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);

			errorAttributes = new SimpleAttributeSet();
			errorAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.RED);
			errorAttributes.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
			errorAttributes.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
		}

		public void appendCommand(String s) {
			updateString(commandAttributes, s+"\n");
		}

		public void appendError(String s) {
			updateString(errorAttributes, s);
		}

		public void appendResult(String s) {
		}

		public void appendMessage(String s) {
			updateString(messageAttributes, s);
		}

		private void updateString(AttributeSet set, String s) {
			StyledDocument doc = getStyledDocument();
			try {
				doc.insertString(doc.getLength(), s, set);
			} catch (BadLocationException badLocationException) {
			}
		}
	}
}
