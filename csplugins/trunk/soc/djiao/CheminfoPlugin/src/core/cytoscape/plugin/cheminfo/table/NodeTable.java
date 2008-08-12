package cytoscape.plugin.cheminfo.table;

import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.plugin.cheminfo.ChemInfoPlugin.AttriType;
import cytoscape.plugin.cheminfo.structure.MoleculeViewDialog;
import cytoscape.plugin.cheminfo.structure.StructureDepictor;
import cytoscape.view.CyNetworkView;

public class NodeTable extends ChemTable {

	private MoleculeViewDialog moleculeDialog;

	private int structureColumn = -1;

	private static String[] popupItems = { "Copy Selected", "Remove From Table",
			"View Structure" };

	public NodeTable(ChemTableModel model, String networkID, String attribute, AttriType attrType) {
		super(model, networkID, attribute, attrType);

		moleculeDialog = new MoleculeViewDialog(Cytoscape.getDesktop());
		moleculeDialog.setSize(new Dimension(320, 320));
	}

	protected void setupPopup() {
		this.popupMenu = new JPopupMenu();
		for (int i = 0; i < popupItems.length; i++) {
			JMenuItem menuItem = new JMenuItem(popupItems[i]);
			popupMenu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					String command = ev.getActionCommand();
					if (command.equals("Copy Selected")) {
						copySelected();
					} else if (command.equals("View Structure")) {
						displayStructure();
					} else if (command.equals("Remove From Table")) {
						removeFromTable();
					}
				}
			});
		}

	}

	public void onSelectEvent(SelectEvent evt) {
		final ChemTableModel model = (ChemTableModel)this.getModel();
		if (evt.getTargetType() == SelectEvent.NODE_SET || evt.getTargetType() == SelectEvent.SINGLE_NODE) {
			Set set = (Set)evt.getTarget();
			final List records = new ArrayList();
			for (Object object : set) {
				CyNode node = (CyNode)object;
				NodeView nodeView = Cytoscape.getNetworkView(networkID).getNodeView(node);
				StructureDepictor depictor = new StructureDepictor(node, attribute, attrType);
				//Image image = depictor.
				String text = depictor.getMoleculeString();
				String id = node.getIdentifier();
				List row = new ArrayList();
				row.add(id);
				row.add(text);
				row.add(depictor);
				records.add(row);					
			}			
			if (evt.getEventType()) { //select
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						model.addAll(records);
					}
				});
			} else { //deselect
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						model.removeAll(records);
					}
				});
			}
			this.updateUI();
		}
	}	
	


	/**
	 * Select a group of nodes in Cytoscape. If a node is already selected,
	 * change the color to indicate a second-level selection.
	 * 
	 */
	public void highlightCytoscapeNodes(List<CyNode> highlight, List<CyNode> update) {
		CyNetworkView networkView = Cytoscape.getNetworkView(networkID);
		
		CyNetwork network = Cytoscape.getNetwork(networkID);
		
		//network.unselectAllNodes();
		for (CyNode node: update) {
			NodeView nodeView = networkView.getNodeView(node);
			if (highlight.contains(node)) {
				nodeView.setSelectedPaint(java.awt.Color.CYAN);
			} else {
				nodeView.setSelectedPaint(java.awt.Color.YELLOW);
			}
			//nodeView.setSelected(true);
		}

		networkView.updateView();
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);

		DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e
				.getSource();

		if (!selectionModel.getValueIsAdjusting()) {
			List values = ((ChemTableModel) getModel()).getRecords();
			List<CyNode> update = new ArrayList<CyNode>();
			List<CyNode> highlight = new ArrayList<CyNode>();
			int first = e.getFirstIndex();
			if (first != -1) { // removing a row
				int last = e.getLastIndex();
				for (int i = first; i <= last; i++) {
					List record = (List) values.get(i);
					CyNode node = ((StructureDepictor) record
							.get(structureColumn)).getNode();
					if (selectionModel.isSelectedIndex(i)) {
						highlight.add(node);
					}
					update.add(node);
				}
				highlightCytoscapeNodes(highlight, update);
			}
		}
	}

	private void displayStructure() {
		Point point = new Point(xc, yc);
		int rc = rowAtPoint(point);
		int cc = columnAtPoint(point);
		StructureDepictor depictor = (StructureDepictor) getModel().getValueAt(
				rc, 2);
		moleculeDialog.setDepictor(depictor);
		moleculeDialog.setLocationRelativeTo(Cytoscape.getDesktop());
		moleculeDialog.pack();
		moleculeDialog.setVisible(true);
	}

	@Override
	protected void removeFromTable() {
		List values = ((ChemTableModel) getModel())
				.getValuesAt(getSelectedRows());
		((ChemTableModel) getModel()).removeAll(values);
		this.updateUI();
		for (Object object : values) {
			List record = (List) object;
			CyNode node = ((StructureDepictor) record.get(structureColumn))
					.getNode();
			NodeView nodeView = Cytoscape.getNetworkView(networkID)
					.getNodeView(node);
			nodeView.unselect();
		}
		Cytoscape.getNetworkView(networkID).updateView();
	}

	public int getStructureColumn() {
		return structureColumn;
	}

	public void setStructureColumn(int structureColumn) {
		this.structureColumn = structureColumn;
	}

	@Override
	public void showTableDialog(String title)  {
		ChemTableDialog dialog = new ChemTableDialog();
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.setTitle(title);
		dialog.setModal(false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
        JScrollPane spane = new JScrollPane();
        spane.getViewport().add(this);
        dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(spane, BorderLayout.CENTER);
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.pack();
		dialog.setVisible(true);
	}
}
