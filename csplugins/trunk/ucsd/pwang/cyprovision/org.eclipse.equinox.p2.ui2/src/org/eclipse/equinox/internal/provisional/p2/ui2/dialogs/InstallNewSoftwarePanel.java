package org.eclipse.equinox.internal.provisional.p2.ui2.dialogs;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import java.net.URISyntaxException;
import org.eclipse.equinox.internal.p2.ui2.ProvUIMessages;
import org.eclipse.equinox.internal.p2.ui2.model.MetadataRepositoryElement;
import org.eclipse.equinox.internal.p2.ui2.model.CategoryElement;
import org.eclipse.equinox.internal.provisional.p2.ui2.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui2.dialogs.AddSiteDialog;
import org.eclipse.equinox.internal.provisional.p2.ui2.model.MetadataRepositories;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.IUViewQueryContext;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.Policy;
import org.eclipse.equinox.internal.p2.ui2.model.QueriedElement;
import org.eclipse.equinox.internal.p2.ui2.model.AvailableIUElement;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import org.eclipse.equinox.internal.provisional.p2.ui2.dialogs.RepositoryManipulationDialog;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;


public class InstallNewSoftwarePanel extends JPanel implements ActionListener, ItemListener,MouseListener {

	JDialog dlg;
	Policy policy;
	String profileId;
	QueryableMetadataRepositoryManager manager;
	IUViewQueryContext queryContext;
	URI[] comboRepos;
	//private static final String ALL = ProvUIMessages.AvailableIUsPage_AllSites;
	//private static final int INDEX_ALL = 0;
	private String AllAvailableSites = "All Available Sites";
	private URI repositoryFilter = null;
	private RepositorySetting lastRepositorySetting = new RepositorySetting(); // use default setting
	
