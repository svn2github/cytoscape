/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package chemViz.ui;

import chemViz.model.Compound.AttriType;
import chemViz.model.ChemInfoProperties;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import giny.model.GraphObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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


public class ChemInfoSettingsDialog extends JDialog implements ActionListener, PropertyChangeListener {

	private static final String defaultSmilesAttributes[] = {"SMILES","Compounds","Compound","Smiles","smiles"};
	private static final String defaultInCHIAttributes[] = {"InCHI","inchi","InChi","InChI"};
	private static List<String> smilesAttributes = null;
	private static List<String> inCHIAttributes = null;
	private ChemInfoProperties properties;
	private JPanel tunablePanel;
	private int maxCompounds = 0;
	private double tcCutoff = 0.25;

	public ChemInfoSettingsDialog() {
		super(Cytoscape.getDesktop(), "ChemViz Plugin Settings Dialog", false);

		properties = new ChemInfoProperties("chemViz");

		initializeProperties();

		// Listen for ATTRIBUTES_CHANGED so we can update our Tunables in response
		Cytoscape.getPropertyChangeSupport()
					.addPropertyChangeListener(
							Cytoscape.NETWORK_CREATED, this);
		Cytoscape.getPropertyChangeSupport()
					.addPropertyChangeListener(
							Cytoscape.ATTRIBUTES_CHANGED, this);

		initialize();
		pack();
	}

