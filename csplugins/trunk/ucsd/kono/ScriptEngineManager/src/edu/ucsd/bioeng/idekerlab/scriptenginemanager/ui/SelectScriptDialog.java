/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package edu.ucsd.bioeng.idekerlab.scriptenginemanager.ui;

import cytoscape.Cytoscape;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;

import cytoscape.util.FileUtil;

import edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManager;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManagerPlugin;
import edu.ucsd.bioeng.idekerlab.scriptenginemanager.engine.ScriptingEngine;

import java.awt.Color;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;


/**
 *
 */
public class SelectScriptDialog extends JDialog {
	private static SelectScriptDialog dialog = new SelectScriptDialog(null, true);
	private static String currentEngineID;
	private String scriptName = null;
	private Map<String, String> arguments = new HashMap<String, String>();

	// Variables declaration - do not modify
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton runButton;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JButton fileButton;
	private javax.swing.JPanel filePanel;
	private javax.swing.JTextField fileTextField;

	// End of variables declaration
	/**
	 *  DOCUMENT ME!
	 */
	public static void showDialog(String engineID) {
		final ScriptingEngine engine = ScriptEngineManagerPlugin.getManager().getEngine(engineID);

		if (engine == null)
			return;

		currentEngineID = engineID;
		dialog.titleLabel.setIcon(engine.getIcon());
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * Creates a new SelectScriptDialog object.
	 *
	 * @param parent  DOCUMENT ME!
	 * @param modal  DOCUMENT ME!
	 */
	public SelectScriptDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		pack();
		repaint();
	}

	private void initComponents() {
		// Background color for this plugin panel.
		this.getContentPane().setBackground(Color.white);
		titleLabel = new javax.swing.JLabel();
		titleLabel.setBackground(Color.white);
		filePanel = new javax.swing.JPanel();
		filePanel.setBackground(Color.white);
		fileTextField = new javax.swing.JTextField();
		fileTextField.setBackground(Color.white);
		fileButton = new javax.swing.JButton();
		fileButton.setBackground(Color.white);
		cancelButton = new javax.swing.JButton();
		cancelButton.setBackground(Color.white);
		runButton = new javax.swing.JButton();
		runButton.setBackground(Color.white);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Execute Script");
		setResizable(false);

		titleLabel.setFont(new java.awt.Font("SansSerif", 1, 16));
		titleLabel.setText("Run Script from File");

		filePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Script File",
		                                                                 javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                 javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                 new java.awt.Font("SansSerif",
		                                                                                   0, 12)));

		fileButton.setText("Select");
		fileButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					fileButtonActionPerformed(evt);
				}
			});

		org.jdesktop.layout.GroupLayout filePanelLayout = new org.jdesktop.layout.GroupLayout(filePanel);
		filePanel.setLayout(filePanelLayout);
		filePanelLayout.setHorizontalGroup(filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                  .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                       filePanelLayout.createSequentialGroup()
		                                                                      .addContainerGap()
		                                                                      .add(fileTextField,
		                                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                           438,
		                                                                           Short.MAX_VALUE)
		                                                                      .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                      .add(fileButton)
		                                                                      .addContainerGap()));
		filePanelLayout.setVerticalGroup(filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                .add(filePanelLayout.createSequentialGroup()
		                                                                    .add(filePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                                        .add(fileButton)
		                                                                                        .add(fileTextField,
		                                                                                             org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                             org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                             org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
		                                                                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                     Short.MAX_VALUE)));

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					cancelButtonActionPerformed(evt);
				}
			});

		runButton.setText("Execute");
		runButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					runButtonActionPerformed(evt);
				}
			});

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(filePanel,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           Short.MAX_VALUE).add(titleLabel)
		                                                      .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                           layout.createSequentialGroup()
		                                                                 .add(runButton)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                 .add(cancelButton)))
		                                           .addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(titleLabel)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(filePanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                          org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                          Short.MAX_VALUE)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(cancelButton).add(runButton))
		                                         .addContainerGap()));

		pack();
	} // </editor-fold>

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
		scriptName = fileTextField.getText();

		setVisible(false);
		runScript();
		dispose();
	}

	private void fileButtonActionPerformed(java.awt.event.ActionEvent evt) {
		final File file = FileUtil.getFile("Select Script File", FileUtil.LOAD);

		if (file == null)
			return;

		fileTextField.setText(file.getAbsolutePath());
		fileTextField.setToolTipText("Target Script File: " + file.getAbsolutePath());
		runButton.setEnabled(true);
	}

	private void runScript() {
		// Create Task
		Task task = new URLdownloadTask();

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);
		jTaskConfig.displayCancelButton(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	private class URLdownloadTask implements Task {
		private TaskMonitor taskMonitor;

		public URLdownloadTask() {
		}

		public void run() {
			taskMonitor.setStatus("Running Script: \n\n" + scriptName + "\n\n");
			taskMonitor.setPercentCompleted(-1);

			if (scriptName != null) {
				try {
					ScriptEngineManager.execute(currentEngineID, scriptName, arguments);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					taskMonitor.setException(e1, "Could not finish script.");
				}
			}

			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Finished!");
		}

		public void halt() {
		}

		public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
			this.taskMonitor = monitor;
		}

		public String getTitle() {
			return "Running Script: " + scriptName;
		}
	}
}
