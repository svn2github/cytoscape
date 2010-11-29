/* vim: set ts=2:

  File: MetanodeSettingsDialog.java

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

import metaNodePlugin2.MetaNodeGroupViewer;
import metaNodePlugin2.model.MetaNode;
import metaNodePlugin2.model.MetaNodeManager;
import metaNodePlugin2.model.MetanodeProperties;
import metaNodePlugin2.data.AttributeHandler;
import metaNodePlugin2.data.AttributeHandlingType;
import metaNodePlugin2.data.AttributeManager;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualStyle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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


/**
 *
 * The MetanodeSettingsDialog is a dialog that provides an interface into all of the
 * options for the various metanode settings
 */
public class MetanodeSettingsDialog extends JDialog 
                                    implements ActionListener, TunableListener, ComponentListener {

	private MetanodeProperties metanodeProperties;
	private Tunable typeString = null;
	private Tunable typeList = null;
	private Tunable attrList = null;
	private Tunable nodeChartAttrList = null;
	private Tunable nodeChartTypeList = null;
	private String[] attributeArray = null;
	private List<Tunable>tunableEnablers = null;
	private List<Tunable>nodeChartEnablers = null;
	private boolean hideMetaNode = true;
  private boolean enableHandling = false;
  private boolean useNestedNetworks = false;
	private double metanodeOpacity = 255.0f;
	private String chartType = null;
	private String nodeChartAttribute = null;
	private MetaNodeGroupViewer groupViewer = null;
	private static String NONETYPE = "-- None --";

	// Dialog components
	JPanel tunablePanel = null;

	/**
	 * Create a new settings dialog
	 *
	 * @param viewer the group viewer
	 */
	public MetanodeSettingsDialog(MetaNodeGroupViewer viewer) {
		super(Cytoscape.getDesktop(), "Metanode Settings Dialog", false);
		metanodeProperties = new MetanodeProperties("metanode");
		this.groupViewer = viewer;

		initializeProperties();

		AttributeManager.saveSettings();

		initialize();
		pack();
	}

	/**
	 * Initialize the dialog
	 */
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

		{
			JButton button = new JButton("Save Settings");
			button.setActionCommand("save");
			button.addActionListener(this);
			buttonBox.add(button);
		}

		{
			JButton button = new JButton("Clear Settings");
			button.setActionCommand("clear");
			button.addActionListener(this);
			buttonBox.add(button);
		}

		{
			JButton button = new JButton("Apply to selected");
			button.setActionCommand("applySelected");
			button.addActionListener(this);
			buttonBox.add(button);
		}

		{
			JButton button = new JButton("Apply to all");
			button.setActionCommand("applyAll");
			button.addActionListener(this);
			buttonBox.add(button);
		}

		{
			JButton button = new JButton("Cancel");
			button.setActionCommand("cancel");
			button.addActionListener(this);
			buttonBox.add(button);
		}

		{
			JButton button = new JButton("Done");
			button.setActionCommand("done");
			button.addActionListener(this);
			buttonBox.add(button);
		}

		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		mainPanel.add(buttonBox);
		setContentPane(mainPanel);
	}


	/**
	 * Create all of our properties
	 */
	private void initializeProperties() {
		tunableEnablers = new ArrayList<Tunable>();
		nodeChartEnablers = new ArrayList<Tunable>();

		
		metanodeProperties.add(new Tunable("appearanceGroup", "Metanode Appearance",
		                                   Tunable.GROUP, new Integer(2),
		                                   new Boolean(true), null, Tunable.COLLAPSABLE));
		{
			{
				Tunable t = new Tunable("useNestedNetworks",
				                        "Create a nested network for collapsed metanodes",
				                        Tunable.BOOLEAN, new Boolean(false), 0);
				t.addTunableValueListener(this);
				metanodeProperties.add(t);
			}

			// Sliders always look better when grouped
			metanodeProperties.add(new Tunable("opacityGroup", "Metanode Opacity",
		 	                                  Tunable.GROUP, new Integer(1),
		 	                                  null, null, 0));

			{
				Tunable t = new Tunable("metanodeOpacity",
				                        "Percent opacity of collapsed metanodes",
				                        Tunable.DOUBLE, new Double(0), new Double(0), new Double(100), Tunable.USESLIDER);
				t.addTunableValueListener(this);
				metanodeProperties.add(t);
			}
		}

		{
			Tunable t = new Tunable("enableHandling",
			                        "Enable Attribute Aggregation",
			                        Tunable.BOOLEAN, new Boolean(false), 0);
			t.addTunableValueListener(this);
			metanodeProperties.add(t);
		}

		// If we have nodeCharts, provide chart options for aggregated attributes
		{
			Tunable t = new Tunable("nodeChartsGroup", "Node Chart Options",
			                        Tunable.GROUP, new Integer(2),
		                          new Boolean(true), null, Tunable.COLLAPSABLE);
			metanodeProperties.add(t);

			{
				// Get the attribute to map
				nodeChartAttrList = new Tunable("nodeChartAttribute", "Attribute to use for node chart",
		 	 	                                Tunable.LIST, new Integer(0),
		 	 	                                getNodeAttributes(), null, 0);
				nodeChartEnablers.add(nodeChartAttrList);
				metanodeProperties.add(nodeChartAttrList);
				nodeChartAttrList.addTunableValueListener(this);

				// Get the chart type (might change depending on the chart type)
				nodeChartTypeList = new Tunable("chartType", "Chart type",
		 	 	                                Tunable.LIST, new Integer(0),
		 	 	                                getChartTypes(), null, 0);
				nodeChartEnablers.add(nodeChartTypeList);
				metanodeProperties.add(nodeChartTypeList);
				nodeChartTypeList.addTunableValueListener(this);
			}
		}

		metanodeProperties.add(new Tunable("defaultsGroup", "Defaults",
		                                   Tunable.GROUP, new Integer(5),
		                                   new Boolean(true), null, Tunable.COLLAPSABLE));
		{
			Tunable t = new Tunable("stringDefaults", "String Attributes",
		 	                        Tunable.LIST, new Integer(0),
		 	                        AttributeManager.getStringOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);

			t = new Tunable("intDefaults", "Integer Attributes",
		 	                                  Tunable.LIST, new Integer(0),
		 	                                  AttributeManager.getIntOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);

			t = new Tunable("doubleDefaults", "Double Attributes",
		 	                                  Tunable.LIST, new Integer(0),
		 	                                  AttributeManager.getDoubleOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);

			t = new Tunable("listDefaults", "List Attributes",
		 	                                  Tunable.LIST, new Integer(0),
		 	                                  AttributeManager.getListOptions(), null, 0);
			metanodeProperties.add(t);
			tunableEnablers.add(t);

			t = new Tunable("booleanDefaults", "Boolean Attributes",
		 	                                  Tunable.LIST, new Integer(0),
		 	                                  AttributeManager.getBooleanOptions(), null, 0);
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

			String[] empty = {"(no override)"};
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
      enableHandling = ((Boolean) t.getValue()).booleanValue();
			AttributeManager.setEnable(enableHandling);
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
			enableTunables(enableHandling);
		}

		t = metanodeProperties.get("hideMetanodes");
		if ((t != null) && (t.valueChanged() || force)) {
      hideMetaNode = ((Boolean) t.getValue()).booleanValue();
			MetaNodeManager.setHideMetaNodeDefault(hideMetaNode);
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = metanodeProperties.get("useNestedNetworks");
		if ((t != null) && (t.valueChanged() || force)) {
      useNestedNetworks = ((Boolean) t.getValue()).booleanValue();
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = metanodeProperties.get("metanodeOpacity");
		if ((t != null) && (t.valueChanged() || force)) {
      metanodeOpacity = ((Double) t.getValue()).doubleValue()*255/100;
			MetaNodeManager.setExpandedOpacityDefault(metanodeOpacity);
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = metanodeProperties.get("nodeChartAttribute");
		if ((t != null) && (t.valueChanged() || force)) {
			nodeChartAttribute = (String)getListValue(t);
			MetaNodeManager.setNodeChartAttributeDefault(nodeChartAttribute);
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = metanodeProperties.get("chartType");
		if ((t != null) && (t.valueChanged() || force)) {
			chartType = (String)getListValue(t);
			MetaNodeManager.setChartTypeDefault(chartType);
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}


		// For each default value, get the default and set it
		t = metanodeProperties.get("stringDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeManager.setDefault(CyAttributes.TYPE_STRING, (AttributeHandlingType)getListValue(t));
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}
		t = metanodeProperties.get("intDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeManager.setDefault(CyAttributes.TYPE_INTEGER, (AttributeHandlingType)getListValue(t));
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}
		t = metanodeProperties.get("doubleDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeManager.setDefault(CyAttributes.TYPE_FLOATING, (AttributeHandlingType)getListValue(t));
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}
		t = metanodeProperties.get("listDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeManager.setDefault(CyAttributes.TYPE_SIMPLE_LIST, (AttributeHandlingType)getListValue(t));
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}
		t = metanodeProperties.get("booleanDefaults");
		if ((t != null) && (t.valueChanged() || force)) {
			AttributeManager.setDefault(CyAttributes.TYPE_BOOLEAN, (AttributeHandlingType)getListValue(t));
			metanodeProperties.setProperty(t.getName(), t.getValue().toString());
		}
	}

	/**
	 * Revert our settings
	 */
	public void revertSettings() {
		metanodeProperties.revertProperties();
		AttributeManager.revertSettings();
	}

	/**
	 * Return our settings
	 *
	 * @return our properties
	 */
	public MetanodeProperties getSettings() {
		return metanodeProperties;
	}

	/**
	 * Update all of our attribute manager override values
	 *
	 * @param network the network we're updating our override values for
	 */
	public void updateOverrides(CyNetwork network) {
		AttributeManager.loadHandlerMappings(network);
	}

	/**
	 * Update the tunables that list attribute values
	 */
	public void updateAttributes() {
		attrList.setLowerBound(getAttributes());
		nodeChartAttrList.setLowerBound(getNodeAttributes());
	}

	/**
	 * Update our list of node chart types
	 */
	public void updateNodeChartTypes() {
		if (groupViewer.checkNodeCharts()) {
			enableNodeCharts(true);
			nodeChartTypeList.setLowerBound(getChartTypes());
		} else
			enableNodeCharts(false);
	}

	/**
	 * Return 'true' if we're supposed to use the nested network viewer
	 */
	public boolean getUseNestedNetworks() {
		updateSettings(false);
		String bv = (String)metanodeProperties.getProperties().get("useNestedNetworks");
		useNestedNetworks = Boolean.parseBoolean(bv);
		return useNestedNetworks;
	}

	/**
	 * Return all of the node attributes as a string array
	 *
	 * @return array of node attributes
	 */
	private String[] getNodeAttributes() {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		List<String> attributeList = new ArrayList();
		makeAttributes(null, nodeAttributes, attributeList);
		String [] a = new String[1];
		attributeArray = attributeList.toArray(a);
		Arrays.sort(attributeArray);
		return attributeArray;
	}

	/**
	 * Return all of the attributes as a string array
	 *
	 * @return array of attributes
	 */
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

	/**
	 * Return all of the chart types as a string array
	 *
	 * @return array of chart types
	 */
	private String[] getChartTypes() {
		String [] a = new String[1];
		List<String> viewerList = groupViewer.getChartTypes();
		viewerList.add(NONETYPE);
		String [] viewerArray = viewerList.toArray(a);
		Arrays.sort(viewerArray);
		return viewerArray;
	}

	private void makeAttributes(String prefix, CyAttributes attrs, List<String>list) {
		// Build a list
		String[] names = attrs.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (!attrs.getUserVisible(names[i]))
				continue;
			if (prefix != null)
				list.add(prefix+"."+names[i]);
			else
				list.add(names[i]);
		}
	}

	/**
	 * Methods to support the ComponentListener interface
	 */
	public void componentHidden(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {
		doLayout();
		pack();
	}

	/**
	 * This method is called when the user performs an action
	 */
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
			AttributeManager.clearSettings();
			tunableChanged(attrList);

		// Apply the current settings to the selected metanodes
		} else if (command.equals("applySelected")) {
			updateSettings(true);
			// Get all selected nodes
			Set<CyNode> nodeList = Cytoscape.getCurrentNetwork().getSelectedNodes();
			for (CyNode node: nodeList) {
				MetaNode mn = MetaNodeManager.getMetaNode(node);
				if (mn != null) {
					updateMetaNodeSettings(mn);
				}
			}
			setVisible(false);

		// Apply the current settings to all metanodes
		} else if (command.equals("applyAll")) {
			updateSettings(true);
			// Get the list of groups
			List<CyGroup> groupList = CyGroupManager.getGroupList(groupViewer);
			if (groupList != null && groupList.size() > 0) {
				// Update them
				for (CyGroup group: groupList) 	{
					MetaNode mn = MetaNodeManager.getMetaNode(group);
					if (mn != null) {
						updateMetaNodeSettings(mn);
					}
				}
			}
			setVisible(false);
		}
	}

	private void updateMetaNodeSettings(MetaNode mn) {
		mn.setUseNestedNetworks(useNestedNetworks);
		// mn.setSizeToBoundingBox(sizeToBoundingBox);
		mn.setHideMetaNode(hideMetaNode);
		mn.setAggregateAttributes(enableHandling);
		// System.out.println("setting opacity for "+mn+" to "+metanodeOpacity);
		mn.setMetaNodeOpacity(metanodeOpacity);
		mn.setChartType(chartType);
		mn.setNodeChartAttribute(nodeChartAttribute);
	}

	public void tunableChanged(Tunable t) {
		if (t.getName().equals("hideMetanodes")) {
      hideMetaNode = ((Boolean) t.getValue()).booleanValue();
			MetaNodeManager.setHideMetaNodeDefault(hideMetaNode);
		} else if (t.getName().equals("useNestedNetworks")) {
      useNestedNetworks = ((Boolean) t.getValue()).booleanValue();
			MetaNodeManager.setUseNestedNetworksDefault(useNestedNetworks);
		} else if (t.getName().equals("metanodeOpacity")) {
      metanodeOpacity = ((Double) t.getValue()).doubleValue()*255/100;
			MetaNodeManager.setExpandedOpacityDefault(metanodeOpacity);
		} else if (t.getName().equals("enableHandling")) {
      enableHandling = ((Boolean) t.getValue()).booleanValue();
			AttributeManager.setEnable(enableHandling);
			enableTunables(enableHandling);
		} else if (t.getName().equals("nodeChartAttribute")) {
			nodeChartAttribute = (String)getListValue(t);
			MetaNodeManager.setNodeChartAttributeDefault(nodeChartAttribute);
		} else if (t.getName().equals("chartType")) {
			chartType = (String)getListValue(t);
			MetaNodeManager.setChartTypeDefault(chartType);
		} else if (t.getName().equals("attributeList")) {
			String attributeWP = (String)getListValue(t);
			if (attributeWP == null)
				return;
			updateAttributeHandlers(attributeWP);
		} else if (t.getName().equals("aggregationType")) {
			// Get the attribute
			String attributeWP = (String)getListValue(attrList);

			// Get the handler type
			AttributeHandlingType handlerType = (AttributeHandlingType)getListValue(t);

			// Create the handler
			AttributeManager.addHandler(attributeWP, handlerType);
		}
		repaint();
	}

	private void updateAttributeHandlers(String attributeWP) {
		CyAttributes attrs = null;

		// Strip prefix
		String attribute = attributeWP.substring(5);

		if (attributeWP.startsWith("edge"))
			attrs = Cytoscape.getEdgeAttributes();
		else
			attrs = Cytoscape.getNodeAttributes();

		byte type = attrs.getType(attribute);

		// Get the list
		AttributeHandlingType[] hTypes = AttributeManager.getHandlingOptions(type);
		AttributeHandlingType[] handlingTypes = new AttributeHandlingType[hTypes.length+1];

		handlingTypes[0] = AttributeHandlingType.DEFAULT;
		for (int i = 0; i < hTypes.length; i++) {
			handlingTypes[i+1] = hTypes[i];
		}

		// Set the name
		typeString.setValue(CyAttributesUtils.toString(type));

		typeList.removeTunableValueListener(this);
		// Update the list
		typeList.setLowerBound(handlingTypes);

		// Do we already have a handler?
		AttributeHandler handler = AttributeManager.getHandler(attributeWP);
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
		return;
	}

	private Object getListValue(Tunable t) {
		// Get the index
		int attributeIndex = ((Integer)t.getValue()).intValue();
		if (attributeIndex < 0)
			return null;

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

	private void enableNodeCharts(boolean enableHandling) {
		for (Tunable t: nodeChartEnablers) {
			// System.out.println("Setting immutable for "+t+" to "+(!enableHandling));
		 	t.setImmutable(!enableHandling);
		}
		doLayout();
		pack();
	}

	private void modifyVizMap() {
		// Get the current Visual Style
		VisualStyle currentStyle = Cytoscape.getCurrentNetworkView().getVisualStyle();

		// Now set the dependency
		currentStyle.getDependency().set(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED, false);
	}
}
