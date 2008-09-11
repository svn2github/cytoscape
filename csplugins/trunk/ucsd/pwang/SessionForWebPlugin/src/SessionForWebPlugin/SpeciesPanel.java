package SessionForWebPlugin;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JDialog;

import java.util.Map;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.util.Set;
import java.util.Iterator;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

//import SessionForWebPlugin.NetworksTable.NetworksTableMouseListener;
//import SessionForWebPlugin.NetworksTable.NetworksTableMouseMotionListener;
import javax.swing.event.MouseInputAdapter;

import SessionForWebPlugin.NetworksTable.NetworksTableMouseMotionListener;


public class SpeciesPanel extends JPanel implements ListSelectionListener
{
    private JDialog parent = null;
    private DefaultTableModel model;
    Vector<String> columnNames;

    /** Creates new form NetworkSpeciesPanel */
    public SpeciesPanel(JDialog parent) {
        initComponents();
        this.parent= parent;
        MyActionListener l = new MyActionListener(parent);
        btnAssign.addActionListener(l);
        
        columnNames = new Vector<String>();
        columnNames.add("Network Title");
        columnNames.add("Species");
        
        Vector<Vector<String>> data = getTableData();
        
        model = new DefaultTableModel(data, columnNames);
        tblNetworkSpecies.setModel(model);
        
        SpeciesCellRenderer cellRender = new SpeciesCellRenderer();
        tblNetworkSpecies.getColumnModel().getColumn(1).setCellRenderer(cellRender);
        MyMouseInputAdapter ml = new MyMouseInputAdapter();
        tblNetworkSpecies.addMouseListener(ml);
        tblNetworkSpecies.addMouseMotionListener(new NetworksTableMouseMotionListener());
        tblNetworkSpecies.getSelectionModel().addListSelectionListener(this);
    }
    
    public void valueChanged(ListSelectionEvent e){
    	//System.out.println("ListSelectEvent is received\n");
    	int[] selectedRows = tblNetworkSpecies.getSelectedRows();
    	if (selectedRows.length ==0) {
    		btnAssign.setText("Assign species for all networks");
    	}
    	else if (selectedRows.length>0) {
       		btnAssign.setText("Assign species for selected networks");    		
    	}
    }
    
	private class MyMouseInputAdapter extends MouseInputAdapter {

		public void mouseClicked(MouseEvent e) {
			int[] rows = tblNetworkSpecies.getSelectedRows();
			int[] cols = tblNetworkSpecies.getSelectedColumns();
			
			if (cols.length == 1 && cols[0] == 1) { // species is clicked
				String networkTitle = (String) tblNetworkSpecies.getModel().getValueAt(rows[0], 0);
				
        		Map<String,String> theMap = SessionForWebPlugin.networkTitleToIDMap();			
				String[] nertworkIDs = new String[1];
				nertworkIDs[0] = theMap.get(networkTitle);;
				SpeciesAssignmentDialog dlg = new SpeciesAssignmentDialog(parent, true, nertworkIDs);
				dlg.setSize(300, 300);
				dlg.setVisible(true);
	            if (!dlg.isVisible()) {
	            	updateTableData();
	            }
			}
		}
	}
	

	private void updateTableData() {
    	Vector<Vector<String>> data = getTableData();
        model.setDataVector(data, columnNames);
        model.fireTableDataChanged();
	}
	
	// Cannot use a MouseMotionListener to the TableCellRenderer as JTable does not dispatch
	// mouse events to the renderer
	class NetworksTableMouseMotionListener extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			int col = tblNetworkSpecies.convertColumnIndexToModel(tblNetworkSpecies.columnAtPoint(e.getPoint()));
			if (col == 1)
				tblNetworkSpecies.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				tblNetworkSpecies.setCursor(Cursor.getDefaultCursor());
		}
	}

	
	class SpeciesCellRenderer extends JLabel implements TableCellRenderer
	{
		public Component getTableCellRendererComponent(	JTable table,
								Object value,
								boolean isSelected,
								boolean hasFocus,
								int row, int column)
		{
			setText(value.toString());
			setName(new Integer(row).toString());
			setOpaque(true);
			//setForeground(Color.GREEN);
			//setBackground(Color.WHITE);
			setToolTipText("Click on the species value to edit.");
			return this;
		}
	}

	
    private Vector<Vector<String>> getTableData() {
    	Vector<Vector<String>> data = new Vector<Vector<String>>();
    
     	Map<String,String> theMap = SessionForWebPlugin.networkTitleToSpeciesMap();
    	
    	Set<String> keySet = theMap.keySet();
    	Iterator<String> it = keySet.iterator();
    	
    	while (it.hasNext()) {
    		String networkTitle = (String) it.next();
    		String species = theMap.get(networkTitle);
    		if (species == null || species.trim().equals("")) {
    			species = "unknown";
    		}
    		Vector<String> oneRow = new Vector<String>();
    		oneRow.add(networkTitle);
    		oneRow.add(species);
    		data.add(oneRow);
    	}
    	return data;
    }
    
    
    class MyActionListener implements ActionListener {
    	JDialog parent= null;
    	public MyActionListener(JDialog parent) {
    		this.parent = parent;
    	}

    	public void actionPerformed(ActionEvent e){
        	int[] selectedRows = tblNetworkSpecies.getSelectedRows();
        	SpeciesAssignmentDialog dlg;
        	
        	if (selectedRows.length ==0) {
        		dlg = new SpeciesAssignmentDialog(parent, true);
        	}
        	else { // (selectedRows.length>0) {
        		Map<String,String> theMap = SessionForWebPlugin.networkTitleToIDMap();
        		String[] networkIDs = new String[selectedRows.length];
        		for (int i=0; i< selectedRows.length; i++) {
        			String networkTitle = (String) tblNetworkSpecies .getModel().getValueAt(selectedRows[i], 0);
        			String id = theMap.get(networkTitle);
        			networkIDs[i] = id;
        		}
        		dlg = new SpeciesAssignmentDialog(parent, true, networkIDs);
        	}

            dlg.setSize(400, 300);
            dlg.setVisible(true);
            if (!dlg.isVisible()) {
            	updateTableData();
            }
    	}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNetworkSpecies = new javax.swing.JTable();
        btnAssign = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lbTitle.setText("Select networks to assign species");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(lbTitle, gridBagConstraints);

        tblNetworkSpecies.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Network Title", "Species"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblNetworkSpecies);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        btnAssign.setText("Assign species for all networks");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(btnAssign, gridBagConstraints);

    }// </editor-fold>                        
    
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnAssign;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JTable tblNetworkSpecies;
    // End of variables declaration                   

}
