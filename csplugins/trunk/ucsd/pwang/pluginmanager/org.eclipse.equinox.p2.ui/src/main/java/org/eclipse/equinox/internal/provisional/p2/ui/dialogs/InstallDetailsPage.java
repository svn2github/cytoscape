package org.eclipse.equinox.internal.provisional.p2.ui.dialogs;

import java.awt.Component;

import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardController;
//import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.p2.ui.PlanAnalyzer;
import org.eclipse.equinox.internal.p2.ui.model.QueriedElement;
import org.eclipse.equinox.internal.p2.ui.ProvUIMessages;
import org.eclipse.equinox.internal.p2.ui.model.IIUElement;
import org.eclipse.equinox.internal.p2.ui.model.AvailableIUElement;
import org.eclipse.equinox.internal.p2.ui.model.CategoryElement;
import org.eclipse.equinox.internal.p2.ui.model.ElementUtils;
import org.eclipse.equinox.internal.p2.ui.model.MetadataRepositoryElement;

import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.ILicense;
import org.eclipse.equinox.internal.provisional.p2.query.IQueryable;
import org.eclipse.equinox.internal.provisional.p2.ui.IStatusCodes;
import org.eclipse.equinox.internal.provisional.p2.ui.ProvisioningOperationRunner;
//import org.eclipse.equinox.internal.provisional.p2.ui.ProvUI;
import org.eclipse.equinox.internal.provisional.p2.ui.ResolutionResult;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.PlannerResolutionOperation;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
import org.eclipse.equinox.internal.provisional.p2.ui.IUPropertyUtils;
import org.eclipse.equinox.internal.provisional.p2.ui.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.model.MetadataRepositories;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
//import org.eclipse.jface.dialogs.IMessageProvider;
//import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
//import org.eclipse.ui.statushandlers.StatusManager;

import java.awt.event.ComponentAdapter;

import org.eclipse.core.runtime.NullProgressMonitor;
//import org.eclipse.equinox.internal.provisional.p2.ui.actions.InstallAction;


public class InstallDetailsPage extends WizardPage {
	Policy policy;
	String profileId;
	QueryableMetadataRepositoryManager manager;
    private HashSet checkedIUElements = null;

    String problemStr = null;
    
	public InstallDetailsPage(Policy policy, String profileId, QueryableMetadataRepositoryManager manager){
		this.policy = policy;
		this.profileId = profileId;
		this.manager = manager;
		
		this.putWizardData("profileId",this.profileId);
		
		initComponents();		
		 
		initRepoTree();
			
		//TreeSelectionListener treeSelectionListener = new TreeSelectionListener(){
		//		public void valueChanged(TreeSelectionEvent tse){
					// Handle the detail area
		//			updateDetails();
		//		}
		//	};
			
		//jTree1.addTreeSelectionListener(treeSelectionListener);
		
		//Populate the JTree when the panel show up the first time
		ComponentAdapter componentAdapter = new ComponentAdapter(){
		    public void componentMoved(ComponentEvent e)  {
		    	//System.out.println("component has been moved");
		    	recycle();
		    }
		};
		this.addComponentListener(componentAdapter);
		
	}
	
	private void updateSizeInfo(){
		
		Runnable sizeJob = new Runnable(){
			public void run(){
				try {
					if (resolvedOperation != null){
						long size = ProvisioningUtil.getSize(resolvedOperation.getProvisioningPlan(), profileId, null);
						lbSize.setText(getFormattedSize(size));											
					}
				} catch (ProvisionException e) {
					e.getStatus();
				}
			}
		};

		Thread newThread = new Thread(sizeJob);
		newThread.start();
	}
	
