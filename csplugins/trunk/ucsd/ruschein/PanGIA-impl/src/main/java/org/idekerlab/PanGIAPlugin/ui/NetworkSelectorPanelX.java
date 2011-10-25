package org.idekerlab.PanGIAPlugin.ui;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;


public class NetworkSelectorPanelX extends JPanel implements PropertyChangeListener, ActionListener
{
	private static final long serialVersionUID = 8694272457769377810L;
	
	private final JComboBox networkComboBox;
	private String preferredSelection = null;
	private boolean building = false;

	public NetworkSelectorPanelX() {
		super();
		this.setLayout(new BorderLayout());
		networkComboBox = new JComboBox();

		//limit the length of combobox if the network name is too long
		networkComboBox.setPreferredSize(new java.awt.Dimension(networkComboBox.getPreferredSize().width, networkComboBox.getPreferredSize().height));
		
		add(networkComboBox, BorderLayout.CENTER);
		updateNetworkList();
		
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
		
		networkComboBox.addActionListener(this);
	}
	
	/**
	 * If selected, return selected network.
	 * Otherwise, return null.
	 */
	public CyNetwork getSelectedNetwork() {
		for (CyNetwork net : Cytoscape.getNetworkSet()) {
			if (net.getTitle().equals(networkComboBox.getSelectedItem()))
				return net;
		}
		
		return null;
	}

	private void updateNetworkList() {
		
		building = true;
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		final SortedSet<String> networkNames = new TreeSet<String>();

		for (CyNetwork net : networks)
			networkNames.add(net.getTitle());

		networkComboBox.removeAllItems();
		for (String name : networkNames)
			networkComboBox.addItem(name);

		updateSelection();
		
		building = false;
	}

	public void propertyChange(PropertyChangeEvent evt) {

		final String propName = evt.getPropertyName();

		if (propName.equals(Cytoscape.NETWORK_CREATED)||propName.equals(Cytoscape.NETWORK_TITLE_MODIFIED))
			updateNetworkList();
		else if (propName.equals(Cytoscape.NETWORK_DESTROYED))
		{
			networkComboBox.removeItem(Cytoscape.getNetwork((String) evt.getNewValue()).getTitle());
			updateSelection();
		}
	}

	private void updateSelection()
	{
		int count = networkComboBox.getItemCount();
		Set<String> networkNames = new HashSet<String>(count,1);
		
		for (int i=0;i<count;i++)
			networkNames.add(networkComboBox.getItemAt(i).toString());
		
		if ((preferredSelection==null || !networkNames.contains(preferredSelection)) && networkNames.contains(Cytoscape.getCurrentNetwork().getTitle())) networkComboBox.setSelectedItem(Cytoscape.getCurrentNetwork().getTitle());
		else networkComboBox.setSelectedItem(preferredSelection);
	}
	
	/**
	 *  Installs a new item listener for the embedded combo box.
	 */
	public void addItemListener(final ItemListener newListener) {
		networkComboBox.addItemListener(newListener);
	}

	public void setComboBoxToolTip(String text)
	{
		networkComboBox.setToolTipText(text);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (building)
		{
			//updateSelection();
			return;
		}
		
		establishSelected();
		
		//updateSelection();
	}
	
	public void establishSelected()
	{
		if (networkComboBox.getSelectedItem()==null) preferredSelection = null;
		else preferredSelection = networkComboBox.getSelectedItem().toString();
	}
}
