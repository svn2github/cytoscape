package cytoscape.plugin.cheminfo;

import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;

public class ChemTable extends JTable implements ClipboardOwner, SelectEventListener {
	private JPopupMenu popupMenu;

	private String networkID;

	private int xc;

	private int yc;

	private String[] popupItems = { "Copy Selected", "Remove From Table", "View Structure"};

	private MoleculeViewDialog moleculeDialog;

	public ChemTable(ChemTableModel model, String networkID) {
		super(model);
		
		this.networkID = networkID; 
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setRowHeight(121);

		TableColumn col = getColumnModel().getColumn(2);
		col.setWidth(121);
		col.setPreferredWidth(121);
		col.setResizable(false);
		MoleculeCellRenderer mcr = new MoleculeCellRenderer(new Dimension(120,
				120));
		col.setCellRenderer(mcr);

		getColumnModel().getColumn(1).setPreferredWidth(250);
		getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());

		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		setSelectionBackground(Color.CYAN);

		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setupPopup();

		moleculeDialog = new MoleculeViewDialog(Cytoscape.getDesktop());
		moleculeDialog.setSize(new Dimension(320, 320));

		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					// store the position where popup was called
					xc = e.getX();
					yc = e.getY();
					Point point = new Point(xc, yc);
					int rc = rowAtPoint(point);
					int cc = columnAtPoint(point);

					((JMenuItem) popupMenu.getComponent(1)).setEnabled(true);
					((JMenuItem) popupMenu.getComponent(0)).setEnabled(true);

					// show the popup
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	public void sortAllRowsBy(int colIndex, boolean ascending) {
		List data = ((ChemTableModel) getModel()).getRecords();
		Collections.sort(data, new ColumnSorter(colIndex, ascending));
		((ChemTableModel) getModel()).fireTableStructureChanged();
	}

	private void setupPopup() {
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
	
	private void removeFromTable() {
		List values = ((ChemTableModel)getModel()).getValuesAt(getSelectedRows());
		((ChemTableModel)getModel()).removeAll(values);
		this.updateUI();
		for (Object object : values) {
			List record = (List)object;
			CyNode node = ((StructureDepictor)record.get(2)).getNode();
			NodeView nodeView = Cytoscape.getNetworkView(networkID).getNodeView(node);
			nodeView.unselect();
		}
		Cytoscape.getNetworkView(networkID).updateView();
	}

	private void copySelected() {
		List values = ((ChemTableModel) getModel())
				.getValuesAt(getSelectedRows());
		Iterator it = values.iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			List row = (List) it.next();
			Iterator lit = row.iterator();
			sb.append(lit.next());
			sb.append('\t');
			sb.append(lit.next());
			sb.append('\n');
		}
		StringSelection stringSelection = new StringSelection(sb.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/**
	 * Empty implementation of the ClipboardOwner interface.
	 */
	public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.JTable#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);

		DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)e.getSource();
		/*
		System.out.println("anchor: " + selectionModel.getAnchorSelectionIndex());
		System.out.println("lead: " + selectionModel.getLeadSelectionIndex());
		System.out.println("min: " + selectionModel.getMinSelectionIndex());
		System.out.println("max: " + selectionModel.getMaxSelectionIndex());
		
		System.out.println("first: " + e.getFirstIndex());
		System.out.println("last: " + e.getLastIndex());
		System.out.println("mode: " + selectionModel.getSelectionMode());
		System.out.println("adjusting: " + selectionModel.getValueIsAdjusting());
		*/
		if (!selectionModel.getValueIsAdjusting()) {
			List values = ((ChemTableModel) getModel()).getRecords();
			List<CyNode> update = new ArrayList<CyNode>();
			List<CyNode> highlight = new ArrayList<CyNode>();
			int first = e.getFirstIndex();
			if (first != -1) { // removing a row
				int last = e.getLastIndex();
				for (int i = first; i <= last; i++) {
					List record = (List)values.get(i);
					CyNode node = ((StructureDepictor)record.get(2)).getNode();
					if (selectionModel.isSelectedIndex(i)) {
						highlight.add(node);
					}
					update.add(node);
				}
				highlightCytoscapeNodes(highlight, update);
			}
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

	public void onSelectEvent(SelectEvent evt) {
		final ChemTableModel model = (ChemTableModel)this.getModel();
		if (evt.getTargetType() == SelectEvent.NODE_SET || evt.getTargetType() == SelectEvent.SINGLE_NODE) {
			Set set = (Set)evt.getTarget();
			final List records = new ArrayList();
			for (Object object : set) {
				CyNode node = (CyNode)object;
				NodeView nodeView = Cytoscape.getNetworkView(networkID).getNodeView(node);
				/*
				if (evt.getEventType()) { // select
					
				} else {
					nodeView.getGraphView().updateView()
				}
				*/
				StructureDepictor depictor = new StructureDepictor(node);
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
	
	public void showDialog() {
		ChemTableDialog dialog = new ChemTableDialog();
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.setTitle("View 2D Structures");
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
	
	public String getNetworkID() {
		return networkID;
	}

	public void setNetworkID(String networkID) {
		this.networkID = networkID;
	}

	class ChemTableDialog extends JDialog {
		public void dispose() {
			Cytoscape.getNetwork(networkID).removeSelectEventListener(ChemTable.this);
			super.dispose();
		}
	}
}
