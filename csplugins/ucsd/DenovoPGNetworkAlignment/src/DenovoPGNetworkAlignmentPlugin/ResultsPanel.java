package DenovoPGNetworkAlignmentPlugin;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.cytopanels.CytoPanel;
import giny.model.Node;
import giny.model.RootGraph;

public class ResultsPanel extends JPanel
{
	enum NewNetworkType { SINGLE_NETWORK, MULTIPLE_NETWORKS };

	CyNetwork parentNetwork;
	java.util.List<Result> results;
	NewNetworkType newNetworkType = NewNetworkType.MULTIPLE_NETWORKS;
	ResultsTable resultsTable;
	OptionsDialog optionsDialog = new OptionsDialog();
	JButton createNetworkButton;

	public ResultsPanel(CyNetwork parentNetwork, java.util.List<Result> results)
	{
		this.parentNetwork = parentNetwork;
		this.results = results;

		resultsTable = new ResultsTable();
		JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
		createNetworkButton = new JButton("Create Selected Networks");
		createNetworkButton.setEnabled(false);
		createNetworkButton.addActionListener(new CreateNetworkAction());
		JButton createAllNetworksButton = new JButton("Create All Networks");
		createAllNetworksButton.addActionListener(new CreateAllNetworksAction());
		JButton optionsButton = new JButton("Options...");
		optionsButton.addActionListener(new OptionsListener());
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new CloseAction());

		JPanel buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.add(createNetworkButton);
		buttonsPanel.add(createAllNetworksButton);
		buttonsPanel.add(optionsButton);
		buttonsPanel.add(closeButton);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.gridx = 0;		c.gridy = 0;
		c.gridwidth = 1;	c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;	c.weighty = 1.0;
		add(resultsScrollPane, c);

