package org.eclipse.equinox.internal.provisional.p2.ui.dialogs;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.net.URI;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JButton;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.ui.DefaultMetadataURLValidator;
import org.eclipse.equinox.internal.p2.ui.ProvUIMessages;
import org.eclipse.equinox.internal.p2.ui.model.ElementUtils;
import org.eclipse.equinox.internal.p2.ui.model.MetadataRepositoryElement;
//import org.eclipse.equinox.internal.p2.ui.viewers.RepositoryDetailsLabelProvider;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
//import org.eclipse.equinox.internal.provisional.p2.ui.UpdateManagerCompatibility;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.CachedMetadataRepositories;
import org.eclipse.equinox.internal.provisional.p2.ui.model.MetadataRepositories;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningOperation;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.RepositoryOperation;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.QueryProvider;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.RepositoryLocationValidator;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.RepositoryManipulator;
import org.eclipse.osgi.util.NLS;
//import org.eclipse.swt.custom.BusyIndicator;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.internal.Model;

import javax.swing.table.*;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.table.TableColumn;
import javax.swing.JTable;

public class RepositoryManipulationDialog extends JDialog implements ActionListener, ListSelectionListener {

	private Policy policy;
	JDialog parent;
	RepositoryManipulator manipulator;
	RepositoryManipulator localCacheRepoManipulator;
	CachedMetadataRepositories input;
	boolean changed = false;

    /** Creates new form RepositoryManipulationPage */
    public RepositoryManipulationDialog(boolean modal, Policy policy) {
        //super(parent, modal);
    	this.setModal(modal);
        this.policy = policy;
        this.parent = parent;
        this.setTitle("Repository Manipulation");
        
        initComponents();

        // Disable auto resizing
        //tblSites.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //TableColumn col2 = tblSites.getColumnModel().getColumn(2); // Enabled
        //col2.setPreferredWidth(300);
        
		btnRemove.setEnabled(false);
		btnTestConnection.setEnabled(false);
		btnTestConnection.setVisible(false); // will not implement at this time
		btnEnable.setEnabled(false);
		btnExport.setEnabled(false);
		//btnOK.setEnabled(false);
		
        btnOK.addActionListener(this);
        btnCancel.addActionListener(this);
        
        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);
        btnTestConnection.addActionListener(this);
        btnEnable.addActionListener(this);
        btnExport.addActionListener(this);
        btnImport.addActionListener(this);

        init();
        getInput();
        input.getChildren(null);
        
        MetadataRepositoryElement[] elements = getElements();
        Vector tableData = new Vector();
        
        for (int i=0; i< elements.length; i++){
        	tableData.add(elements[i]);
        }
        
        SitesTableModel model = new SitesTableModel(tableData);
        tblSites.setModel(model);
        
