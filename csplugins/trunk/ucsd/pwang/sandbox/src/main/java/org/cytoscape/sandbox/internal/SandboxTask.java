package org.cytoscape.sandbox.internal;


import org.cytoscape.application.swing.CySwingApplication;
import javax.swing.JButton;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.GUITaskManager;
import org.cytoscape.session.CyApplicationManager;

public class SandboxTask extends AbstractTask {

	private CySwingApplication desktopApp;
	private CyApplicationManager appMgr;
	private CyNetworkManager netMgr;
	private CyTableManager tableMgr;
	private GUITaskManager taskMgr;

	public SandboxTask(CySwingApplication desktopApp, CyApplicationManager appMgr, CyNetworkManager netMgr,
			CyTableManager tableMgr,  GUITaskManager taskMgr) {
		
		this.desktopApp = desktopApp;
		this.appMgr = appMgr;
		this.netMgr = netMgr;
		this.tableMgr = tableMgr;
		this.taskMgr = taskMgr;

	}

	
	/**
	 * This method processes the chosen input file and output type and attempts
	 * to write the file.
	 * @param tm The {@link org.cytoscape.work.TaskMonitor} provided by the TaskManager execution environment.
	 */
	public final void run(TaskMonitor tm) throws Exception {


		System.out.println("Executing sandbox Task ....");
	}

}