		c.gridx = 0;		c.gridy = 1;
		c.weightx = 1.0;	c.weighty = 0.0;
		add(buttonsPanel, c);
	}

	private class OptionsListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			optionsDialog.setVisible(true);
		}
	}

	private class CloseAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
			cytoPanel.remove(ResultsPanel.this);
		}
	}

	private class CreateNetworkAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			int[] rows = resultsTable.getSelectedRows();
			ArrayList<Result> modules = new ArrayList<Result>();
			for (int i = 0; i < rows.length; i++)
				modules.add(results.get(rows[i]));
			createNetworks(modules);
		}
	}

	private class CreateAllNetworksAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			createNetworks(results);
		}
	}

	private void createNetworks(java.util.List<Result> modules)
	{
		if (newNetworkType == NewNetworkType.SINGLE_NETWORK)
		{
			Set moduleNodes = new HashSet();
			Iterator parentNodes = parentNetwork.nodesIterator();
			while (parentNodes.hasNext())
			{
				Node node = (Node) parentNodes.next();
				for (Result module : modules)
					if (module.memberNodes.contains(node.getIdentifier()))
						moduleNodes.add(node);
			}

			CyNetwork newNetwork = Cytoscape.createNetwork(moduleNodes, parentNetwork.getConnectingEdges(new ArrayList(moduleNodes)), CyNetworkNaming.getSuggestedSubnetworkTitle(parentNetwork), parentNetwork);

			if (Cytoscape.getNetworkView(newNetwork.getIdentifier()) != null)
				Cytoscape.getNetworkView(newNetwork.getIdentifier()).redrawGraph(true, true);
		}
		else if (newNetworkType == NewNetworkType.MULTIPLE_NETWORKS)
		{
			for (Result module : modules)
			{
				Set moduleNodes = new HashSet();
				Iterator parentNodes = parentNetwork.nodesIterator();
				while (parentNodes.hasNext())
				{
					Node node = (Node) parentNodes.next();
					if (module.memberNodes.contains(node.getIdentifier()))
						moduleNodes.add(node);
				}

				CyNetwork newNetwork = Cytoscape.createNetwork(moduleNodes, parentNetwork.getConnectingEdges(new ArrayList(moduleNodes)), module.startNode, parentNetwork);

				if (Cytoscape.getNetworkView(newNetwork.getIdentifier()) != null)
					Cytoscape.getNetworkView(newNetwork.getIdentifier()).redrawGraph(true, true);
			}
		}
	}

	class OptionsDialog extends JDialog
	{
		JComboBox newNetworkComboBox;
		public OptionsDialog()
		{
			super(Cytoscape.getDesktop(), "DenovoPGNetworkAlignment Results: Options");

			JLabel newNetworkLabel = new JLabel("Create a new network:");
			String[] newNetworkOptions = { "For each module", "With all modules" };
			newNetworkComboBox = new JComboBox(newNetworkOptions);
			if (newNetworkType == NewNetworkType.SINGLE_NETWORK)
				newNetworkComboBox.setSelectedIndex(1);
			else if (newNetworkType == NewNetworkType.MULTIPLE_NETWORKS)
				newNetworkComboBox.setSelectedIndex(0);
			JButton okButton = new JButton("   OK    ");
			okButton.addActionListener(new OKAction());
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new CancelAction());

			JPanel newNetworkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			newNetworkPanel.setBorder(BorderFactory.createTitledBorder("New Network"));
			newNetworkPanel.add(newNetworkLabel);
			newNetworkPanel.add(newNetworkComboBox);

			JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttonsPanel.add(cancelButton);
			buttonsPanel.add(okButton);

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5,5,5,5);

			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			add(newNetworkPanel, c);

			c.gridx = 0;		c.gridy = 1;
			c.weightx = 1.0;	c.weighty = 1.0;
			c.anchor = GridBagConstraints.LAST_LINE_END;
			add(buttonsPanel, c);

			setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			pack();
		}

		class OKAction implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if (newNetworkComboBox.getSelectedIndex() == 0)
					newNetworkType = NewNetworkType.MULTIPLE_NETWORKS;
				else if (newNetworkComboBox.getSelectedIndex() == 1)
					newNetworkType = NewNetworkType.SINGLE_NETWORK;
				OptionsDialog.this.setVisible(false);
			}
		}

		class CancelAction implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				OptionsDialog.this.setVisible(false);
			}
		}
	}

	class ResultsTable extends JTable
	{
		private final String[] columnNames = {"Start Node", "Module Score", "ST1 P-Value", "ST2 P-Value", "ST3 P-Value"};

		public ResultsTable()
		{
			setModel(new ResultsTableModel());
			addMouseListener(new ResultsTableMouseListener());
		}

		class ResultsTableModel extends AbstractTableModel
		{
			public String getColumnName(int c)
			{
				return columnNames[c];
			}

			public int getRowCount()
			{
				return results.size();
			}

			public int getColumnCount()
			{
				return columnNames.length;
			}

			public Class getColumnClass(int c)
			{
				return String.class;
			}

			public Object getValueAt(int row, int col)
			{
				Result result = results.get(row);
				switch (col)
				{
					case 0: return result.startNode;
					case 1: return Double.toString(result.moduleScore);
					case 2: return Double.toString(result.st1Pval);
					case 3: return Double.toString(result.st2Pval);
					case 4: return Double.toString(result.st3Pval);
				}
				return null;
			}

			public boolean isCellEditable(int row, int col)
			{
				return false;
			}
		}

		class ResultsTableMouseListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent e)
			{
				int row = ResultsTable.this.rowAtPoint(e.getPoint());
				Result module = results.get(row);
				parentNetwork.unselectAllNodes();
				parentNetwork.unselectAllEdges();
				
				Iterator nodes = parentNetwork.nodesIterator();
				while (nodes.hasNext())
				{
					Node node = (Node) nodes.next();
					if (module.memberNodes.contains(node.getIdentifier()))
						parentNetwork.setSelectedNodeState(node, true);
				}

				Cytoscape.getNetworkView(parentNetwork.getIdentifier()).redrawGraph(true, false);
			}

			public void mouseReleased(MouseEvent e)
			{
				if (ResultsTable.this.getSelectedRowCount() == 0)
					createNetworkButton.setEnabled(false);
				else
					createNetworkButton.setEnabled(true);
			}
		}
	}
}
