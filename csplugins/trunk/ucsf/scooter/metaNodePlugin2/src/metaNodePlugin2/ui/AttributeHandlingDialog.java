/* vim: set ts=2:

  File: AttributeHandlingDialog.java

  Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - University of California San Francisco
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
  Dout of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package metaNodePlugin2.ui;

import metaNodePlugin2.model.MetanodeProperties;
import metaNodePlugin2.model.AttributeHandler;
import metaNodePlugin2.model.AttributeHandler.AttributeHandlingType;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 *
 * The AttributeHandling is a dialog that provides an interface into all of the
 * options for attribute handling. 
 */
public class AttributeHandlingDialog extends JDialog 
                                      implements ActionListener, TunableListener, ComponentListener {

	private MetanodeProperties metanodeProperties;
	private Tunable typeString = null;
	private Tunable typeList = null;
	private Tunable attrList = null;
	private String[] attributeArray = null;
	private List<Tunable>tunableEnablers = null;

	// Dialog components
	JPanel tunablePanel = null;

	public AttributeHandlingDialog() {
		super(Cytoscape.getDesktop(), "Metanode Attribute Handling Dialog", false);
		metanodeProperties = new MetanodeProperties("");

		initializeProperties();

		AttributeHandler.saveSettings();

		initialize();
		pack();
	}

	private void initialize() {

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		this.tunablePanel = metanodeProperties.getTunablePanel();

		tunablePanel.addComponentListener(this);

		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder, "Tunable Settings");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		tunablePanel.setBorder(titleBorder);
		mainPanel.add(tunablePanel);

		JPanel buttonBox = new JPanel();

		JButton saveButton = new JButton("Save Settings");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);

		JButton clearButton = new JButton("Clear Settings");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);

		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		buttonBox.add(saveButton);
		buttonBox.add(clearButton);
		buttonBox.add(cancelButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		mainPanel.add(buttonBox);
		setContentPane(mainPanel);
	}

	private void initializeProperties() {
		tunableEnablers = new ArrayList();

		{
			Tunable t = new Tunable("enableHandling",
			                        "Enable Attribute Aggregation",
			                        Tunable.BOOLEAN, new Boolean(false), 0);
			t.addTunableValueListener(this);
			metanodeProperties.add(t);
		}

		metanodeProperties.add(new Tunable("defaultsGroup", "Defaults",
		                                   Tunable.GROUP, new Integer(5),
		                                   new Boolean(true), null, Tunable.COLLAPSABLE));
		{
			Tunable t = new Tunable("stringDefaults", "String Attributes",
		 	                        Tunable.LIST, new Integer(0),
		 	                        AttributeHandler.getStringOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);

			t = new Tunable("intDefaults", "Integer Attributes",
		 	                                  Tunable.LIST, new Integer(0),
		 	                                  AttributeHandler.getIntOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);

			t = new Tunable("doubleDefaults", "Double Attributes",
		 	                                  Tunable.LIST, new Integer(0),
		 	                                  AttributeHandler.getDoubleOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);

			t = new Tunable("listDefaults", "List Attributes",
		 	                                  Tunable.LIST, new Integer(0),
		 	                                  AttributeHandler.getListOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);

			t = new Tunable("booleanDefaults", "Boolean Attributes",
		 	                                  Tunable.LIST, new Integer(0),
		 	                                  AttributeHandler.getBooleanOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);
		}

		// We only really care about the property settings for our defaults
		metanodeProperties.initializeProperties();

		metanodeProperties.add(new Tunable("attributesGroup",
		                                    "Overrides for Specific Attributes",
		                                    Tunable.GROUP, new Integer(3),
		                                    new Boolean(true), null, Tunable.COLLAPSABLE));

		{
			attrList = new Tunable("attributeList", "Attributes",
		 	                       Tunable.LIST, new Integer(0),
		 	                       getAttributes(), null, 0);
			attrList.addTunableValueListener(this);
			metanodeProperties.add(attrList);
			tunableEnablers.add(attrList);

			typeString = new Tunable("attributeType", "Attribute Type",
			                Tunable.STRING, "",
			                null, null, 0);
			typeString.setImmutable(true);
			metanodeProperties.add(typeString);

			String[] empty = {""};
			typeList = new Tunable("aggregationType", "Aggregation Type",
			                       Tunable.LIST, new Integer(0), empty, null, 0);
			metanodeProperties.add(typeList);
			tunableEnablers.add(typeList);
			// Update
			tunableChanged(attrList);
		}
		updateSettings(true);
	}

	public void updateSettings(boolean force) {
		metanodeProperties.updateValues();

		// Get our defaults and pass them down to the AttributeHandler
		Tunable t = metanodeProperties.get("enableHandling");
		if ((t != null) && (t.valueChanged() || force)) {
      boolean enableHandling = ((Boolean) t.getValue()).booleanValue();
			AttributeHandler.setEnable(enableHandling);
			enableTunables(enableHandling);
		}

		// For each default value, get the default and set it
		t = metanodeProperties.get("stringDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeHandler.setDefault(CyAttributes.TYPE_STRING, (AttributeHandlingType)getListValue(t));
		}
		t = metanodeProperties.get("intDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeHandler.setDefault(CyAttributes.TYPE_INTEGER, (AttributeHandlingType)getListValue(t));
		}
		t = metanodeProperties.get("doubleDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeHandler.setDefault(CyAttributes.TYPE_FLOATING, (AttributeHandlingType)getListValue(t));
		}
		t = metanodeProperties.get("listDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeHandler.setDefault(CyAttributes.TYPE_SIMPLE_LIST, (AttributeHandlingType)getListValue(t));
		}
		t = metanodeProperties.get("booleanDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeHandler.setDefault(CyAttributes.TYPE_BOOLEAN, (AttributeHandlingType)getListValue(t));
		}

	}

	public void revertSettings() {
		metanodeProperties.revertProperties();
		AttributeHandler.revertSettings();
	}

	private String[] getAttributes() {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		List<String> attributeList = new ArrayList();
		makeAttributes("node", nodeAttributes, attributeList);
		makeAttributes("edge", edgeAttributes, attributeList);
		String [] a = new String[1];
		attributeArray = attributeList.toArray(a);
		Arrays.sort(attributeArray);
		return attributeArray;
	}

	private void makeAttributes(String prefix, CyAttributes attrs, List<String>list) {
		// Build a list
		String[] names = attrs.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (!attrs.getUserVisible(names[i]))
				continue;
			list.add(prefix+"."+names[i]);
		}
	}

	public void componentHidden(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {
		doLayout();
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals("done")) {
			updateSettings(true);
			setVisible(false);
		} else if (command.equals("save")) {
			updateSettings(true);
			metanodeProperties.saveProperties();
		} else if (command.equals("cancel")) {
			revertSettings();
			setVisible(false);
		} else if (command.equals("clear")) {
			AttributeHandler.clearSettings();
			tunableChanged(attrList);
		}
	}

	public void tunableChanged(Tunable t) {
		if (t.getName().equals("enableHandling")) {
      boolean enableHandling = ((Boolean) t.getValue()).booleanValue();
			AttributeHandler.setEnable(enableHandling);
			enableTunables(enableHandling);
		} else if (t.getName().equals("attributeList")) {
			CyAttributes attrs = null;

			// Get the attribute
			String attributeWP = (String)getListValue(t);

			if (attributeWP == null)
				return;

			// Strip prefix
			String attribute = attributeWP.substring(5);

			if (attributeWP.startsWith("edge"))
				attrs = Cytoscape.getEdgeAttributes();
			else
				attrs = Cytoscape.getNodeAttributes();

			byte type = attrs.getType(attribute);

			// Get the list
			AttributeHandlingType[] handlingTypes = AttributeHandler.getHandlingOptions(type);

			// Set the name
			typeString.setValue(attributeName(type));

			typeList.removeTunableValueListener(this);
			// Update the list
			typeList.setLowerBound(handlingTypes);

			// Do we already have a handler?
			AttributeHandler handler = AttributeHandler.getHandler(attributeWP);
			if (handler != null) {
				// Yes, show the right one to the user
				for (int i = 0; i < handlingTypes.length; i++) {
					if (handler.getHandlerType() == handlingTypes[i]) {
						typeList.setValue(Integer.valueOf(i));
						break;
					}
				}
			}
			typeList.addTunableValueListener(this);
		} else if (t.getName().equals("aggregationType")) {
			// Get the attribute
			String attributeWP = (String)getListValue(attrList);

			// Get the handler type
			AttributeHandlingType handlerType = (AttributeHandlingType)getListValue(t);

			// Create the handler
			AttributeHandler.addHandler(attributeWP, handlerType);
		}
	}

	private String attributeName(byte type) {
		switch (type) {
			case CyAttributes.TYPE_BOOLEAN:
				return "Boolean";
			case CyAttributes.TYPE_INTEGER:
				return "Integer";
			case CyAttributes.TYPE_FLOATING:
				return "Floating-point";
			case CyAttributes.TYPE_STRING:
				return "String";
			case CyAttributes.TYPE_SIMPLE_LIST:
				return "Simple List";
			case CyAttributes.TYPE_SIMPLE_MAP:
				return "Simple Map";
			case CyAttributes.TYPE_COMPLEX:
				return "Complex type (unsupported)";
			default:
				return "Undefined";
		}
	}

	private Object getListValue(Tunable t) {
		// Get the index
		int attributeIndex = ((Integer)t.getValue()).intValue();

		// Get the original array
		Object[] array = (Object [])t.getLowerBound();

		return array[attributeIndex];
	}

	private void enableTunables(boolean enableHandling) {
		for (Tunable t: tunableEnablers) {
			// System.out.println("Setting immutable for "+t+" to "+(!enableHandling));
		 	t.setImmutable(!enableHandling);
		}
		doLayout();
		pack();
	}
}
