package org.eclipse.equinox.internal.provisional.p2.ui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import javax.swing.*;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.internal.p2.ui.DefaultMetadataURLValidator;
import org.eclipse.equinox.internal.provisional.p2.ui.ProvisioningOperationRunner;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningOperation;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.RepositoryOperation;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.*;
//import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.equinox.internal.p2.ui.model.MetadataRepositoryElement;
//import org.eclipse.ui.statushandlers.StatusManager;


public class AddSiteDialog extends JDialog implements ActionListener {

	private Policy policy;
	private File localFile = null;
	private RepositoryLocationValidator urlValidator;
	private String url;
	private CachedMetadataRepositories cachedMetadataRepository = null;;
	
	//private MetadataRepositoryElement element;
	//private Hashtable elements = null;
	
	/** Creates new form AddSiteDialog */
	public AddSiteDialog(boolean modal, Policy policy) {
		//super(modal);
		this.setModal(modal);
		this.policy = policy;
		init();
	}


	public AddSiteDialog(JDialog parent, boolean modal, Policy policy) {
		super(parent, modal);
		this.policy = policy;
		init();
	}
	
	public AddSiteDialog(JDialog parent, boolean modal, Policy policy, CachedMetadataRepositories cachedMetadataRepository) {
		super(parent, modal);
		this.policy = policy;
		this.cachedMetadataRepository = cachedMetadataRepository;
		init();
	}
	
	private void init() {
		initComponents();
		btnArchive.setVisible(false);
		btnLocal.addActionListener(this);
		btnOK.addActionListener(this);
		btnCancel.addActionListener(this);
		urlValidator = createRepositoryLocationValidator();		
	}
	
	protected RepositoryLocationValidator createRepositoryLocationValidator() {
		DefaultMetadataURLValidator validator = new DefaultMetadataURLValidator();
		//validator.setKnownRepositoriesFlag(repoFlag);
		return validator;
	}

	protected IStatus validateRepositoryURL(final boolean contactRepositories) {
		if (url == null || url.trim().equals(""))
			return Status.OK_STATUS;
		final IStatus[] status = new IStatus[1];
		status[0] = RepositoryLocationValidator.getInvalidLocationStatus(url.trim());
		final URI userLocation = getUserLocation();
		//if (url.length() == 0)
		//	status[0] = new Status(IStatus.ERROR, ProvUIActivator.PLUGIN_ID, RepositoryLocationValidator.LOCAL_VALIDATION_ERROR, ProvUIMessages.RepositoryGroup_URLRequired, null);
		//else if (userLocation == null)
		//	status[0] = new Status(IStatus.ERROR, ProvUIActivator.PLUGIN_ID, RepositoryLocationValidator.LOCAL_VALIDATION_ERROR, ProvUIMessages.AddRepositoryDialog_InvalidURL, null);
		//else {
		//Runnable newThread = new Runnable(){
		//public void run() {
		status[0] = getRepositoryLocationValidator().validateRepositoryLocation(userLocation, contactRepositories, null);
		//}
		//};

		//newThread.run();
		//}

		// At this point the subclasses may have decided to opt out of
		// this dialog.
		//if (status[0].getSeverity() == IStatus.CANCEL) {
		//cancelPressed();
		//}

		//setOkEnablement(status[0].isOK());
		//updateStatus(status[0]);
		return status[0];

	}

	protected RepositoryLocationValidator getRepositoryLocationValidator() {
		return urlValidator;
	}

	/**
	 * Get the repository location as currently typed in by the user.  Return null if there
	 * is a problem with the URL.
	 * 
	 * @return the URL currently typed in by the user.
	 */
	protected URI getUserLocation() {
		URI userLocation;
		try {
			userLocation = URIUtil.fromString(tfLocation.getText().trim());
		} catch (URISyntaxException e) {
			return null;
		}
		return userLocation;
	}

	protected boolean addRepository() {
		if (cachedMetadataRepository != null){			
			// add to the HashTable only
			cachedMetadataRepository.cachedElements.put(getUserLocation().toString(), new MetadataRepositoryElement(cachedMetadataRepository,getUserLocation(),true));
			return true;
		}
		
		if (validateRepositoryURL(false).isOK()) {
			System.out.println("AddSiteDialog.addRepository(): add now");

			ProvisioningOperationRunner.schedule(getOperation(getUserLocation()), 0); //StatusManager.SHOW | StatusManager.LOG);
			return true;
		}
		return false;
	}

	protected ProvisioningOperation getOperation(URI repositoryLocation) {
		final RepositoryManipulator repoMan = policy.getRepositoryManipulator();

		RepositoryOperation op = repoMan.getAddOperation(repositoryLocation);
		op.setNotify(false);
		return op;

	}

	private boolean isDuplicatedURL(String url) {
		if (cachedMetadataRepository == null || cachedMetadataRepository.cachedElements.size() ==0){
			return false;
		}
		if (cachedMetadataRepository.cachedElements.containsKey(url)){
			return true;
		}
		return false;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();

			if (btn == btnOK) {
				System.out.println("btnOK is clicked");

				url = tfLocation.getText();

				if (isDuplicatedURL(url)){
					JOptionPane.showMessageDialog(this, "Repository URL already existed!", "Warning", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (validateRepositoryURL(true).isOK()) {
					addRepository();
					this.dispose();
				} else {
					JOptionPane.showMessageDialog(this, "Invalid repository URL", "Warning", JOptionPane.ERROR_MESSAGE);
				}

			} else if (btn == btnCancel) {
				this.dispose();
			} else if (btn == btnLocal) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					localFile = chooser.getSelectedFile();
					//System.out.println("You chose to open this file: " + localFile.getName());
					tfLocation.setText(localFile.toURI().toString());
				}
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

		lbLocation = new javax.swing.JLabel();
		tfLocation = new javax.swing.JTextField();
		pnlLocalArchive = new javax.swing.JPanel();
		btnLocal = new javax.swing.JButton();
		btnArchive = new javax.swing.JButton();
		pnlButton = new javax.swing.JPanel();
		btnOK = new javax.swing.JButton();
		btnCancel = new javax.swing.JButton();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Add Site");
		lbLocation.setText("Location:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 10);
		getContentPane().add(lbLocation, gridBagConstraints);

		tfLocation.setText("http://");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
		getContentPane().add(tfLocation, gridBagConstraints);

		pnlLocalArchive.setLayout(new java.awt.GridBagLayout());

		btnLocal.setText("Local...");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		pnlLocalArchive.add(btnLocal, gridBagConstraints);

		btnArchive.setText("Archive...");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		pnlLocalArchive.add(btnArchive, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 10);
		getContentPane().add(pnlLocalArchive, gridBagConstraints);

		pnlButton.setLayout(new java.awt.GridBagLayout());

		btnOK.setText("OK");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
		pnlButton.add(btnOK, gridBagConstraints);

		btnCancel.setText("Cancel");
		pnlButton.add(btnCancel, new java.awt.GridBagConstraints());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(30, 0, 20, 10);
		getContentPane().add(pnlButton, gridBagConstraints);

		pack();
	}// </editor-fold>                        

	// Variables declaration - do not modify                     
	private javax.swing.JButton btnArchive;
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnLocal;
	private javax.swing.JButton btnOK;
	private javax.swing.JLabel lbLocation;
	private javax.swing.JPanel pnlButton;
	private javax.swing.JPanel pnlLocalArchive;
	private javax.swing.JTextField tfLocation;
	// End of variables declaration                   

}
