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

import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

import edu.ucsd.bioeng.idekerlab.scriptenginemanager.ScriptEngineManager;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;


/**
 *
 */
public class SelectScriptDialog extends JDialog {
	private static final String DEF_ENGINE_NAME = "ruby";
	private static SelectScriptDialog dialog = new SelectScriptDialog(null, true);
	private String scriptName = null;
	private Map<String, String> arguments = new HashMap<String, String>();

	// Variables declaration - do not modify
	private javax.swing.JButton cancelButton;
	private javax.swing.JTextField fileNameTextField;
	private javax.swing.JPanel parameterPanel;
	private javax.swing.JTextField parameterTextField;
	private javax.swing.JButton runButton;
	private javax.swing.JPanel scriptFilePanel;
	private javax.swing.JButton selectButton;
	private javax.swing.JLabel titleLabel;

	// End of variables declaration
	/**
	 *  DOCUMENT ME!
	 */
	public static void showDialog() {
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
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
		parameterPanel.setVisible(false);
		pack();
		repaint();
	}

	private void initComponents() {
		titleLabel = new javax.swing.JLabel();
		scriptFilePanel = new javax.swing.JPanel();
		fileNameTextField = new javax.swing.JTextField();
		selectButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		runButton = new javax.swing.JButton();
		parameterPanel = new javax.swing.JPanel();
		parameterTextField = new javax.swing.JTextField();

		setTitle("Run Script");

		titleLabel.setFont(new java.awt.Font("SansSerif", 1, 14));
		titleLabel.setText("<html><body> Run Script</body></html>");
		//titleLabel.setBorder(new MatteBorder(0, 5, 1, 0, new Color(200, 0, 20, 150)));
		scriptFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Script File",
		                                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                       new java.awt.Font("SansSerif",
		                                                                                         0,
		                                                                                         13)));

		selectButton.setText("Select");
		selectButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					selectButtonActionPerformed(evt);
				}
			});

		org.jdesktop.layout.GroupLayout scriptFilePanelLayout = new org.jdesktop.layout.GroupLayout(scriptFilePanel);
		scriptFilePanel.setLayout(scriptFilePanelLayout);
		scriptFilePanelLayout.setHorizontalGroup(scriptFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                              .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                                   scriptFilePanelLayout.createSequentialGroup()
		                                                                                        .addContainerGap()
		                                                                                        .add(fileNameTextField,
		                                                                                             org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                             373,
		                                                                                             Short.MAX_VALUE)
		                                                                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                                        .add(selectButton)));
		scriptFilePanelLayout.setVerticalGroup(scriptFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                            .add(scriptFilePanelLayout.createSequentialGroup()
		                                                                                      .add(scriptFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                                                                .add(selectButton)
		                                                                                                                .add(fileNameTextField,
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

		runButton.setText("Run!");
		runButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					runButtonActionPerformed(evt);
				}
			});

		parameterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                      "Command Line Arguments",
		                                                                      javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                      javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                      new java.awt.Font("SansSerif",
		                                                                                        0,
		                                                                                        13)));

		org.jdesktop.layout.GroupLayout parameterPanelLayout = new org.jdesktop.layout.GroupLayout(parameterPanel);
		parameterPanel.setLayout(parameterPanelLayout);
		parameterPanelLayout.setHorizontalGroup(parameterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                            .add(parameterPanelLayout.createSequentialGroup()
		                                                                                     .addContainerGap()
		                                                                                     .add(parameterTextField,
		                                                                                          org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                          437,
		                                                                                          Short.MAX_VALUE)
		                                                                                     .addContainerGap()));
		parameterPanelLayout.setVerticalGroup(parameterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                          .add(parameterPanelLayout.createSequentialGroup()
		                                                                                   .add(parameterTextField,
		                                                                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                                                   .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                    Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
		                                                      .add(layout.createSequentialGroup()
		                                                                 .addContainerGap()
		                                                                 .add(titleLabel)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                                                  279,
		                                                                                  Short.MAX_VALUE))
		                                                      .add(layout.createSequentialGroup()
		                                                                 .addContainerGap()
		                                                                 .add(runButton)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
		                                           .add(cancelButton))
		                                .add(scriptFilePanel,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     Short.MAX_VALUE)
		                                .add(parameterPanel,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(titleLabel)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(scriptFilePanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(parameterPanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(cancelButton).add(runButton))
		                                         .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                          Short.MAX_VALUE)));
		pack();
	} // </editor-fold>

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
		scriptName = fileNameTextField.getText();

		String paramStr = parameterTextField.getText();
		paramStr = paramStr.replace(" ", "");

		String[] params = null;

		if (paramStr.length() != 0) {
			params = paramStr.split(",");

			if (params.length != 0) {
				String[] parts;

				for (int i = 0; i < params.length; i++) {
					parts = params[i].split("=");

					if (parts.length == 2) {
						arguments.put(parts[0], parts[1]);
					}
				}
			}
		}

		setVisible(false);
		runScript();
		dispose();
	}

	private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		CyFileFilter tempCFF = new CyFileFilter();

		final File file = FileUtil.getFile("Select Script File", FileUtil.LOAD);

		fileNameTextField.setText(file.getAbsolutePath());
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
					ScriptEngineManager.execute(DEF_ENGINE_NAME, scriptName, arguments);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					taskMonitor.setException(e1, "Could not finish script.");
				}
			}

			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Script run successfully!");
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
