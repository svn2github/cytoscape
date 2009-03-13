package org.eclipse.equinox.internal.provisional.p2.ui2.dialogs;

import java.awt.event.*;
import javax.swing.*;

import org.eclipse.equinox.internal.p2.ui2.ProvUIMessages;
import org.eclipse.equinox.internal.provisional.p2.ui2.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui2.dialogs.AddSiteDialog;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.IUViewQueryContext;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.Policy;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import org.eclipse.equinox.internal.provisional.p2.ui2.dialogs.RepositoryManipulationDialog;

public class InstallNewSoftwarePanel extends JPanel implements ActionListener, ItemListener,MouseListener {

	JDialog dlg;
	Policy policy;
	String profileId;
	QueryableMetadataRepositoryManager manager;
	IUViewQueryContext queryContext;
	URI[] comboRepos;
	private static final String ALL = ProvUIMessages.AvailableIUsPage_AllSites;
	private static final int INDEX_ALL = 0;
	private String AllAvailableSites = "All Available Sites";
	
	/** Creates new form AvailableSoftwarePanel */
	public InstallNewSoftwarePanel(JDialog dlg, Policy policy, String profileId, QueryableMetadataRepositoryManager manager) {
		
		initComponents();
		this.dlg = dlg;
		this.policy = policy;
		this.profileId = profileId;
		this.manager = manager;
		makeQueryContext();
		
		fillRepoCombo(null);
		
		repoCombo.addItemListener(this);
		
		//taAvailableSoftware.setEditable(false);
		
		btnCancel.addActionListener(this);
		btnInstall.addActionListener(this);
		btnAddSite.addActionListener(this);

		useCategoriesCheckbox.addItemListener(this);
		showLatestVersionsCheckbox.addItemListener(this);
		hideInstalledCheckbox.addItemListener(this);
		
		lbAvailableSoftwareSites.addMouseListener(this);
		lbAlreadyInstalled.addMouseListener(this);
		
	}

	
	void fillRepoCombo(final String selection) {
		
		//System.out.println("fillRepoCombo");
		
		if (repoCombo == null || policy.getRepositoryManipulator() == null)
			return;
		comboRepos = policy.getRepositoryManipulator().getKnownRepositories();
		final String[] items = new String[comboRepos.length + 1];
		items[0] = AllAvailableSites;
		for (int i = 0; i < comboRepos.length; i++){
			items[i + 1] = comboRepos[i].toString();
			//System.out.println("\t"+ items[i + 1]);
		}
		
		DefaultComboBoxModel model = new DefaultComboBoxModel(items);
		
		repoCombo.setModel(model);		
	}

	
	void repoComboSelectionChanged() {
		/*
		int selection = repoCombo.getSelectionIndex();
		if (comboRepos == null || selection > comboRepos.length)
			selection = INDEX_ALL;

		if (selection == INDEX_ALL) {
			availableIUGroup.setRepositoryFilter(null);
		} else if (selection > 0) {
			availableIUGroup.setRepositoryFilter(comboRepos[selection - 1]);
		}
		*/
	}

	
	private void makeQueryContext() {
		// Make a local query context that is based on the default.
		IUViewQueryContext defaultQueryContext = policy.getQueryContext();
		queryContext = new IUViewQueryContext(defaultQueryContext.getViewType());
		
		//System.out.println("defaultQueryContext.getViewType()="+defaultQueryContext.getViewType()); // 1
		
		queryContext.setArtifactRepositoryFlags(defaultQueryContext.getArtifactRepositoryFlags());
		
		//System.out.println("defaultQueryContext.getArtifactRepositoryFlags()="+defaultQueryContext.getArtifactRepositoryFlags()); //2
		
		queryContext.setMetadataRepositoryFlags(defaultQueryContext.getMetadataRepositoryFlags());
		if (defaultQueryContext.getHideAlreadyInstalled()) {
			queryContext.hideAlreadyInstalled(profileId);
		}
		queryContext.setShowLatestVersionsOnly(defaultQueryContext.getShowLatestVersionsOnly());
		queryContext.setVisibleAvailableIUProperty(defaultQueryContext.getVisibleAvailableIUProperty());
		queryContext.setVisibleInstalledIUProperty(defaultQueryContext.getVisibleInstalledIUProperty());
	}

	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();
			if (btn == btnCancel) {
				dlg.dispose();
			} else if (btn == btnInstall) {
				System.out.println("btnInstall is clicked");
			} else if (btn == btnAddSite) {
				//System.out.println("btnAddSite is clicked");
				AddSiteDialog addSiteDlg = new AddSiteDialog(dlg, true, policy);
				addSiteDlg.setLocationRelativeTo(dlg);
				addSiteDlg.setSize(400, 150);
				addSiteDlg.setVisible(true);
			}

		}
	}

	public void itemStateChanged(ItemEvent ie) {

		if (ie.getSource() instanceof JCheckBox) {
			JCheckBox chk = (JCheckBox) ie.getSource();
			if (chk == useCategoriesCheckbox) {
				//System.out.println("chbGroupByCategory is clicked");
			} else if (chk == showLatestVersionsCheckbox) {
				//System.out.println("chkShowOnlyLatestVersion is clicked");
			} else if (chk == hideInstalledCheckbox) {
				//System.out.println("btnAddSite is clicked");
			}
			//updateJTree();
		}
		if (ie.getSource() instanceof JComboBox) {
			JComboBox cmb = (JComboBox) ie.getSource();
			if (cmb == repoCombo) {
				//updateJTree();
			}
		}
		updateJTree();
	}

	
	private void updateJTree(){
		// make sure do update only if there is any change in the setting
		RepositorySetting currentRepSetting = new RepositorySetting((String)repoCombo.getSelectedItem(), 
				useCategoriesCheckbox.isSelected(),showLatestVersionsCheckbox.isSelected(),hideInstalledCheckbox.isSelected());
		if (currentRepSetting.equals(lastRepositorySetting)){
			return;
		}
		else {
			lastRepositorySetting = currentRepSetting;
		}
		System.out.println("updateJTree()...");
		
		
		//System.out.println("\tCurrent setting:");
		//String selectedItem = (String) repoCombo.getSelectedItem();
		//System.out.println("\tRespitory combobox: selectedItem = "+ selectedItem);
		//System.out.println("\tuseCategoriesCheckbox.isSelected() ="+useCategoriesCheckbox.isSelected());
		//System.out.println("\tshowLatestVersionsCheckbox.isSelected() ="+showLatestVersionsCheckbox.isSelected());
		//System.out.println("\thideInstalledCheckbox.isSelected() ="+hideInstalledCheckbox.isSelected());
		
		System.out.println("\tQuery metadataRepository based on setting ...");
		updateQueryContext(currentRepSetting);
		
	}
	
	
	private void updateQueryContext(RepositorySetting pRepQuerySetting){
		//
		IUViewQueryContext defaultQueryContext = policy.getQueryContext();
		queryContext = new IUViewQueryContext(defaultQueryContext.getViewType());
		
		//System.out.println("defaultQueryContext.getViewType()="+defaultQueryContext.getViewType()); // 1
		
		queryContext.setArtifactRepositoryFlags(defaultQueryContext.getArtifactRepositoryFlags());
		
		//System.out.println("defaultQueryContext.getArtifactRepositoryFlags()="+defaultQueryContext.getArtifactRepositoryFlags()); //2
		
		queryContext.setMetadataRepositoryFlags(defaultQueryContext.getMetadataRepositoryFlags());
		if (pRepQuerySetting.getHideInstalled()) {
			queryContext.hideAlreadyInstalled(profileId);
		}
		queryContext.setShowLatestVersionsOnly(pRepQuerySetting.getShowLatestVersions());
		queryContext.setUseCategories(pRepQuerySetting.getUseCategories());
		
		
		if (pRepQuerySetting.getSelectedRepository().equalsIgnoreCase(AllAvailableSites)) {
			//availableIUGroup.setRepositoryFilter(null);
		} else {
			//availableIUGroup.setRepositoryFilter(pRepQuerySetting.getSelectedRepository());
		}
		
		queryContext.setVisibleAvailableIUProperty(defaultQueryContext.getVisibleAvailableIUProperty());
		queryContext.setVisibleInstalledIUProperty(defaultQueryContext.getVisibleInstalledIUProperty());

	}
	
	
	
	private RepositorySetting lastRepositorySetting = new RepositorySetting(); // use default setting
	
	
	// mouseListener
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof JLabel){
			JLabel lb = (JLabel)e.getSource();
			if (lb == lbAvailableSoftwareSites){
				//System.out.println("lbAvailableSoftwareSites is clicked");
				//policy.getRepositoryManipulator().manipulateRepositories(getShell());
				RepositoryManipulationDialog repoManDlg = new RepositoryManipulationDialog(dlg, true, policy);
				repoManDlg.setSize(600, 500);
				repoManDlg.setLocationRelativeTo(dlg);
				repoManDlg.setVisible(true);
			}
			else if (lb == lbAlreadyInstalled){
				System.out.println("lbAlreadyInstalled is clicked, not implemented yet!");				
			}
		}
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	

	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        taAvailableSoftware = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        repoCombo = new javax.swing.JComboBox();
        btnAddSite = new javax.swing.JButton();
        tfFilter = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lbAvailableSoftwareSites = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pnlTree = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        pnlDetails = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        showLatestVersionsCheckbox = new javax.swing.JCheckBox();
        useCategoriesCheckbox = new javax.swing.JCheckBox();
        hideInstalledCheckbox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        lbAlreadyInstalled = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pnlButton = new javax.swing.JPanel();
        btnInstall = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        taAvailableSoftware.setColumns(20);
        taAvailableSoftware.setEditable(false);
        taAvailableSoftware.setFont(new java.awt.Font("Monospaced", 1, 14));
        taAvailableSoftware.setRows(5);
        taAvailableSoftware.setText("Available Software\nCheck the items that you wish to install\n");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        add(taAvailableSoftware, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Work with:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(jLabel3, gridBagConstraints);

        repoCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { AllAvailableSites, "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(repoCombo, gridBagConstraints);

        btnAddSite.setText("Add...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel5.add(btnAddSite, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 373;
        gridBagConstraints.ipady = 45;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(jPanel5, gridBagConstraints);

        tfFilter.setText("Type filter text");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(tfFilter, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jLabel6, gridBagConstraints);

        jLabel1.setText("Go to the ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        lbAvailableSoftwareSites.setForeground(new java.awt.Color(0, 51, 255));
        lbAvailableSoftwareSites.setText("Available Software Sites");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(lbAvailableSoftwareSites, gridBagConstraints);

        jLabel5.setText(" preferences");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(jPanel1, gridBagConstraints);

        pnlTree.setLayout(new java.awt.GridBagLayout());

        pnlTree.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        pnlTree.setPreferredSize(new java.awt.Dimension(276, 324));
        pnlTree.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(jTree1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlTree.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlTree, gridBagConstraints);

        pnlDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Details"));
        pnlDetails.setMinimumSize(new java.awt.Dimension(0, 50));
        pnlDetails.setPreferredSize(new java.awt.Dimension(0, 50));
        javax.swing.GroupLayout pnlDetailsLayout = new javax.swing.GroupLayout(pnlDetails);
        pnlDetails.setLayout(pnlDetailsLayout);
        pnlDetailsLayout.setHorizontalGroup(
            pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
        );
        pnlDetailsLayout.setVerticalGroup(
            pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        add(pnlDetails, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        showLatestVersionsCheckbox.setSelected(true);
        showLatestVersionsCheckbox.setText("Show only the latest versions of available software");
        showLatestVersionsCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showLatestVersionsCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(showLatestVersionsCheckbox, gridBagConstraints);

        useCategoriesCheckbox.setText("Group items by category");
        useCategoriesCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useCategoriesCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(useCategoriesCheckbox, gridBagConstraints);

        hideInstalledCheckbox.setText("Hide items that are already installed");
        hideInstalledCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        hideInstalledCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        jPanel3.add(hideInstalledCheckbox, gridBagConstraints);

        jLabel2.setText("What is ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        lbAlreadyInstalled.setForeground(new java.awt.Color(0, 0, 255));
        lbAlreadyInstalled.setText("already installed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(lbAlreadyInstalled, gridBagConstraints);

        jLabel4.setText(" ?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        add(jPanel3, gridBagConstraints);

        btnInstall.setText("Install");
        pnlButton.add(btnInstall);

        btnCancel.setText("Cancel");
        pnlButton.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 20, 10);
        add(pnlButton, gridBagConstraints);

    }// </editor-fold>                        
    
    
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnAddSite;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnInstall;
    private javax.swing.JCheckBox hideInstalledCheckbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lbAlreadyInstalled;
    private javax.swing.JLabel lbAvailableSoftwareSites;
    private javax.swing.JPanel pnlButton;
    private javax.swing.JPanel pnlDetails;
    private javax.swing.JPanel pnlTree;
    private javax.swing.JComboBox repoCombo;
    private javax.swing.JCheckBox showLatestVersionsCheckbox;
    private javax.swing.JTextArea taAvailableSoftware;
    private javax.swing.JTextField tfFilter;
    private javax.swing.JCheckBox useCategoriesCheckbox;
    // End of variables declaration                   
 
	class RepositorySetting {
		// default setting
		private String selectedRepository = AllAvailableSites;
		boolean useCategories = false;
		boolean showLatestVersions = true;
		boolean hideInstalled = false;

		// default constructor
		public RepositorySetting(){}
		
		public RepositorySetting(String selectedRepo, boolean useCategories, boolean showLatestVersions, boolean hideInstalled){
			this.selectedRepository = selectedRepo;
			this.useCategories = useCategories;
			this.showLatestVersions = showLatestVersions;
			this.hideInstalled = hideInstalled;		
		}
		
		String getSelectedRepository(){
			return selectedRepository;
		}
		boolean getUseCategories(){
			return useCategories;
		}
		boolean getShowLatestVersions(){
			return showLatestVersions;
		}
		boolean getHideInstalled(){
			return hideInstalled;
		}

		void setSelectedRepository(String pRepositoryName){
			selectedRepository = pRepositoryName;
		}
		
		void setUseCategories(boolean pUseCategories){
			useCategories = pUseCategories;
		}
		void setShowLatestVersions(boolean pShowLatestVersions){
			showLatestVersions = pShowLatestVersions;
		}
		void setHideInstalled(boolean pHideInstalled){
			hideInstalled = pHideInstalled;
		}
		
		public boolean equals(RepositorySetting pOtherSetting){
			if (pOtherSetting.getSelectedRepository() != selectedRepository){
				return false;
			}
			if (pOtherSetting.getShowLatestVersions() != showLatestVersions){
				return false;
			}
			if (pOtherSetting.getUseCategories() != useCategories){
				return false;
			}
			if (pOtherSetting.getHideInstalled() != hideInstalled){
				return false;
			}
			return true;
		}
	}

    
}
