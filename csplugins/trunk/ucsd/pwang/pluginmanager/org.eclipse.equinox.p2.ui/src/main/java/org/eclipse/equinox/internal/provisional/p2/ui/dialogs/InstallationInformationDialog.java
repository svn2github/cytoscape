package org.eclipse.equinox.internal.provisional.p2.ui.dialogs;

import javax.swing.JDialog;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import org.eclipse.equinox.internal.p2.ui.model.AvailableIUElement;
import org.eclipse.equinox.internal.provisional.p2.ui.IUPropertyUtils;
import org.eclipse.equinox.internal.provisional.p2.ui.model.InstalledIUElement;
import org.eclipse.equinox.internal.provisional.p2.ui.model.ProfileElement;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.util.Vector;

public class InstallationInformationDialog extends JDialog implements ChangeListener, ActionListener {

	String profileId = null;
	
    /** Creates new form InstallationInformationDialog */
    public InstallationInformationDialog(boolean modal, String profileId) {
        //super(modal);
    	this.setModal(modal);
        initComponents();
        this.setTitle("Installation Information");

        this.profileId = profileId;
        pnlRevertButtons.setVisible(false);
        
        // register a change listener
        jTabbedPane1.addChangeListener(this);
        
        btnOK.addActionListener(this);
        btnOK_revertpanel.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnUninstall.addActionListener(this);
        btnProperties.addActionListener(this);
        btnRevert.addActionListener(this);
        
        initRepoTree();
        
		btnUpdate.setEnabled(false);        
		btnUninstall.setEnabled(false);
		btnProperties.setEnabled(false);

		TreeSelectionListener treeSelectionListener = new TreeSelectionListener(){
				public void valueChanged(TreeSelectionEvent tse){
					// Handle the detail area
					updateDetails();
					updateButtonStatus();
				}
			};
			
		jTree1.addTreeSelectionListener(treeSelectionListener);
    }
    
    
	private void initRepoTree(){
		jTree1.setRootVisible(false);
		jTree1.setShowsRootHandles(true);
		
		RepoCellRenderer r1 = new RepoCellRenderer();
		jTree1.setCellRenderer(r1);
		
		DefaultTreeModel model= rebuildTreeModel();
        jTree1.setModel(model);

	}

    
	  
	private DefaultTreeModel rebuildTreeModel(){

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");                
		DefaultMutableTreeNode node;
		
        //Populate the tree
        ProfileElement profileElement = (ProfileElement)getInput();
        Object[] children = profileElement.getChildren(null);

        if (children == null || children.length == 0){
        	return new DefaultTreeModel(null);
        }
        
        for (int i=0; i< children.length; i++){
       		if (children[i] instanceof InstalledIUElement){
       			
       			node = new DefaultMutableTreeNode();
    			node.setUserObject(children[i]);
    			root.add(node);
    			
       			//IInstallableUnit installableUnit = ((InstalledIUElement)children[i]).getIU();
				//String name = IUPropertyUtils.getIUProperty(installableUnit, IInstallableUnit.PROP_NAME);
	       		//System.out.println("name = "+ name);

       		}
       	}

		return new DefaultTreeModel(root);
	}


	
	Object getInput() {
		ProfileElement element = new ProfileElement(null, profileId);
		return element;
	}

	void updateButtonStatus() {
		TreePath[] selectedPaths = jTree1.getSelectionPaths();
		
		if (selectedPaths == null){
			btnUpdate.setEnabled(false); 
			btnUninstall.setEnabled(false);
			btnProperties.setEnabled(false);
		}
		else {
			btnUpdate.setEnabled(true); 
			btnUninstall.setEnabled(true);
			btnProperties.setEnabled(true);
		}
	}
	