	public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("done")) {
      updateSettings(true);
      setVisible(false);
    } else if (command.equals("save")) {
      updateSettings(true);
      properties.saveProperties();
    } else if (command.equals("cancel")) {
      properties.revertProperties();
      setVisible(false);
    } else if (command.equals("clear")) {
			updateAttributeTunables();
    }
	}

	public List<String> getNodeCompoundAttributes() {
		List<String>attrList = new ArrayList();
		attrList.addAll(getCompoundAttributes("node",AttriType.smiles));
		attrList.addAll(getCompoundAttributes("node",AttriType.inchi));
		return attrList;
	}

	public List<String> getEdgeCompoundAttributes() {
		List<String>attrList = new ArrayList();
		attrList.addAll(getCompoundAttributes("edge",AttriType.smiles));
		attrList.addAll(getCompoundAttributes("edge",AttriType.inchi));
		return attrList;
	}

	public List<String> getCompoundAttributes(String objType, AttriType type) {
		List<String> attrs = null;
		List<String> resultList = new ArrayList();

		if (type == AttriType.smiles) {
			if (smilesAttributes == null) {
				resultList.addAll(Arrays.asList(defaultSmilesAttributes));
			} else
				attrs = smilesAttributes;
		} else  {
			if (inCHIAttributes == null)
				resultList.addAll(Arrays.asList(defaultInCHIAttributes));
			else
				attrs = inCHIAttributes;
		}

		if (attrs == null) return resultList;

		for (String str: attrs) {
			if (str.startsWith(objType+".")) {
				resultList.add(str.substring(5));
			}
		}
		return resultList;
	}

	public void updateSettings(boolean force) {
		properties.updateValues();

		Tunable t = properties.get("smilesAttributes");
		if ((t != null) && (t.valueChanged() || force)) {
			smilesAttributes = getListFromTunable((Object [])t.getLowerBound(), (String)t.getValue());
		}

		t = properties.get("inChiAttributes");
		if ((t != null) && (t.valueChanged() || force)) {
			inCHIAttributes = getListFromTunable((Object [])t.getLowerBound(), (String)t.getValue());
		}

		t = properties.get("maxCompounds");
		if ((t != null) && (t.valueChanged() || force)) {
			maxCompounds = ((Integer) t.getValue()).intValue();
		}

		t = properties.get("tcCutoff");
		if ((t != null) && (t.valueChanged() || force)) {
			tcCutoff = ((Double) t.getValue()).doubleValue();
		}
	}

	public ChemInfoProperties getProperties() {
		return properties;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == Cytoscape.NETWORK_CREATED ||
		    evt.getPropertyName() == Cytoscape.ATTRIBUTES_CHANGED) {
			updateAttributeTunables();
		}
	}

	public boolean hasNodeCompounds(Collection<CyNode> nodeSet) {
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		List<String> attrsFound = getMatchingAttributes(attributes, getNodeCompoundAttributes());

		if (attrsFound.size() == 0)
			return false;

		if (nodeSet == null)
			return true;

		// We we know all of the attributes we're interested in -- see if these objects have any of them
		for (CyNode node: nodeSet) {
			for (String attribute: attrsFound) {
				if (attributes.hasAttribute(node.getIdentifier(),attribute)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasEdgeCompounds(Collection<CyEdge> edgeSet) {
		CyAttributes attributes = Cytoscape.getEdgeAttributes();
		List<String> attrsFound = getMatchingAttributes(attributes, getEdgeCompoundAttributes());

		if (attrsFound.size() == 0)
			return false;

		if (edgeSet == null)
			return true;

		// We we know all of the attributes we're interested in -- see if these objects have any of them
		ArrayList<String>hasAttrs = new ArrayList();
		for (CyEdge edge: edgeSet) {
			for (String attribute: attrsFound) {
				if (attributes.hasAttribute(edge.getIdentifier(),attribute)) {
					hasAttrs.add(attribute);
				}
			}
		}

		if (hasAttrs.size() > 0)
			return true;

		return false;
	}

	public int getMaxCompounds() {
		return maxCompounds;
	}

	public double getTcCutoff() {
		return tcCutoff;
	}

	private List<String> getMatchingAttributes(CyAttributes attributes, List<String> compoundAttributes) {
		// Get the names of all of the object attributes
		String[] attrNames = attributes.getAttributeNames();

		// Now see if any of the attributes are in our list
		ArrayList<String>attrsFound = new ArrayList();
		for (int i = 0; i < attrNames.length; i++) {
			if (compoundAttributes.contains(attrNames[i])) {
				attrsFound.add(attrNames[i]);
			}
		}
		return attrsFound;
	}

	private void updateAttributeTunables() {
		List<String>possibleAttributes = getAllAttributes(Cytoscape.getNodeAttributes(), 
	 	                                                 Cytoscape.getEdgeAttributes());
		// Get the smiles tunable
		Tunable t = properties.get("smilesAttributes");
		t.setLowerBound((Object[])possibleAttributes.toArray());
		String smilesDefaults = getDefaults(possibleAttributes, defaultSmilesAttributes);
		t.setUpperBound(smilesDefaults);
		// Repeat for the inCHI tunable
		t = properties.get("inChiAttributes");
		t.setLowerBound((Object[])possibleAttributes.toArray());
		String inCHIDefaults = getDefaults(possibleAttributes, defaultInCHIAttributes);
		t.setUpperBound(inCHIDefaults);
	}

	private void initializeProperties() {
		Tunable t = new Tunable("group1","", Tunable.GROUP, new Integer(2));
		properties.add(t);

		t = new Tunable("maxCompunds",
		                "Maximum number of compounds to show in 2D structure popup",
		                Tunable.INTEGER, new Integer(0));
		properties.add(t);

		t = new Tunable("tcCutoff",
		                "Minimum tanimoto value to consider for edge creation",
		                Tunable.DOUBLE, new Double(0.25));
		properties.add(t);


		List<String>possibleAttributes = getAllAttributes(Cytoscape.getNodeAttributes(), 
		                                                  Cytoscape.getEdgeAttributes());

		t = new Tunable("attributeGroup",
		                "Attribute Settings",
		                Tunable.GROUP, new Integer(4));
		properties.add(t);

		t = new Tunable("attributeGroup1",
		                "SMILES Attributes",
		                Tunable.GROUP, new Integer(2));
		properties.add(t);
		String smilesDefaults = getDefaults(possibleAttributes, defaultSmilesAttributes);
		t = new Tunable("smilesAttributes",
		                "Attributes that contain SMILES strings",
		                Tunable.LIST, smilesDefaults,
		                (Object)possibleAttributes.toArray(), null, Tunable.MULTISELECT);
		properties.add(t);

		t = new Tunable("attributeGroup2",
		                "InCHI Attributes",
		                Tunable.GROUP, new Integer(2));
		properties.add(t);
		String inCHIDefaults = getDefaults(possibleAttributes, defaultInCHIAttributes);
		t = new Tunable("inChiAttributes",
		                "Attributes that contain InCHI fingerprints",
		                Tunable.LIST, inCHIDefaults,
		                (Object)possibleAttributes.toArray(), null, Tunable.MULTISELECT);
		properties.add(t);

		properties.initializeProperties();
	}

	private List<String> getAllAttributes(CyAttributes nodeAttributes, CyAttributes edgeAttributes) {
		List<String>attributes = new ArrayList();
		getAttributes(attributes,nodeAttributes,"node.");
		getAttributes(attributes,edgeAttributes,"edge.");
		return attributes;
	}

	private void getAttributes(List<String>attributes, CyAttributes attrs, String prefix) {
		String[] names = attrs.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			byte type = attrs.getType(names[i]);
			if ((type == CyAttributes.TYPE_STRING || type == CyAttributes.TYPE_SIMPLE_LIST)
			    && attrs.getUserVisible(names[i])) {
				attributes.add(prefix+names[i]);
			}
		}
	}

	private String getDefaults(List<String>attributes, String[] defaults) {
		String result = null;
		for (int i = 0; i < defaults.length; i++) {
			for (String objType: Arrays.asList("node.","edge.")) {
				int index = attributes.indexOf(objType+defaults[i]);
				if (index >= 0) {
					if (result == null)
						result = ""+index;
					else
						result = result+","+index;
				}
			}
		}
		if (result == null)
			return "0";
		return result;
	}

	private List<String> getListFromTunable(Object [] multiArray, String selectedValues) {
		List<String>valueList = new ArrayList();
		List<Object>multiList = Arrays.asList(multiArray);
		String[] selInts = selectedValues.split(",");
		for (int i = 0; i < selInts.length; i++) {
			if (selInts[i].length() > 0) {
				int index = Integer.parseInt(selInts[i]);
				valueList.add((String)multiList.get(index));
			}
		}
		return valueList;
	}

	private void initialize() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		this.tunablePanel = properties.getTunablePanel();
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

}
