package org.eclipse.equinox.internal.p2.ui2.sdk.scheduler;

import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
//import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;

public class UpdateDialog extends JDialog implements ActionListener, MouseListener {


	//private IInstallableUnit[] iusWithUpdates;
	private IInstallableUnit[] ius_latest;
	private AutomaticUpdater automaticUpdater;
	
	public UpdateDialog(AutomaticUpdater automaticUpdater){
		this.automaticUpdater = automaticUpdater;
		this.ius_latest = automaticUpdater.iusLatest;
		this.setTitle("Available Updates");
				
		//for (int i=0; i<ius_latest.length; i++){
		//	System.out.println("iu_latest_id = "+ius_latest[i].getId());
		//	System.out.println("iu_latest_version = "+ius_latest[i].getVersion());
		//}		
		
		Vector<CheckBoxNode> rootVector = new Vector<CheckBoxNode>();		
		for (int i=0; i<ius_latest.length; i++){
			String tmpStr = ius_latest[i].getId() + "----" + ius_latest[i].getVersion();
			rootVector.add(new CheckBoxNode(tmpStr, false));
		}

		tree = new InstallableUnitTree(rootVector);
				
		initComponents();
		btnInstall.setEnabled(false);
		
		tree.addMouseListener(this);

		btnCancel.addActionListener(this);
		btnInstall.addActionListener(this);

		setSize(400, 300);
		setVisible(true);
	}
	
	
	private void installIUs(){
		System.out.println("btnInstall is clicked. install now!");
		
		IInstallableUnit[] selectedIUs = getSelectedIUs();

		if (selectedIUs.length == 0){
			btnInstall.setEnabled(false);
			return;
		}
		
		System.out.println("\tSelected IUs: ");

		for (int i=0; i< selectedIUs.length; i++){
			System.out.println("\t\t"+selectedIUs[i].getId() + "----"+ selectedIUs[i].getVersion());
		}

		// run install in a different thread with task monitor
		//???
		
		automaticUpdater.doUpdate(getSelectedIUs());
		
		this.dispose();
	}
	
	
	private IInstallableUnit[] getSelectedIUs(){

		Vector<String> iuStrVect = new Vector<String>();
		Vector<IInstallableUnit> selectedIUVector = new Vector<IInstallableUnit>();

		TreeNode root = (TreeNode) tree.getModel().getRoot();
		Enumeration<DefaultMutableTreeNode> nodes = root.children();

		while (nodes.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
			CheckBoxNode chkNode = (CheckBoxNode) node.getUserObject();

			if (chkNode.isSelected()){
				iuStrVect.add(chkNode.getText());
			}
		}
		
		for (int i=0; i<iuStrVect.size(); i++) {
			String tmpStr = iuStrVect.elementAt(i);
			String[] items = tmpStr.split("----");
			
			String id = items[0];
			String version = items[1];
			
			for (int j=0;j<ius_latest.length;j++){
				if (ius_latest[j].getId().toString().equals(id) && ius_latest[j].getVersion().toString().equals(version)){ 
					selectedIUVector.add(ius_latest[j]);
					break;
				}
			}
		}

		IInstallableUnit[] returnValue = new IInstallableUnit[selectedIUVector.size()]; 
		for (int i=0; i<selectedIUVector.size(); i++){
			returnValue[i] = selectedIUVector.elementAt(i);
		}
		
		return returnValue;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj instanceof JButton){
			JButton btn = (JButton) obj;
			if (btn == btnCancel){
				this.dispose();
			}
			if (btn == btnInstall) {
				installIUs();
			}
		}
	}
	
	
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbAvailableUpdates = new javax.swing.JLabel();
        lbReviewConfirm = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        //jTree2 = new javax.swing.JTree();
        pnlButton = new javax.swing.JPanel();
        btnInstall = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        lbAvailableUpdates.setFont(new java.awt.Font("Tahoma", 1, 14));
        lbAvailableUpdates.setText("Available Updates");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        getContentPane().add(lbAvailableUpdates, gridBagConstraints);

        lbReviewConfirm.setText("Review and confirm that the checked update will be installed.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 0);
        getContentPane().add(lbReviewConfirm, gridBagConstraints);

        jScrollPane1.setViewportView(tree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 5);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        btnInstall.setText("Install");
        pnlButton.add(btnInstall);

        btnCancel.setText("Cancel");
        pnlButton.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 5);
        getContentPane().add(pnlButton, gridBagConstraints);

        pack();
    }// </editor-fold>                        
    

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnInstall;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbAvailableUpdates;
    private javax.swing.JLabel lbReviewConfirm;
    private javax.swing.JPanel pnlButton;
    // End of variables declaration                   

    
	private static JTree tree;


	// handle mouse event
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof JTree){
			JTree tree = (JTree) e.getSource();
			
			TreeNode root = (TreeNode) tree.getModel().getRoot();
			Enumeration<DefaultMutableTreeNode> nodes = root.children();

			boolean isIUSelected = false;
			while (nodes.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
				CheckBoxNode chkNode = (CheckBoxNode) node.getUserObject();

				if (chkNode.isSelected()){
					isIUSelected = true;
				}
			}
			
			if (isIUSelected){
				btnInstall.setEnabled(true);
			}
			else {
				btnInstall.setEnabled(false);
			}
		}
	}
	 public void mouseEntered(MouseEvent e) {}
	 public void mouseExited(MouseEvent e) {}
	 public void mousePressed(MouseEvent e) {}
	 public void mouseReleased(MouseEvent e) {}  
	


}
