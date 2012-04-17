package org.cytoscape.app.internal.ui;

import java.awt.Container;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;
import org.cytoscape.app.internal.exception.AppParsingException;
import org.cytoscape.app.internal.manager.App;
import org.cytoscape.app.internal.manager.AppManager;
import org.cytoscape.app.internal.manager.AppParser;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;

public class InstallAppsPanel extends javax.swing.JPanel {

	private javax.swing.JComboBox enterUrlComboBox;
    private javax.swing.JLabel enterUrlLabel;
    private javax.swing.JButton installFromFileButton;
    private javax.swing.JLabel installFromFileLabel;
    private javax.swing.JLabel installFromURLLabel;
    private javax.swing.JButton installFromUrlButton;
	
    private AppManager appManager;
    private FileUtil fileUtil;
    private JFileChooser fileChooser;
    
    /**
     * A reference to the parent of this panel used to create the filechooser dialog.
     */
    private Container parent;
    
    /**
     * Creates new form InstallAppsPanel
     * @param fileUtil 
     */
    public InstallAppsPanel(AppManager appManager, FileUtil fileUtil, Container parent) {
        this.appManager = appManager;
    	this.fileUtil = fileUtil;
    	this.parent = parent;
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

    	// Setup a the file filter for the open file dialog
    	FileChooserFilter fileChooserFilter = new FileChooserFilter("Jar, Zip Files (*.jar, *.zip)",
    			new String[]{"jar", "zip"});
    	
    	Collection<FileChooserFilter> fileChooserFilters = new LinkedList<FileChooserFilter>();
    	fileChooserFilters.add(fileChooserFilter);
    	
    	// Show the dialog
    	File[] files = fileUtil.getFiles(parent, 
    			"Choose file(s)", FileUtil.LOAD, FileUtil.LAST_DIRECTORY, "Install", true, fileChooserFilters);
    	
        if (files != null) {
        	
        	for (int index = 0; index < files.length; index++) {
        		AppParser appParser = appManager.getAppParser();
        		
        		App app = null;
        		
        		// Attempt to parse each file as an App object
        		try {
					app = appParser.parseApp(files[index]);
					
				} catch (AppParsingException e) {
					
					// TODO: Replace System.out.println() messages with exception or a pop-up message box
					System.out.println("Error parsing app: " + e.getMessage());
					
					JOptionPane.showMessageDialog(parent, "Error opening app: " + e.getMessage(),
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