        tblSites.getSelectionModel().addListSelectionListener(this);
        
    }
    

    // Selection listener
    public void valueChanged(ListSelectionEvent e){
	
    	if (tblSites.getSelectedRow() == -1) {
    		btnRemove.setEnabled(false);
    		btnTestConnection.setEnabled(false);
    		btnExport.setEnabled(false);
    	}
    	else {
    		btnRemove.setEnabled(true);
    		btnTestConnection.setEnabled(true);
    		btnExport.setEnabled(true);
    	}

    	if (tblSites.getSelectedRowCount() == 1){
    		int selectedRow = tblSites.getSelectedRow();
    		
    		SitesTableModel model = (SitesTableModel) tblSites.getModel();
    		MetadataRepositoryElement selectedElement = model.getRowAt(selectedRow);
    		if (selectedElement.isEnabled()){
    			btnEnable.setText("Disable");
    		}
    		else {
    			btnEnable.setText("Enable");
    		}
     		btnEnable.setEnabled(true); 
     		
     		btnRemove.setEnabled(true);
    	}
    	else {
    		btnEnable.setEnabled(false);
    		btnRemove.setEnabled(false);
    	}
		//if (changed) {
    	//	btnOK.setEnabled(true);    			
		//}
		//else {
		//	btnOK.setEnabled(false);
		//}
    }
    
	public void init() {
		//noDefaultAndApplyButton();
		if (policy == null)
			policy = Policy.getDefault();
		manipulator = policy.getRepositoryManipulator();
	}


    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() instanceof JButton){
    		JButton btn = (JButton) e.getSource();
    		
    		if (btn == btnOK){
    			if (changed)
    				ElementUtils.updateRepositoryUsingElements(getElements());
    			this.dispose();
    		}
    		else if (btn == btnCancel){
    			this.dispose();
    		}
    		else if (btn == btnAdd){
    			addRepository();
    		}
    		else if (btn == btnRemove){
    			removeRepositories();
    		}
    		else if (btn == btnTestConnection){
    			System.out.println("btntestConnection is pressed!");
    		}
    		else if (btn == btnEnable){
    			toggleRepositoryEnablement();
    		}
    		else if (btn == btnImport){
    			importRepositories();
    		}
    		else if (btn == btnExport){
    			exportRepositories();
    		}
    	}
    }

	void toggleRepositoryEnablement() {
		
		int selectedRow = tblSites.getSelectedRow();
		
		SitesTableModel model = (SitesTableModel) tblSites.getModel();
		MetadataRepositoryElement selectedElement = model.getRowAt(selectedRow);

		selectedElement.setEnabled(!selectedElement.isEnabled());
			
		model.refresh();
		changed = true;
		
		if (selectedElement.isEnabled()){
			btnEnable.setText("Disable");
		}
		else {
			btnEnable.setText("Enable");
		}
	}


	void importRepositories() {
		Runnable newThread = new Runnable(){
			public void run() {
				/*
				MetadataRepositoryElement[] imported = UpdateManagerCompatibility.importSites(RepositoryManipulationDialog.this);
				if (imported.length > 0) {
					Hashtable repos = getInput().cachedElements;
					changed = true;
					for (int i = 0; i < imported.length; i++)
						repos.put(imported[i].getLocation().toString(), imported[i]);
					//asyncRefresh();
					
					// refresh the table
					syncTableData();
				}
			*/
			
			}
		};
		
		newThread.run();
		
		/*
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				MetadataRepositoryElement[] imported = UpdateManagerCompatibility.importSites(getShell());
				if (imported.length > 0) {
					Hashtable repos = getInput().cachedElements;
					changed = true;
					for (int i = 0; i < imported.length; i++)
						repos.put(imported[i].getLocation().toString(), imported[i]);
					asyncRefresh();
				}
			}
		});
		*/
	}

	/*
	void asyncRefresh() {
		display.asyncExec(new Runnable() {
			public void run() {
				repositoryViewer.refresh();
			}
		});
	}
*/
	

	void exportRepositories() {
		
		Runnable newThread = new Runnable(){
			public void run() {
				
				int[] selectedRows = tblSites.getSelectedRows();
				SitesTableModel model = (SitesTableModel) tblSites.getModel();
				
				MetadataRepositoryElement[] elements = new MetadataRepositoryElement[selectedRows.length];//getSelectedElements();
			
				for (int i=0; i< selectedRows.length; i++){
					elements[i] = model.getRowAt(selectedRows[i]);
				}
				
				if (elements.length == 0)
					elements = getElements();
				//UpdateManagerCompatibility.exportSites(RepositoryManipulationDialog.this, elements);
			}
		};
		newThread.run();
		
		/*
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				MetadataRepositoryElement[] elements = getSelectedElements();
				if (elements.length == 0)
					elements = getElements();
				UpdateManagerCompatibility.exportSites(getShell(), elements);
			}
		});
		*/
	}


	void removeRepositories() {
		
		int selectRowIndex = tblSites.getSelectedRow();
		if (selectRowIndex ==-1) return;
		
		SitesTableModel tblModel = (SitesTableModel)tblSites.getModel();
		MetadataRepositoryElement element = tblModel.getRowAt(selectRowIndex);
		
		int userInput = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove "+ element.getLocation(), "Remove Sites", JOptionPane.YES_NO_OPTION);
		
		if (userInput == JOptionPane.NO_OPTION){
			return;
		}
		tblModel.deleteRowAt(selectRowIndex);

		// sync the data in tableModel and in cachedMetaRepository
		getInput().cachedElements.remove(element.getLocation().toString());

		changed = true;		
	}

    
	void addRepository() {
		//System.out.println("btnAddSite is clicked");
		AddSiteDialog addSiteDlg = new AddSiteDialog(this, true, policy, input);
		addSiteDlg.setLocationRelativeTo(this);
		addSiteDlg.setSize(400, 150);
		addSiteDlg.setVisible(true);	
		while (addSiteDlg.isVisible()){
			try {
				Thread.sleep(2000);				
			}
			catch (InterruptedException ie){
				// do nothing
			}
		}

		syncTableData();
		/*
		SitesTableModel _model =  (SitesTableModel) tblSites.getModel();
		
		// sync the data in tableModel and in cachedMetaRepository
		if (input.cachedElements.size() > _model.getRowCount()) {
			// Determine which site is teh one just added
			Hashtable clonedTable = (Hashtable) input.cachedElements.clone();
			for (int i=0; i< _model.getRowCount(); i++){
				if (clonedTable.containsKey(_model.getRowAt(i).getLocation().toString())){
					clonedTable.remove(_model.getRowAt(i).getLocation().toString());
				}
			}
			Object[] keyArray = clonedTable.keySet().toArray();
			MetadataRepositoryElement newElement = (MetadataRepositoryElement) clonedTable.get(keyArray[0]);
			_model.addRow(newElement);			
			changed = true;			
		}		
		*/
	}

 
	// sync the data in tableModel and in cachedMetaRepository after the change in cachedElements
	private void syncTableData(){
		SitesTableModel _model =  (SitesTableModel) tblSites.getModel();
		
		if (input.cachedElements.size() > _model.getRowCount()) {
			// Determine which sites are new in cachedElements
			Hashtable clonedTable = (Hashtable) input.cachedElements.clone();
			for (int i=0; i< _model.getRowCount(); i++){
				if (clonedTable.containsKey(_model.getRowAt(i).getLocation().toString())){
					clonedTable.remove(_model.getRowAt(i).getLocation().toString());
				}
			}
			Object[] keyArray = clonedTable.keySet().toArray();

			for (int i=0; i< keyArray.length; i++){
				MetadataRepositoryElement newElement = (MetadataRepositoryElement) clonedTable.get(keyArray[i]);
				_model.addRow(newElement);							
			}
			
			changed = true;			
		}		
		
	}
	
	
	CachedMetadataRepositories getInput() {
		if (input == null)
			input = new CachedMetadataRepositories(policy);
		return input;
	}

	MetadataRepositoryElement[] getElements() {		
		return (MetadataRepositoryElement[]) getInput().cachedElements.values().toArray(new MetadataRepositoryElement[getInput().cachedElements.size()]);
	}

	
	URI[] getKnownRepositories() {
		MetadataRepositoryElement[] elements = getElements();
		URI[] locations = new URI[elements.length];
		for (int i = 0; i < elements.length; i++)
			locations[i] = elements[i].getLocation();
		return locations;
	}
	
	/*
	RepositoryLocationValidator getRepositoryLocationValidator() {
		DefaultMetadataURLValidator validator = new DefaultMetadataURLValidator() {
			protected URI[] getKnownLocations() {
				return getKnownRepositories();
			}
		};
		return validator;

	}


	RepositoryOperation getRepoAddOperation(URI location) {
		return new RepositoryOperation("Cached add repo operation", new URI[] {location}) { //$NON-NLS-1$
			protected IStatus doExecute(IProgressMonitor monitor) {
				for (int i = 0; i < locations.length; i++) {
					Hashtable elements = getInput().cachedElements;
					elements.put(locations[i].toString(), new MetadataRepositoryElement(getInput(), locations[i], true));

				}
				//changed = true;
				//asyncRefresh();
				return Status.OK_STATUS;
			}

			protected IStatus doBatchedExecute(IProgressMonitor monitor) throws ProvisionException {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	*/
