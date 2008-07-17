/*
  File: TunableSampler.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

package tunableSampler;

import cytoscape.Cytoscape;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.layout.LayoutProperties;
import cytoscape.plugin.CytoscapePlugin;

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
 * TunableSampler presents a simple list of Tunables to demonstrate some of
 * the capabilities Tunables offer.
 */
public class TunableSampler extends CytoscapePlugin implements ActionListener, TunableListener {
	LayoutProperties properties = null;

	// Tunable values
	boolean testBool = false;
	int testInt = 0;
	int testIntSlider = 0;
	double testFloatSlider = 0;
	String edgeAttribute = null;
	List<String> nodeAttributes = null;
	int singleList = 0;
	int[] multiList = null;
	String textInput = null;

	List<String>numberList = null;
	List<String>nameList = null;
	List<String>colorList = null;

	// Tunables we want to remember
	Tunable setTextTunable = null;
	Tunable multiListTunable = null;
	Tunable integerSlider = null;
	Tunable doubleSlider = null;

	/**
	 * The constructor does very little.  
	 */
	public TunableSampler() {
		JMenuItem menu = new JMenuItem("Tunable Sampler");
		menu.addActionListener(this);
		menu.setActionCommand("initialize");
		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins");
		pluginMenu.add(menu);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("initialize")) {
			// Create the properties list
			properties = new LayoutProperties("tunableSampler");

			// Initialize our Tunables
			initializeTunables();

			// Create our dialog
			SamplerDialog dialog = new SamplerDialog(properties, this);
		} else if (command.equals("buttonTest")) {
			// Update all of our values and print them out
			updateTunables(true);
			System.out.println("Tunable values: ");
			for (Tunable t: properties.getTunables()) {
				if (t.getType() == Tunable.GROUP || t.getType() == Tunable.BUTTON)
					continue;
				System.out.println("Tunable "+t.getName()+" = "+t.getValue());
			}
		}
	}

	private void initializeTunables() {
		{
			Tunable t = new Tunable("testBool", "Test Boolean Value",
		                            Tunable.BOOLEAN, new Boolean(false),
		                            0);
			properties.add(t);
		}

		properties.add(new Tunable("group1", "Numeric Tunables",
		                            Tunable.GROUP, new Integer(5)));

		{
			Tunable t = new Tunable("testInt", "Test Bounded Integer Value",
		                            Tunable.INTEGER, new Integer(10),
		                            new Integer(-100), new Integer(100),
		                            0);
			properties.add(t);
		}
		{
			// Sliders look better when they are grouped
			properties.add(new Tunable("intSliderGroup", "Test Bounded Integer Value (slider)",
		                            Tunable.GROUP, new Integer(1)));

			integerSlider = new Tunable("testIntSlider", "",
		                            Tunable.INTEGER, new Integer(10),
		                            new Integer(-100), new Integer(100),
		                            Tunable.USESLIDER);
			properties.add(integerSlider);
		}
		{
			// Sliders look better when they are grouped
			properties.add(new Tunable("doubleSliderGroup", "Test Bounded Double Value (slider)",
		                            Tunable.GROUP, new Integer(1)));

			doubleSlider = new Tunable("testFloatSlider", "",
		                            Tunable.DOUBLE, new Double(1.1),
		                            new Double(-10), new Double(10),
		                            Tunable.USESLIDER);
			properties.add(doubleSlider);
		}

		properties.add(new Tunable("group2", "Attribute Tunables",
		                            Tunable.GROUP, new Integer(2), 
		                            new Boolean(true), null, Tunable.COLLAPSABLE));

		{
			ArrayList<String>initialValues = new ArrayList();
			initialValues.add("Initial (empty) value");
			Tunable t = new Tunable("edgeAttribute", "Single-valued edge attribute",
		                            Tunable.EDGEATTRIBUTE, "interaction",
		                            initialValues, null,
		                            0);
			properties.add(t);
		}
		{
			ArrayList<String>initialValues = new ArrayList();
			initialValues.add("Initial (empty) value");
			Tunable t = new Tunable("nodeAttribute", "Multiselect numeric node attribute",
		                            Tunable.NODEATTRIBUTE, "",
		                            initialValues, null,
		                            Tunable.NUMERICATTRIBUTE|Tunable.MULTISELECT);
			properties.add(t);
		}

		properties.add(new Tunable("group3", "List Tunables",
		                            Tunable.GROUP, new Integer(2)));
		{
			ArrayList<String>values = new ArrayList();
			values.add("Numbers");
			values.add("Colors");
			values.add("Names");
			// Note that the initial selected value is an Integer in this case
			Tunable t = new Tunable("singleList", "Single list",
		                            Tunable.LIST, new Integer(1),
		                            (Object)values.toArray(), null, 0);
			// Add a listener to this tunable
			t.addTunableValueListener(this);
			properties.add(t);
		}
		{
			colorList = new ArrayList();
			colorList.add("Blue");
			colorList.add("Red");
			colorList.add("Green");
			colorList.add("Cyan");
			colorList.add("Magenta");
			numberList = new ArrayList();
			numberList.add("One");
			numberList.add("Two");
			numberList.add("Three");
			numberList.add("Four");
			nameList = new ArrayList();
			nameList.add("George");
			nameList.add("Jane");
			nameList.add("Herb");
			nameList.add("Bob");
			nameList.add("Sarah");
			nameList.add("Suzanne");
			// Note that the initial selected values are encoded as an int string
			multiListTunable = new Tunable("multiList", "Multiple Selection list",
		                                 Tunable.LIST, "1,3",
		                                 (Object)(colorList.toArray()), null, Tunable.MULTISELECT);
			properties.add(multiListTunable);
		}

		properties.add(new Tunable("group4", "String Tunables",
		                            Tunable.GROUP, new Integer(2)));

		{
			Tunable t = new Tunable("textInput", "Text Input",
		                            Tunable.STRING, "",
		                            null, null, 0);
			// Add a listener to this tunable
			t.addTunableValueListener(this);
			properties.add(t);
		}
		{
			setTextTunable = new Tunable("immutableTextInput", "Immutable Text Input",
		                            Tunable.STRING, "",
		                            null, null, 0);
			setTextTunable.setImmutable(true);
			properties.add(setTextTunable);
		}

		properties.add(new Tunable("group5", "Interaction tests",
		                            Tunable.GROUP, new Integer(3)));

		{
			Tunable t = new Tunable("setIntegerBounds", "Reset Integer Slider Lower Bounds",
		                            Tunable.INTEGER, "",
		                            null, null, 0);
			t.addTunableValueListener(this);
			properties.add(t);
		}
		{
			Tunable t = new Tunable("setDoubleBounds", "Reset Double Slider Upper Bounds",
		                            Tunable.DOUBLE, "",
		                            null, null, 0);
			t.addTunableValueListener(this);
			properties.add(t);
		}

		{
			Tunable t = new Tunable("buttonTest", "Click to get all values",
			                        Tunable.BUTTON, "Get Values", this, null, 0);
			properties.add(t);
		}
	}

	public void updateTunables(boolean force) {
		Tunable t = properties.get("testBool");
    if ((t != null) && (t.valueChanged() || force))
      testBool = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("testInt");
    if ((t != null) && (t.valueChanged() || force))
      testInt = ((Integer) t.getValue()).intValue();

		t = properties.get("testIntSlider");
    if ((t != null) && (t.valueChanged() || force))
      testIntSlider = ((Integer) t.getValue()).intValue();

		t = properties.get("testFloatSlider");
    if ((t != null) && (t.valueChanged() || force))
      testFloatSlider = ((Double) t.getValue()).doubleValue();

		t = properties.get("edgeAttribute");
    if ((t != null) && (t.valueChanged() || force))
      edgeAttribute = (String) t.getValue();

		t = properties.get("nodeAttributes");
    if ((t != null) && (t.valueChanged() || force)) {
      String nodeList = (String) t.getValue();
			String[] nodeArray = nodeList.split(",");
			nodeAttributes = Arrays.asList(nodeArray);
		}

		// LISTs return an index into the list
		t = properties.get("singleList");
    if ((t != null) && (t.valueChanged() || force))
      singleList = ((Integer) t.getValue()).intValue();

		// LISTs with MULTISELECT return an index into the list
		t = properties.get("multiList");
    if ((t != null) && (t.valueChanged() || force)) {
      String selection = (String) t.getValue();
			String[] selInts = selection.split(",");
			multiList = new int[selInts.length];
			for (int i = 0; i < selInts.length; i++)
				multiList[i] = Integer.parseInt(selInts[i]);
		}

		t = properties.get("textInput");
    if ((t != null) && (t.valueChanged() || force))
      textInput = (String) t.getValue();
	}

	public void revertTunables() {
		properties.revertProperties();
	}

	public void tunableChanged(Tunable t) {
		if (t.getName().equals("textInput")) {
			setTextTunable.setValue("Tunable "+t.getName()+" was set to "+t.getValue());
		} else if (t.getName().equals("singleList")) {
			int index = ((Integer)t.getValue()).intValue();
			setTextTunable.setValue("Tunable "+t.getName()+" was set to "+index);
			if (index == 0) {
				multiListTunable.setLowerBound(numberList.toArray());
			} else if (index == 1) {
				multiListTunable.setLowerBound(colorList.toArray());
			} else if (index == 2) {
				multiListTunable.setLowerBound(nameList.toArray());
			}
		} else if (t.getName().equals("setIntegerBounds")) {
			integerSlider.setLowerBound(t.getValue());
		} else if (t.getName().equals("setDoubleBounds")) {
			doubleSlider.setUpperBound(t.getValue());
		}
	}

	// For simplicity, I've implemented this as an inner class, but in reality
	// I would always recommend a cleaner division where this would be in a
	// separate tunableSampler.ui package.
	public class SamplerDialog extends JDialog implements ActionListener,ComponentListener {
		LayoutProperties properties = null;
		JPanel tunablePanel = null;
		TunableSampler caller = null;

		public SamplerDialog(LayoutProperties properties, TunableSampler caller) {
			super(Cytoscape.getDesktop(), "Tunable Sampler Dialog", false);
			this.properties = properties;
			this.caller = caller;
			initialize();
			pack();
			setVisible(true);
		}

		private void initialize() {
			setDefaultCloseOperation(HIDE_ON_CLOSE);

			// Create our main panel
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

			// Create a panel for the Tunables
			this.tunablePanel = properties.getTunablePanel();

			tunablePanel.addComponentListener(this);

			Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder, "Tunable Settings");
			titleBorder.setTitlePosition(TitledBorder.LEFT);
			titleBorder.setTitlePosition(TitledBorder.TOP);
			tunablePanel.setBorder(titleBorder);
			mainPanel.add(tunablePanel);

    	// Create a panel for our button box
      JPanel buttonBox = new JPanel();

			JButton doneButton = new JButton("Done");
			doneButton.setActionCommand("done");
			doneButton.addActionListener(this);
	
			JButton saveButton = new JButton("Save Settings");
			saveButton.setActionCommand("save");
			saveButton.addActionListener(this);

			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("cancel");
			cancelButton.addActionListener(this);
			buttonBox.add(saveButton);
			buttonBox.add(cancelButton);
			buttonBox.add(doneButton);
			buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			mainPanel.add(buttonBox);
			setContentPane(mainPanel);
		}

		public void componentHidden(ComponentEvent e) { }

		public void componentMoved(ComponentEvent e) { }

		public void componentResized(ComponentEvent e) {
			doLayout();
			pack();
		}

		public void componentShown(ComponentEvent e) { }

		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			if (command.equals("done")) {
				caller.updateTunables(true);
				setVisible(false);
			} else if (command.equals("save")) {
				caller.updateTunables(true);
				properties.saveProperties();
			} else if (command.equals("cancel")) {
				caller.revertTunables();
				setVisible(false);
			}
		}
	}
}