	/** Creates new form AvailableSoftwarePanel */
	public InstallNewSoftwarePanel(JDialog dlg, Policy policy, String profileId, QueryableMetadataRepositoryManager manager) {
		
		initComponents();
		
		// filter is a place-holder (not implemented yet), we may need it in the future
		tfFilter.setVisible(false);
		
		// we only implemented useCategoty, but we leave a place-holder for future
		useCategoriesCheckbox.setVisible(false);
		
		this.dlg = dlg;
		this.policy = policy;
		this.profileId = profileId;
		this.manager = manager;
		makeQueryContext();
		
		fillRepoCombo(null);
		
		// now initialize the repository tree
		initRepoTree();
		
		addEventListeners();
	}

	
	private void initRepoTree(){
		
		jTree1.setRootVisible(false);
		jTree1.setShowsRootHandles(true);
		
		CheckBoxTreeCellRenderer r = new CheckBoxTreeCellRenderer(jTree1, jTree1.getCellRenderer());
		jTree1.setCellRenderer(r);
		
        jTree1.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                // Invoke later to ensure all mouse handling is completed
                SwingUtilities.invokeLater(new Runnable() { public void run() {
                	CheckBoxTreeCellRenderer rr = (CheckBoxTreeCellRenderer)jTree1.getCellRenderer();
                	String text1 = createText(rr.getCheckedPaths());

                	System.out.println("\tgetCheckedPaths():\n"+ text1);
                }});
            }
        });

		DefaultTreeModel model= rebuildTreeModel();
        jTree1.setModel(model);

	}
		
	
	private static String createText(TreePath[] paths) {
        if (paths.length == 0) {
            return "Nothing checked";
        }
        String checked = "Checked:\n";
        for (int i=0;i < paths.length;i++) {
            checked += paths[i] + "\n";
        }
        return checked;
    }

	
	Object getNewInput() {
		
	    System.out.println("getNewInput(): repositoryFilter ="+repositoryFilter);
	       
		if (repositoryFilter != null) {
			return new MetadataRepositoryElement(queryContext, policy, repositoryFilter, true);
		}
		return new MetadataRepositories(queryContext, policy, manager);
	}

	
	private void addEventListeners(){
		repoCombo.addItemListener(this);

		btnCancel.addActionListener(this);
		btnInstall.addActionListener(this);
		btnAddSite.addActionListener(this);

		useCategoriesCheckbox.addItemListener(this);
		showLatestVersionsCheckbox.addItemListener(this);
		hideInstalledCheckbox.addItemListener(this);
		
		lbAvailableSoftwareSites.addMouseListener(this);
		lbAlreadyInstalled.addMouseListener(this);	
		
		TreeSelectionListener treeSelectionListener = new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent tse){
				JTree theTree =  (JTree) tse.getSource();
				TreePath[] selectedPaths = theTree.getSelectionPaths();
				
				if (selectedPaths.length == 1){
					TreePath thePath = selectedPaths[0];
					DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
					Object node_userObj = theNode.getUserObject();
					//System.out.println("node_userObj.getClass() = "+node_userObj.getClass());
					if (node_userObj instanceof AvailableIUElement){
						AvailableIUElement avail_iu_element = (AvailableIUElement) node_userObj;
						
						IInstallableUnit installUnit =avail_iu_element.getIU();
						if (installUnit.getProperty(IInstallableUnit.PROP_DESCRIPTION) == null){
							taDetails.setText("No description is available");
						}
						else {
							taDetails.setText(installUnit.getProperty(IInstallableUnit.PROP_DESCRIPTION));
						}
					}
				}
				else if (selectedPaths.length > 1){
					taDetails.setText("");
				}

			}
		};
		jTree1.addTreeSelectionListener(treeSelectionListener);
		
	}
	
	void fillRepoCombo(final String selection) {
				
		if (repoCombo == null || policy.getRepositoryManipulator() == null)
			return;
		comboRepos = policy.getRepositoryManipulator().getKnownRepositories();
		final String[] items = new String[comboRepos.length + 1];
		items[0] = AllAvailableSites; // i.e. "All Available Sites"
		for (int i = 0; i < comboRepos.length; i++){
			items[i + 1] = comboRepos[i].toString();
		}
		
		DefaultComboBoxModel model = new DefaultComboBoxModel(items);
		
		repoCombo.setModel(model);
		
		if (selection !=null && !selection.equals("")){
			repoCombo.setSelectedItem(selection);
		}
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
		//queryContext.showAlreadyInstalled();//profileId);
		//System.out.println();
		
		//queryContext.setShowLatestVersionsOnly(false);
		queryContext.setShowLatestVersionsOnly(defaultQueryContext.getShowLatestVersionsOnly());

		queryContext.setVisibleAvailableIUProperty(defaultQueryContext.getVisibleAvailableIUProperty()); // org.eclipse.equinox.p2.type.group
		queryContext.setVisibleInstalledIUProperty(defaultQueryContext.getVisibleInstalledIUProperty()); // org.eclipse.equinox.p2.type.root
				
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
		}
		if (ie.getSource() instanceof JComboBox) {
			JComboBox cmb = (JComboBox) ie.getSource();
			if (cmb == repoCombo) {
				//System.out.println("repoCombo state changed");
			}
		}
				
		updateJTree();
	}

	
	private void updateJTree(){
		// make sure do updating only if there is any change in the setting
		RepositorySetting currentRepSetting = new RepositorySetting((String)repoCombo.getSelectedItem(), 
				useCategoriesCheckbox.isSelected(),showLatestVersionsCheckbox.isSelected(),hideInstalledCheckbox.isSelected());
		
		// If there is no change in setting, do not update the tree
		if (currentRepSetting.equals(lastRepositorySetting)){
			return;
		}
		else {
			lastRepositorySetting = currentRepSetting;
		}
		
		// The setting has been changed, do updating now
		System.out.println("updateJTree()...");
				
		try {
			repositoryFilter =new URI(currentRepSetting.getSelectedRepository());
		}
		catch (URISyntaxException uri_e){
			repositoryFilter = null;
		}

		if (currentRepSetting.getSelectedRepository().equalsIgnoreCase(this.AllAvailableSites)){
			repositoryFilter = null;
		}
		updateQueryContext(currentRepSetting);

		DefaultTreeModel newModel= rebuildTreeModel();
        jTree1.setModel(newModel);
	}
	
    
	private DefaultTreeModel rebuildTreeModel(){

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");                
		DefaultMutableTreeNode parent;

		Object obj = getNewInput(); 

		System.out.println("obj.getClass()"+ obj.getClass());

		if (obj instanceof MetadataRepositories){

			System.out.println("Case for MetadataRepositories");

			MetadataRepositories repos = (MetadataRepositories) obj;

			if (!repos.hasChildren(null)){
				return new DefaultTreeModel(null);
			}

			Object[] catElementObjs = repos.getChildren(null);
			for (int i=0; i< catElementObjs.length; i++){
				parent = new DefaultMutableTreeNode();
				parent.setUserObject(catElementObjs[i]);

				CategoryElement catElement = (CategoryElement) catElementObjs[i];
				
				if (catElement.hasChildren(null)){
					Object[] cat_children = catElement.getChildren(null);
					for (int j=0; j<cat_children.length; j++){
						DefaultMutableTreeNode iu2 = new DefaultMutableTreeNode();
						iu2.setUserObject(cat_children[j]);
						parent.add(iu2);
					}
				}
				root.add(parent);
			}
		}   
		else if (obj instanceof MetadataRepositoryElement){
			MetadataRepositoryElement repoElement = (MetadataRepositoryElement) obj;

			if (!repoElement.hasChildren(null)){
				return new DefaultTreeModel(null);
			}

			//parent = new DefaultMutableTreeNode();
			//parent.setUserObject(repoElement);

			
			Object[] cat_children = repoElement.getChildren(null);	
			
			System.out.println("repoElement has " + cat_children.length+ " Children");

			for (int j=0; j<cat_children.length; j++){
				
				System.out.println("\t child type : " +cat_children[j].getClass());
				
				parent = new DefaultMutableTreeNode();
				parent.setUserObject(cat_children[j]);
				
				if (cat_children[j] instanceof CategoryElement){
					CategoryElement cat_element = (CategoryElement) cat_children[j];
					Object[] childrenObjs = cat_element.getChildren(null);

					for (int k=0; k<childrenObjs.length; k++){
					
						DefaultMutableTreeNode iu3 = new DefaultMutableTreeNode();
						iu3.setUserObject(childrenObjs[k]);
						parent.add(iu3);
					}
					
				}
				
				root.add(parent);
			}


			
		}
		else {
			//this should not happen, otherwise there is something wrong
		}

		return new DefaultTreeModel(root);
	}

	
	private DefaultTreeModel rebuildTreeModel_1(){
		
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");                
        DefaultMutableTreeNode parent;
		
		if (repositoryFilter != null){
			MetadataRepositoryElement repoElement = (MetadataRepositoryElement)getNewInput();
			
			String name = ((AvailableIUElement)repoElement.getChildren(null)[0]).getIU().getProperty("org.eclipse.equinox.p2.name");
			parent = new DefaultMutableTreeNode(name);
			
			Object[] availIUElementObjs = repoElement.getChildren(null);
			
    		for (int j=0; j<availIUElementObjs.length; j++){
    			AvailableIUElement availIUElement = (AvailableIUElement) availIUElementObjs[j];
    		
    			String iu_name_version = availIUElement.getIU().getProperty("org.eclipse.equinox.p2.name") + " --- version " +availIUElement.getIU().getVersion();
    			DefaultMutableTreeNode iu1 = new DefaultMutableTreeNode(iu_name_version);
    			parent.add(iu1);
    		}

    		root.add(parent);
		}
		else { //repositoryFilter = null, i.e. Selected "All Available Sites"
	        MetadataRepositories repos = (MetadataRepositories) getNewInput();
	        
	        Object [] objs = repos.getChildren(null);
	        
	        int repoCount = objs.length;
	        
	        for (int i=0; i< repoCount; i++){
	        	CategoryElement catElement = (CategoryElement) objs[i];
	    		
	        	String cat_name = catElement.getIU().getProperty("org.eclipse.equinox.p2.name");
	        	parent = new DefaultMutableTreeNode(cat_name);
	        	        	
	    		Object[] availIUElementObjs =  catElement.getChildren(null);
	    		
	    		for (int j=0; j<availIUElementObjs.length; j++){
	    			AvailableIUElement availIUElement = (AvailableIUElement) availIUElementObjs[j];
	    		
	    			String iu_name_version = availIUElement.getIU().getProperty("org.eclipse.equinox.p2.name") + " --- version " +availIUElement.getIU().getVersion();
	    			DefaultMutableTreeNode iu1 = new DefaultMutableTreeNode(iu_name_version);
	    			parent.add(iu1);
	    		}
	        	
	    		root.add(parent);
	        }
		}

        return new DefaultTreeModel(root);
	}

	
	private void updateQueryContext(RepositorySetting pRepQuerySetting){
		//
		IUViewQueryContext defaultQueryContext = policy.getQueryContext();
		queryContext = new IUViewQueryContext(defaultQueryContext.getViewType()); // defaultQueryContext.getViewType() =1
		
		queryContext.setArtifactRepositoryFlags(defaultQueryContext.getArtifactRepositoryFlags()); //defaultQueryContext.getArtifactRepositoryFlags()=2		
		queryContext.setMetadataRepositoryFlags(defaultQueryContext.getMetadataRepositoryFlags());
		
		if (pRepQuerySetting.getHideInstalled()) {
			queryContext.hideAlreadyInstalled(profileId);
		}
		queryContext.setShowLatestVersionsOnly(pRepQuerySetting.getShowLatestVersions());
		
		//queryContext.setUseCategories(pRepQuerySetting.getUseCategories());
		queryContext.setUseCategories(true);
		
		
		if (pRepQuerySetting.getSelectedRepository().equalsIgnoreCase(AllAvailableSites)) {
			//availableIUGroup.setRepositoryFilter(null);
			repositoryFilter = null;
		} else {
			//availableIUGroup.setRepositoryFilter(pRepQuerySetting.getSelectedRepository());
			try {
				repositoryFilter = new URI(pRepQuerySetting.getSelectedRepository());				
			}
			catch (URISyntaxException uri_exp){
				repositoryFilter = null;
			}
		}
		
		queryContext.setVisibleAvailableIUProperty(defaultQueryContext.getVisibleAvailableIUProperty());
		queryContext.setVisibleInstalledIUProperty(defaultQueryContext.getVisibleInstalledIUProperty());

	}
	
		
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
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlDetails = new javax.swing.JPanel();
        taDetails = new javax.swing.JTextArea();
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

        repoCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All Available Sites", "Item 4" }));
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
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlTree, gridBagConstraints);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Details"));
        pnlDetails.setLayout(new java.awt.GridBagLayout());

        pnlDetails.setMinimumSize(new java.awt.Dimension(0, 50));
        pnlDetails.setPreferredSize(new java.awt.Dimension(0, 50));
        taDetails.setBackground(new java.awt.Color(212, 208, 200));
        taDetails.setColumns(20);
        taDetails.setEditable(false);
        taDetails.setRows(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlDetails.add(taDetails, gridBagConstraints);

        jScrollPane2.setViewportView(pnlDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        add(jScrollPane2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        showLatestVersionsCheckbox.setSelected(true);
        showLatestVersionsCheckbox.setText("Show only the latest versions of available software");
        showLatestVersionsCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showLatestVersionsCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(showLatestVersionsCheckbox, gridBagConstraints);

        useCategoriesCheckbox.setSelected(true);
        useCategoriesCheckbox.setText("Group items by category");
        useCategoriesCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useCategoriesCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(useCategoriesCheckbox, gridBagConstraints);

        hideInstalledCheckbox.setSelected(true);
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
        gridBagConstraints.gridx = 1;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lbAlreadyInstalled;
    private javax.swing.JLabel lbAvailableSoftwareSites;
    private javax.swing.JPanel pnlButton;
    private javax.swing.JPanel pnlDetails;
    private javax.swing.JPanel pnlTree;
    private javax.swing.JComboBox repoCombo;
    private javax.swing.JCheckBox showLatestVersionsCheckbox;
    private javax.swing.JTextArea taAvailableSoftware;
    private javax.swing.JTextArea taDetails;
    private javax.swing.JTextField tfFilter;
    private javax.swing.JCheckBox useCategoriesCheckbox;
    // End of variables declaration                   

	
	
    
	class RepositorySetting {
		// default setting
		private String selectedRepository = AllAvailableSites;
		private boolean useCategories = true;
		private boolean showLatestVersions = true;
		private boolean hideInstalled = false;

		// default constructor
		public RepositorySetting(){}
		
		public RepositorySetting(String selectedRepo, boolean useCategories, boolean showLatestVersions, boolean hideInstalled){
			this.selectedRepository = selectedRepo;
			this.useCategories = useCategories;
			this.showLatestVersions = showLatestVersions;
			this.hideInstalled = hideInstalled;		
		}
		
		public String getSelectedRepository(){
			return selectedRepository;
		}
		public boolean getUseCategories(){
			return useCategories;
		}
		public boolean getShowLatestVersions(){
			return showLatestVersions;
		}
		public boolean getHideInstalled(){
			return hideInstalled;
		}

		public void setSelectedRepository(String pRepositoryName){
			selectedRepository = pRepositoryName;
		}
		
		public void setUseCategories(boolean pUseCategories){
			useCategories = pUseCategories;
		}
		public void setShowLatestVersions(boolean pShowLatestVersions){
			showLatestVersions = pShowLatestVersions;
		}
		public void setHideInstalled(boolean pHideInstalled){
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

		public String toString(){
		
			String retValue = "\tCurrent setting:";
			retValue  += "\tRespitory combobox: selectedItem = "+ selectedRepository;
			retValue  += "\tuseCategoriesCheckbox.isSelected() ="+useCategories;
			retValue += "\tshowLatestVersionsCheckbox.isSelected() ="+showLatestVersions;
			retValue += "\thideInstalledCheckbox.isSelected() ="+ hideInstalled;
			
			return retValue;
		}
	}
}
