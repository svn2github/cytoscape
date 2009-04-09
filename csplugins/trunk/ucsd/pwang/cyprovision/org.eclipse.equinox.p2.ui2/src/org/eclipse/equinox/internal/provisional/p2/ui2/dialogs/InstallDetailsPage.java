package org.eclipse.equinox.internal.provisional.p2.ui2.dialogs;

import java.awt.Component;

import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardController;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.eclipse.equinox.internal.p2.ui2.model.AvailableIUElement;
import org.eclipse.equinox.internal.p2.ui2.model.CategoryElement;
import org.eclipse.equinox.internal.p2.ui2.model.ElementUtils;
import org.eclipse.equinox.internal.p2.ui2.model.MetadataRepositoryElement;

import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.ILicense;
import org.eclipse.equinox.internal.provisional.p2.ui2.IUPropertyUtils;
import org.eclipse.equinox.internal.provisional.p2.ui2.model.MetadataRepositories;
import java.awt.event.ComponentAdapter;

public class InstallDetailsPage extends WizardPage {

    private HashSet checkedIUElements = null;

	public InstallDetailsPage(){
		 initComponents();		
		 
		 initRepoTree();
			
		 TreeSelectionListener treeSelectionListener = new TreeSelectionListener(){
				public void valueChanged(TreeSelectionEvent tse){
					// Handle the detail area
					updateDetails();
				}
			};
			
		jTree1.addTreeSelectionListener(treeSelectionListener);
		
		//Populate the JTree when the panel show up the first time
		ComponentAdapter componentAdapter = new ComponentAdapter(){
		    public void componentMoved(ComponentEvent e)  {
		    	//System.out.println("component has been  moved");
		    	recycle();
		    }
		};
		this.addComponentListener(componentAdapter);
		
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
			
			if (node_userObj instanceof AvailableIUElement){
				AvailableIUElement avail_iu_element = (AvailableIUElement) node_userObj;
				IInstallableUnit installUnit =avail_iu_element.getIU();
				
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
	
	
	private void initRepoTree(){
		jTree1.setRootVisible(false);
		jTree1.setShowsRootHandles(true);
		
		RepoCellRenderer r1 = new RepoCellRenderer();
		jTree1.setCellRenderer(r1);
	}
		
	
    public static final String getDescription() {
        return "Install Details";
    }
    
    protected String validateContents (Component component, Object o) {

    	return null;
    }
    
    
    protected  void recycle()  {
    	checkedIUElements = (HashSet) this.getWizardDataMap().get("checkedIUElements");
    	
    	if (checkedIUElements == null){
    		return;
    	}
    	    	
    	//Check Licenses
    	Iterator it = checkedIUElements.iterator();
    	HashMap licenseMap = new HashMap();
    	while (it.hasNext()){
    		IInstallableUnit iu = ElementUtils.getIU(it.next());
    		
    		String name = IUPropertyUtils.getIUProperty(iu, IInstallableUnit.PROP_NAME);
    		ILicense license = IUPropertyUtils.getLicense(iu);
    		if (license != null) {
    			licenseMap.put(name, license);
    		}
    	}
    	
    	this.putWizardData("LicenseMap", licenseMap);
    	
    	if (licenseMap.size() == 0){
    		// There is no license, enable finish button
    		this.setForwardNavigationMode(WizardController.MODE_CAN_FINISH);
    	}
    	
		DefaultTreeModel model= rebuildTreeModel();
        jTree1.setModel(model);
    }
    
    
	private DefaultTreeModel rebuildTreeModel(){

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");                
		DefaultMutableTreeNode node;
		
    	checkedIUElements = (HashSet) this.getWizardDataMap().get("checkedIUElements");
    	if (checkedIUElements == null){
    		return new DefaultTreeModel(null);    		
    	}
    	    	
    	Iterator it = checkedIUElements.iterator();
    	while (it.hasNext()){
    		AvailableIUElement x = (AvailableIUElement) it.next();
    		node = new DefaultMutableTreeNode();
			node.setUserObject(x);
			root.add(node);
    	}

		return new DefaultTreeModel(root);
	}

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlTree = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        lbSize = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        taDetails = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        pnlTree.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setViewportView(jTree1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlTree.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        add(pnlTree, gridBagConstraints);

        lbSize.setText("Size:0 KB");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(lbSize, gridBagConstraints);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Details"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        taDetails.setColumns(20);
        taDetails.setRows(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(taDetails, gridBagConstraints);

        jScrollPane3.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        add(jScrollPane3, gridBagConstraints);

    }// </editor-fold>                        
    
    
    // Variables declaration - do not modify                     
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lbSize;
    private javax.swing.JPanel pnlTree;
    private javax.swing.JTextArea taDetails;
    // End of variables declaration                   
    
    
    
}
