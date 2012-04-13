package org.cytoscape.app.internal.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.cytoscape.app.internal.exception.AppParsingException;
import org.cytoscape.app.internal.manager.App;
import org.cytoscape.app.internal.manager.AppManager;
import org.cytoscape.app.internal.manager.AppParser;

public class InstallAppsPanel extends javax.swing.JPanel {

	private javax.swing.JComboBox enterUrlComboBox;
    private javax.swing.JLabel enterUrlLabel;
    private javax.swing.JButton installFromFileButton;
    private javax.swing.JLabel installFromFileLabel;
    private javax.swing.JLabel installFromURLLabel;
    private javax.swing.JButton installFromUrlButton;
	
    private AppManager appManager;
    private JFileChooser fileChooser;
    
    /**
     * Creates new form InstallAppsPanel
     */
    public InstallAppsPanel(AppManager appManager) {
        this.appManager = appManager;
    	
    	initComponents();
    	
    	setupFileChooser();
    }

    private void initComponents() {

        installFromFileButton = new javax.swing.JButton();
        installFromUrlButton = new javax.swing.JButton();
        installFromFileLabel = new javax.swing.JLabel();
        installFromURLLabel = new javax.swing.JLabel();
        enterUrlLabel = new javax.swing.JLabel();
        enterUrlComboBox = new javax.swing.JComboBox();

        installFromFileButton.setText("Select File(s)");
        installFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installFromFileButtonActionPerformed(evt);
            }
        });

        installFromUrlButton.setText("Install");
        installFromUrlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installFromUrlButtonActionPerformed(evt);
            }
        });

        installFromFileLabel.setText("Install from File");

        installFromURLLabel.setText("Install from URL (for a list of URLs, separate with semicolon)");

        enterUrlLabel.setText("URL(s):");

        enterUrlComboBox.setEditable(true);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(installFromFileLabel)
                    .add(installFromFileButton)
                    .add(installFromURLLabel)
                    .add(layout.createSequentialGroup()
                        .add(enterUrlLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(enterUrlComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 239, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(installFromUrlButton)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(installFromFileLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installFromFileButton)
                .add(18, 18, 18)
                .add(installFromURLLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(enterUrlLabel)
                    .add(enterUrlComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(installFromUrlButton))
                .addContainerGap(236, Short.MAX_VALUE))
        );
    }

    private void installFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	int returnValue = fileChooser.showOpenDialog(this);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File[] files = fileChooser.getSelectedFiles();
        	
        	for (int index = 0; index < files.length; index++) {
        		AppParser appParser = appManager.getAppParser();
        		
        		App app = null;
        		
        		// Attempt to parse each file as an App object
        		try {
					app = appParser.parseApp(files[index]);
					
				} catch (AppParsingException e) {
					
					// TODO: Replace System.out.println() messages with exception or a pop-up message box
					System.out.println("Error parsing app: " + e.getMessage());
					
					JOptionPane.showMessageDialog(this, "Error opening app: " + e.getMessage(),
		                       "Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					
					// Install the app if parsing was successful
					if (app != null) {
						appManager.installApp(app);
					}
				}
        	}
        }
    }

    private void installFromUrlButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }
    
    private void setupFileChooser() {
    	fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select App Files");
        fileChooser.setApproveButtonText("Install");
        fileChooser.setApproveButtonMnemonic('I');
        fileChooser.setMultiSelectionEnabled(true);
        
        fileChooser.addChoosableFileFilter(new FileFilter(){

			@Override
			public boolean accept(File file) {
				if (file.getName().endsWith("jar") || file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return "Jar-packaged App Files (*.jar)";
			}
        	
        });
	}
}