	protected String getFormattedSize(long size) {
		if (size == IIUElement.SIZE_UNKNOWN || size == IIUElement.SIZE_UNAVAILABLE)
			return "Size unknown";//ProvUIMessages.IUDetailsLabelProvider_Unknown;
		if (size > 1000L) {			
			long kb = size / 1000L;
			return "Size: "+ new Long(kb).toString() + " kb";//NLS.bind(ProvUIMessages.IUDetailsLabelProvider_KB, NumberFormat.getInstance().format(new Long(kb)));
		}

		return "Size: "+new Long(size).toString() + " bytes";//NLS.bind(ProvUIMessages.IUDetailsLabelProvider_Bytes, NumberFormat.getInstance().format(new Long(size)));
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
    	if (problemStr !=null){
    		return problemStr;
    	}
    	return null;
    }
    
    
    protected  void recycle()  {
   	
    	problemStr = null;
    	
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
		this.putWizardData("LicenseMap",licenseMap);
 
        // validate bundle dependency, if fail, setProblem() to disable "Next" button
        recomputePlan();
    	
		DefaultTreeModel model= rebuildTreeModel();
        jTree1.setModel(model);
        
    }
    
    
    private void recomputePlan(){
    	String errorMsg = "";

    	final Object[] elements = checkedIUElements.toArray();
    	final IInstallableUnit[] ius = ElementUtils.elementsToIUs(elements);
    	couldNotResolve = false;

    	if (elements.length == 0) {
    		couldNotResolve();
    	} else
    	{
    		Runnable theJob = new Runnable(){
    			public void run(){
    				resolvedOperation = null;
    				resolutionResult = null;
    				IProgressMonitor monitor = new NullProgressMonitor();

    				MultiStatus status = PlanAnalyzer.getProfileChangeAlteredStatus();
    				ProfileChangeRequest request = computeProfileChangeRequest(elements, status, monitor);

    				if (request != null) {
    					resolvedOperation = new PlannerResolutionOperation(ProvUIMessages.ProfileModificationWizardPage_ResolutionOperationLabel, ius, profileId, request, status, false);
    					try {
    						resolvedOperation.execute(monitor);
    					} catch (ProvisionException e) {
    						//ProvUI.handleException(e, null, StatusManager.SHOW | StatusManager.LOG);
    						couldNotResolve();
    					}
    					if (resolvedOperation.getProvisioningPlan() != null) {
    						resolutionResult = resolvedOperation.getResolutionResult();
    						// set up the iu parents to be the plan so that drilldown query can work
    						if (resolvedOperation.getProvisioningPlan() != null)
    							for (int i = 0; i < elements.length; i++) {
    								if (elements[i] instanceof QueriedElement) {
    									((QueriedElement) elements[i]).setQueryable(getQueryable(resolvedOperation.getProvisioningPlan()));
    								}
    							}
    					}
    				}
    				updateStatus();
    				updateSizeInfo();
    				InstallDetailsPage.this.putWizardData("resolutionResult",resolutionResult);
    				InstallDetailsPage.this.putWizardData("resolvedOperation",resolvedOperation);
    			}
    		};

    		Thread newThread = new Thread(theJob);
    		newThread.start();
    	}
    }

    
	void updateStatus() {
		/*
 		IStatus currentStatus;
		if (taDetails == null || !taDetails.isVisible())
			return;
		int messageType = IMessageProvider.NONE;
		boolean pageComplete = true;
		if (couldNotResolve) {
			currentStatus = new Status(IStatus.ERROR, ProvUIActivator.PLUGIN_ID, 0, ProvUIMessages.ProfileModificationWizardPage_UnexpectedError, null);
			//this.setProblem("Cannot complete the install because some dependencies are not satisfiable");
			problemStr = "Cannot complete the install because some dependencies are not satisfiable";
		} else {
			currentStatus = resolvedOperation.getResolutionResult().getSummaryStatus();
		}
				
		if (currentStatus != null && !currentStatus.isOK()) {
			messageType = IMessageProvider.INFORMATION;
			int severity = currentStatus.getSeverity();
			if (severity == IStatus.ERROR) {
				messageType = IMessageProvider.ERROR;
				pageComplete = false;
				
				this.setProblem("Error");
				// Log errors for later support, but not if these are 
				// simple UI validation errors.
				if (currentStatus.getCode() != IStatusCodes.EXPECTED_NOTHING_TO_DO)
					ProvUI.reportStatus(currentStatus, StatusManager.LOG);
			} else if (severity == IStatus.WARNING) {
				messageType = IMessageProvider.WARNING;
				// Log warnings for later support
				ProvUI.reportStatus(currentStatus, StatusManager.LOG);
			}
		} else {
			// Check to see if another operation is in progress
			if (ProvisioningOperationRunner.hasScheduledOperationsFor(profileId)) {
				messageType = IMessageProvider.ERROR;
				currentStatus = PlanAnalyzer.getStatus(IStatusCodes.OPERATION_ALREADY_IN_PROGRESS, null);
				pageComplete = false;
				//this.setProblem("Another operation is in progress");
				problemStr = "Another operation is in progress";
			}
			
		}
		
		//setPageComplete(pageComplete);
		//setMessage(getMessageText(currentStatus), messageType);
				
		if ((messageType == IMessageProvider.NONE || messageType == IMessageProvider.INFORMATION) && pageComplete){
			// everything is fine, so check license to determine if license page should be showed
			HashMap licenseMap = (HashMap) this.getWizardData("LicenseMap");
	    	if (licenseMap.size() == 0){
	    		// There is no license, enable finish button
	    		this.setForwardNavigationMode(WizardController.MODE_CAN_FINISH);
	    	}
		}
		
		if (resolutionResult.getSummaryStatus().getSeverity() == IStatus.ERROR)
		{
			//System.out.println("setProblem --> error");
			//this.setProblem("Error!");
			problemStr = "Error";
		}

		String detail = getDetailText(); 
		if (detail != null){
			taDetails.setText(detail);			
		}
		else {
			taDetails.setText("");
		}
		*/
	}