/////////////////////////////
	
    class SitesTableModel extends AbstractTableModel {
    	
    	String[] columnNames = {"Name","Location","Enabled"};
    	private Vector repoElementVect;
    	
    	public SitesTableModel(Vector MetadataRepositoryElementVect){
    		this.repoElementVect = MetadataRepositoryElementVect;
    	}
    	
    	public int getRowCount(){
    		if (repoElementVect == null){
    			return 0;
    		}
    		return repoElementVect.size();
    	}
    	
    	public int getColumnCount(){
    		return columnNames.length;
    	}
    	
    	public String getColumnName(int column){
    		return columnNames[column];
    	}
    	
    	  public Object getValueAt(int row, int column){
    		  MetadataRepositoryElement rowX = (MetadataRepositoryElement) repoElementVect.elementAt(row);
      		if (column == 0){
    			if (rowX.getName().endsWith("- metadata")){
    				return "";
    			}
    			return rowX.getName();
    		}      		
    		if (column == 1){
    			return rowX.getLocation();
    		}
    		if (column == 2){
    			if (rowX.isEnabled())
    				return "Enabled";
    			else 
    				return "Disabled";
    		}
    		return "unknown";
    	  }
    	  
    	  public MetadataRepositoryElement getRowAt(int row){
    		  return (MetadataRepositoryElement) repoElementVect.elementAt(row);
    	  }
    	  
    	  public void deleteRowAt(int row){
    		  repoElementVect.removeElementAt(row);
    		  this.fireTableDataChanged();
    	  }

       	  public void addRow(MetadataRepositoryElement newElement){
    		  repoElementVect.add(newElement);
    		  this.fireTableDataChanged();
    	  }    
       	  
       	  /*
       	  public void updateRowAt(int row, MetadataRepositoryElement newElement){
       		  repoElementVect.removeElementAt(row);
       		  repoElementVect.insertElementAt(newElement, row);
    		  this.fireTableDataChanged();
       	  }
       	  */
       	  
       	  public void refresh(){
       		  System.out.println("SitesTableModel.refresh()...");
       		this.fireTableDataChanged();
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
        tblSites = new javax.swing.JTable();
        pnlButton_right = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnTestConnection = new javax.swing.JButton();
        btnEnable = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        pnlButton_bottom = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        lbTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
        lbTitle.setText("Available Software sites");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 0);
        getContentPane().add(lbTitle, gridBagConstraints);

        tblSites.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Name", "Location", "Enabled"
            }
        ));
        jScrollPane1.setViewportView(tblSites);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        pnlButton_right.setLayout(new java.awt.GridBagLayout());

        btnAdd.setText("Add...");
        btnAdd.setPreferredSize(new java.awt.Dimension(113, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlButton_right.add(btnAdd, gridBagConstraints);

        btnRemove.setText("Remove");
        btnRemove.setPreferredSize(new java.awt.Dimension(113, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        pnlButton_right.add(btnRemove, gridBagConstraints);

        btnTestConnection.setText("Test Connection");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        pnlButton_right.add(btnTestConnection, gridBagConstraints);

        btnEnable.setText("Enable");
        btnEnable.setPreferredSize(new java.awt.Dimension(113, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        pnlButton_right.add(btnEnable, gridBagConstraints);

        btnImport.setText("Import");
        btnImport.setPreferredSize(new java.awt.Dimension(113, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        pnlButton_right.add(btnImport, gridBagConstraints);

        btnExport.setText("Export");
        btnExport.setPreferredSize(new java.awt.Dimension(113, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        pnlButton_right.add(btnExport, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        getContentPane().add(pnlButton_right, gridBagConstraints);

        pnlButton_bottom.setLayout(new java.awt.GridBagLayout());

        btnOK.setText("OK");
        btnOK.setPreferredSize(new java.awt.Dimension(67, 23));
        pnlButton_bottom.add(btnOK, new java.awt.GridBagConstraints());

        btnCancel.setText("Cancel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlButton_bottom.add(btnCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        getContentPane().add(pnlButton_bottom, gridBagConstraints);

        pack();
    }// </editor-fold>                        
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnEnable;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnTestConnection;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnlButton_bottom;
    private javax.swing.JPanel pnlButton_right;
    private javax.swing.JTable tblSites;
    // End of variables declaration                   

}