	void updateDetails(){
		
		TreePath[] selectedPaths = jTree1.getSelectionPaths();
		
		if (selectedPaths == null){
			taDetails.setText("");
			return;
		}
		
		if (selectedPaths.length == 1){
			TreePath thePath = selectedPaths[0];
			DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
			Object node_userObj = theNode.getUserObject();
			
			if (node_userObj instanceof InstalledIUElement){
				InstalledIUElement installed_iu_element = (InstalledIUElement) node_userObj;
				IInstallableUnit installUnit =installed_iu_element.getIU();
				
				StringBuffer result = new StringBuffer();
				String description = IUPropertyUtils.getIUProperty(installUnit, IInstallableUnit.PROP_DESCRIPTION);

				if (description != null) {
					result.append(description);
				} else {
					String name = IUPropertyUtils.getIUProperty(installUnit, IInstallableUnit.PROP_NAME);
					if (name != null)
						result.append(name);
					else
						result.append(installUnit.getId());
					result.append(" "); //$NON-NLS-1$
					result.append(installUnit.getVersion().toString());
				}

				taDetails.setText(result.toString());
			}
		}
		else if (selectedPaths.length > 1){
			taDetails.setText("");
		}
		
	}

	
    // change listener
    public  void stateChanged(ChangeEvent e)  {
    	JTabbedPane pane = (JTabbedPane) e.getSource();
    	
    	// get current tab
    	int sel = pane.getSelectedIndex();
    	
    	if (sel == 0){
    		pnlUpdateButtons.setVisible(true);
    		pnlRevertButtons.setVisible(false);
    	}
    	else if (sel == 1){
    		pnlUpdateButtons.setVisible(false);
    		pnlRevertButtons.setVisible(true);	
    	}
    	
    }
    
    
    // Action listener
    public void actionPerformed(ActionEvent e){
    
    	if (!(e.getSource() instanceof JButton)) {
    		return;
    	}
    	JButton btn = (JButton) e.getSource();
    	if (btn == btnOK || btn == btnOK_revertpanel){
    		this.dispose();
    	}
    	else if (btn == btnUpdate){
    		UpdateSoftwareWizard wizard = new UpdateSoftwareWizard(this, profileId, getSelectedIUs());
    	}
    	else if (btn == btnUninstall){
    		UninstallSoftwareWizard wizard = new UninstallSoftwareWizard(this, profileId, getSelectedIUs());
    	}
    	else if (btn == btnProperties){
    		PropertiesDialog propDialog = new PropertiesDialog(this, (InstalledIUElement)getSelectedIUs().elementAt(0));
    	}
    	else if (btn == btnRevert){
    		System.out.println("btnRevert is clicked");

    	}
    }
    
    
	private Vector getSelectedIUs(){
		TreePath[] selectedPaths = jTree1.getSelectionPaths();
		
		if (selectedPaths == null){
			return null;
		}
		
		Vector selectedIUs = new Vector();
		for (int i=0; i<selectedPaths.length; i++ ){
			TreePath thePath = selectedPaths[i];
			DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
			Object node_userObj = theNode.getUserObject();
			
			if (node_userObj instanceof InstalledIUElement){
				selectedIUs.add((InstalledIUElement)node_userObj);
			}
		}
		return selectedIUs;
	}

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlInstalledSoftware = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        taDetails = new javax.swing.JTextArea();
        pnlInstallationHistory = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlUpdateButtons = new javax.swing.JPanel();
        btnUpdate = new javax.swing.JButton();
        btnUninstall = new javax.swing.JButton();
        btnProperties = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        pnlRevertButtons = new javax.swing.JPanel();
        btnRevert = new javax.swing.JButton();
        btnOK_revertpanel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        pnlInstalledSoftware.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setViewportView(jTree1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        pnlInstalledSoftware.add(jScrollPane1, gridBagConstraints);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Details"));
        taDetails.setColumns(20);
        taDetails.setRows(5);
        jScrollPane2.setViewportView(taDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        pnlInstalledSoftware.add(jScrollPane2, gridBagConstraints);

        jTabbedPane1.addTab("Installed Software", pnlInstalledSoftware);

        pnlInstallationHistory.setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlInstallationHistory.add(jSplitPane1, gridBagConstraints);

        jTabbedPane1.addTab("Installation History", pnlInstallationHistory);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 10);
        getContentPane().add(jTabbedPane1, gridBagConstraints);

        btnUpdate.setText("Update...");
        pnlUpdateButtons.add(btnUpdate);

        btnUninstall.setText("Uninstall...");
        pnlUpdateButtons.add(btnUninstall);

        btnProperties.setText("Properties");
        pnlUpdateButtons.add(btnProperties);

        btnOK.setText("OK");
        pnlUpdateButtons.add(btnOK);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlUpdateButtons, gridBagConstraints);

        btnRevert.setText("Revert");
        pnlRevertButtons.add(btnRevert);

        btnOK_revertpanel.setText("OK");
        pnlRevertButtons.add(btnOK_revertpanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlRevertButtons, gridBagConstraints);

        pack();
    }// </editor-fold>                        
    
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnOK_revertpanel;
    private javax.swing.JButton btnProperties;
    private javax.swing.JButton btnRevert;
    private javax.swing.JButton btnUninstall;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JPanel pnlInstallationHistory;
    private javax.swing.JPanel pnlInstalledSoftware;
    private javax.swing.JPanel pnlRevertButtons;
    private javax.swing.JPanel pnlUpdateButtons;
    private javax.swing.JTextArea taDetails;
    // End of variables declaration                   

	
}
