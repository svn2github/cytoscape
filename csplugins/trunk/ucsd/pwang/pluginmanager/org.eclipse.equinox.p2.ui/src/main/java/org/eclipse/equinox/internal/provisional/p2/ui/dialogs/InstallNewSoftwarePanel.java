package org.eclipse.equinox.internal.provisional.p2.ui.dialogs;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import java.net.URISyntaxException;
import org.eclipse.equinox.internal.p2.ui.ProvUIMessages;
import org.eclipse.equinox.internal.p2.ui.model.MetadataRepositoryElement;
import org.eclipse.equinox.internal.p2.ui.model.CategoryElement;
import org.eclipse.equinox.internal.provisional.p2.ui.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.AddSiteDialog;
import org.eclipse.equinox.internal.provisional.p2.ui.model.MetadataRepositories;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.IUViewQueryContext;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.equinox.internal.p2.ui.model.QueriedElement;
import org.eclipse.equinox.internal.p2.ui.model.AvailableIUElement;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.ui.IUPropertyUtils;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.RepositoryManipulationDialog;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util. Enumeration;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;

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
	private HashSet checkedIUElements = null;

	/** Creates new form AvailableSoftwarePanel */
	public InstallNewSoftwarePanel(JDialog dlg, Policy policy, String profileId, QueryableMetadataRepositoryManager manager) {
		
		initComponents();
		
		// filter is a place-holder (not implemented yet), we may need it in the future
		tfFilter.setVisible(false);
		
		// we only implemented useCategoty, but we leave a place-holder for future
		useCategoriesCheckbox.setVisible(false);
		
		//This may confuse user, so hide it for now
		hideInstalledCheckbox.setVisible(false);
		
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
		
		RepoCellRenderer r1 = new RepoCellRenderer();
		jTree1.setCellRenderer(r1);
		
		CheckBoxTreeCellRenderer r = new CheckBoxTreeCellRenderer(jTree1, jTree1.getCellRenderer());		
		
		jTree1.setCellRenderer(r);
		
        jTree1.addMouseListener(new MyMouseListener() );

		DefaultTreeModel model= rebuildTreeModel();
        jTree1.setModel(model);
	}
		
	
	private class MyMouseListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            // Invoke later to ensure all mouse handling is completed
            SwingUtilities.invokeLater(new Runnable() { public void run() {
            	updateCheckedIUs();
            	// Disable btnInstall if nothing is checked or the whole tree is checked
            	if (checkedIUElements.size() == 0){
            		btnInstall.setEnabled(false);
            	}
            	else {
            		btnInstall.setEnabled(true);
            	}
            }});
        }		
	}
	
	
	private void updateCheckedIUs() {
		
		CheckBoxTreeCellRenderer rr = (CheckBoxTreeCellRenderer) jTree1.getCellRenderer();
		TreePath[] checkedPaths = rr.getCheckedPaths();
		
		checkedIUElements = new HashSet();
		
		if (checkedPaths.length == 0) {
			return;
        }

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) checkedPaths[0].getLastPathComponent();
		if (checkedPaths.length == 1 && node.getUserObject().toString().equalsIgnoreCase("JTree") && repositoryFilter == null) {
			// selected all IUs in "All Available sites", this is not allowed  
			return;
        }
				
		for (int i=0;i < checkedPaths.length;i++) {
			node = (DefaultMutableTreeNode) checkedPaths[i].getLastPathComponent();

			for (Enumeration nodes = node.depthFirstEnumeration(); nodes.hasMoreElements();){
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) nodes.nextElement();
				if (treeNode.getUserObject() instanceof AvailableIUElement){
					checkedIUElements.add((AvailableIUElement)treeNode.getUserObject());
				}
			}
        }
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
				// Handle the detail area
				JTree theTree =  (JTree) tse.getSource();
				
				TreePath[] selectedPaths = theTree.getSelectionPaths();
				
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

				System.out.println("\nXXXXXXXXXX "+ getDateTime() +"\n");
				Iterator it = checkedIUElements.iterator();
				while (it.hasNext()){
					AvailableIUElement element = (AvailableIUElement) it.next(); 
					System.out.println(element.getIU().getId()+ "--" + element.getIU().getVersion());
				}
				System.out.println("\n");


			} else if (btn == btnAddSite) {
				//System.out.println("btnAddSite is clicked");
				AddSiteDialog addSiteDlg = new AddSiteDialog(dlg, true, policy);
				addSiteDlg.setLocationRelativeTo(dlg);
				addSiteDlg.setSize(400, 150);
				addSiteDlg.setVisible(true);
			}
		}
	}




	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	} 


	
	public void itemStateChanged(ItemEvent ie) {

		/*
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
		*/		
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
		CheckBoxTreeCellRenderer rr = (CheckBoxTreeCellRenderer) jTree1.getCellRenderer();
		rr.clearCheckedPaths();
        jTree1.setModel(newModel);
	}
	
    
	private DefaultTreeModel rebuildTreeModel(){

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");                
		DefaultMutableTreeNode parent;

		Object obj = getNewInput(); 

		if (obj instanceof MetadataRepositories){
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

			Object[] cat_children = repoElement.getChildren(null);	
			for (int j=0; j<cat_children.length; j++){
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
				RepositoryManipulationDialog repoManDlg = new RepositoryManipulationDialog(true, policy);
				repoManDlg.setSize(600, 500);
				repoManDlg.setLocationRelativeTo(InstallNewSoftwarePanel.this);
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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        lbAlreadyInstalled.setForeground(new java.awt.Color(0, 0, 255));
        lbAlreadyInstalled.setText("already installed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(lbAlreadyInstalled, gridBagConstraints);

        jLabel4.setText(" ?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
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
