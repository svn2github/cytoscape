package org.cytoscape.sandbox.internal;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.GUITaskManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskFactory;

public class SandboxTaskFactory implements TaskFactory  {

	private CySwingApplication desktopApp;
	private CyApplicationManager appMgr;
	private CyNetworkManager netMgr;
	private CyTableManager tableMgr;
	private GUITaskManager taskMgr;
	
	public SandboxTaskFactory(CySwingApplication desktopApp, CyApplicationManager appMgr, 
			CyNetworkManager netMgr, CyTableManager tableMgr, GUITaskManager taskMgr){
		
		this.desktopApp = desktopApp;
		this.appMgr = appMgr;
		this.netMgr = netMgr;
		this.tableMgr = tableMgr;
		this.taskMgr = taskMgr;
		
		// Add a button on tool-bar with position index = 5
		JButton btn = new JButton("Sandbox");
		btn.addActionListener(new SanboxActionListener());
		this.desktopApp.getJToolBar().add(btn,5);
	}
	

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new SandboxTask(desktopApp, appMgr, netMgr,tableMgr, taskMgr));
	}
	
	/////
	class SanboxActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e){
			System.out.println("\nSandbox button is clicked!");
		}
	}
}