	String getDetailText() {
		String detail = null;
		IInstallableUnit iu = getSelectedIU();
		
		// We tried to resolve and it failed.  The specific error was already reported, so description
		// text can be used for the selected IU.
		if (couldNotResolve) {
			if (iu != null)
				detail = getIUDescription(iu);
			return detail;
		}

		// An IU is selected and we have resolved.  Look for information about the specific IU.
		if (iu != null) {
			detail = resolutionResult.getDetailedReport(new IInstallableUnit[] {iu});
			if (detail != null){
				return detail;
			}
			// No specific error about this IU.  Show the overall error if it is in error.
			if (resolutionResult.getSummaryStatus().getSeverity() == IStatus.ERROR)
			{
				return resolutionResult.getSummaryReport();
			}
			// The overall status is not an error, so we may as well just return info about this iu rather than everything.
			return getIUDescription(iu);
		}

		//No IU is selected, give the overall report
		detail = resolutionResult.getSummaryReport();
		if (detail == null)
			detail = ""; //$NON-NLS-1$
		return detail;
	}


	String getMessageText(IStatus currentStatus) {
		if (currentStatus == null || currentStatus.isOK())
			return getDescription();
		if (currentStatus.getSeverity() == IStatus.CANCEL)
			return ProvUIMessages.ResolutionWizardPage_Canceled;
		if (currentStatus.getSeverity() == IStatus.ERROR)
			return ProvUIMessages.ResolutionWizardPage_ErrorStatus;
		return ProvUIMessages.ResolutionWizardPage_WarningInfoStatus;
	}
    

	protected String getIUDescription(IInstallableUnit iu) {
		// Get the iu description in the default locale
		String description = IUPropertyUtils.getIUProperty(iu, IInstallableUnit.PROP_DESCRIPTION);
		if (description == null)
			description = ""; //$NON-NLS-1$
		return description;
	}

	
	protected IInstallableUnit getSelectedIU() {
		IInstallableUnit[] units = ElementUtils.elementsToIUs(checkedIUElements.toArray());
		if (units.length == 0)
			return null;
		return units[0];
	}

    
	protected ProfileChangeRequest computeProfileChangeRequest(Object[] selectedElements, MultiStatus additionalStatus, IProgressMonitor monitor) {
		IInstallableUnit[] selected = ElementUtils.elementsToIUs(selectedElements);
		return null;//nstallAction.computeProfileChangeRequest(selected, profileId, additionalStatus, monitor);
	}

    protected IQueryable getQueryable(ProvisioningPlan plan){
    	return plan.getAdditions();
    }
    
    
	private void couldNotResolve() {
		resolvedOperation = null;
		resolutionResult = null;
		couldNotResolve = true;
	}

	PlannerResolutionOperation resolvedOperation;
	ResolutionResult resolutionResult;
	boolean couldNotResolve;

    
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
