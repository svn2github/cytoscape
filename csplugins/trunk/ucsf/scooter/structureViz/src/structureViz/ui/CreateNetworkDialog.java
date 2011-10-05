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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import javax.swing.border.EtchedBorder;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.task.util.TaskManager;

// StructureViz imports
import structureViz.actions.Chimera;
import structureViz.actions.CreateNetworkAction;

/**
 */
public class CreateNetworkDialog extends JDialog implements ActionListener,TunableListener {
	// Instance variables
	Chimera chimeraObject;
	LayoutProperties properties;
	boolean includeContacts = true;
	boolean includeClashes = false;
	boolean includeHBonds = false;
	boolean includeConnectivity = false;
	boolean includeConnectivityDistance = false;
	int interactionBetween = 2; // Between selection & other models
	public static final int BETWEENMODELS = 0;
	public static final int BETWEENSELMODELS = 1;
	public static final int BETWEENALL = 2;
	static final String[] interactionArray = {"Between models", "Between selection & other models", "Between selection and all atoms"};

	/**
	 * Create a CreateNetworkDialog
	 *
	 * @param parent the Frame acting as the parent of this Dialog
	 * @param object the Chimera interface object
	 */
	public CreateNetworkDialog (Dialog parent, Chimera object) {
		super(parent, false);
		chimeraObject = object;
		properties = new LayoutProperties("CreateNetworkDialog");
		initTunables();
		initComponents();
	}

	/**
	 * Initialize all of the graphical components of the dialog
	 */
	private void initComponents() {
		this.setTitle("Create Network Dialog");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create a panel for the main content
		JPanel dataPanel = new JPanel();
		BoxLayout layout = new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS);
		dataPanel.setLayout(layout);

		// Use Tunables for the checkboxes, etc.
		dataPanel.add(properties.getTunablePanel());

		// Add the button box
		JPanel buttonBox = new JPanel();
		JButton createButton = new JButton("Create network");
		createButton.setActionCommand("create");
		createButton.addActionListener(this);

		JButton doneButton = new JButton("Cancel");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);
		buttonBox.add(createButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonBox.setMaximumSize(new Dimension(1000,35));
		dataPanel.add(buttonBox);
		
		setContentPane(dataPanel);
	}

	private void initTunables() {
		properties.add(new Tunable("includeContacts", "Include contacts", 
		                           Tunable.BOOLEAN, includeContacts));
		properties.add(new Tunable("includeClashes", "Include clashes", 
		                           Tunable.BOOLEAN, includeClashes));
		properties.add(new Tunable("includeHBonds", "Include hydrogen bonds (overlaps with contacts)", 
		                           Tunable.BOOLEAN, includeHBonds));
		Tunable t = new Tunable("includeConnectivity", "Include connectivity",
		                        Tunable.BOOLEAN, includeConnectivity);
		t.addTunableValueListener(this);
		properties.add(t);
		t = new Tunable("includeConnectivityDistance", "Calculate connectivity distances (more time consuming)",
		                Tunable.BOOLEAN, includeConnectivityDistance);
		if (!includeConnectivity)
			t.setImmutable(true);
		properties.add(t);
		properties.add(new Tunable("interaction", "Include interactions",
		                           Tunable.LIST, new Integer(interactionBetween),
		                           (Object) interactionArray, (Object) null, 0));
	}

	private void updateTunables(boolean force) {
		Tunable t = properties.get("includeContacts");
		if ((t != null) && (t.valueChanged() || force))
			includeContacts = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("includeClashes");
		if ((t != null) && (t.valueChanged() || force))
			includeClashes = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("includeHBonds");
		if ((t != null) && (t.valueChanged() || force))
			includeHBonds = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("includeConnectivity");
		if ((t != null) && (t.valueChanged() || force))
			includeConnectivity = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("includeConnectivityDistance");
		if ((t != null) && (t.valueChanged() || force))
			includeConnectivityDistance = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("interaction");
		if ((t != null) && (t.valueChanged() || force))
			interactionBetween = ((Integer) t.getValue()).intValue();

	}
	
	/**
	 * The method called to actually execute the command.
	 */
	public void actionPerformed(ActionEvent e) {
		if ("done".equals(e.getActionCommand())) {
			setVisible(false);
			return;
		}
		if ("create".equals(e.getActionCommand())) {
			updateTunables(true);
			CreateNetworkAction action = new CreateNetworkAction(chimeraObject);
			action.setIncludeContacts(includeContacts);
			action.setIncludeClashes(includeClashes);
			action.setIncludeHBonds(includeHBonds);
			action.setIncludeConnectivity(includeConnectivity);
			action.setIncludeConnectivityDistance(includeConnectivityDistance);
			action.setInteractionBetween(interactionBetween);
			TaskManager.executeTask(action, action.getDefaultTaskConfig());

			setVisible(false);
			return;
		}
	}

	public void tunableChanged(Tunable t) {
		if (t.getName().equals("includeConnectivity")) {
			Tunable icd = properties.get("includeConnectivityDistance");
			if (((Boolean)t.getValue()).booleanValue()) {
				icd.setImmutable(false);
			} else {
				icd.setImmutable(true);
			}
		}
	}
}

