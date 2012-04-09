package org.cytoscape.app.internal.swing.main;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.cytoscape.app.internal.exception.AppCopyException;
import org.cytoscape.app.internal.exception.AppParsingException;
import org.cytoscape.app.internal.manager.App;
import org.cytoscape.app.internal.manager.AppManager;
import org.cytoscape.app.internal.manager.AppParser;

public class InstallNewAppsPanel extends javax.swing.JPanel {

    private javax.swing.JButton installFromFileButton;
    private javax.swing.JButton installFromURLButton;
    private javax.swing.JButton installSelectedButton;
    private javax.swing.JLabel resultsLabel;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JTable resultsTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox searchComboBox;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JSeparator searchSeparator;
	
	private JFileChooser fileChooser;
	
	private AppManager appManager;
	
    public InstallNewAppsPanel(AppManager appManager) {
        this.appManager = appManager;
    	
    	initComponents();
        
        setupFileChooser();
        setupResultsTable();
    }

    private void initComponents() {
        installFromFileButton = new javax.swing.JButton();
        installFromURLButton = new javax.swing.JButton();
        searchSeparator = new javax.swing.JSeparator();
        searchLabel = new javax.swing.JLabel();
        searchComboBox = new javax.swing.JComboBox();
        searchButton = new javax.swing.JButton();
        resultsLabel = new javax.swing.JLabel();
        resultsScrollPane = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        installSelectedButton = new javax.swing.JButton();

        installFromFileButton.setText("Install from External File ..");
        installFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installFromFileButtonActionPerformed(evt);
            }
        });

        installFromURLButton.setText("Install from URL ..");
        installFromURLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installFromURLButtonActionPerformed(evt);
            }
        });

        searchLabel.setText("Search for Apps from the Web Store:");

        searchComboBox.setEditable(true);
        searchComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {""}));

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        resultsLabel.setText("Search Results:");
        
        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Name", "Version", "Author", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        
        resultsScrollPane.setViewportView(resultsTable);

        installSelectedButton.setText("Install Selected Apps");
        installSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installSelectedButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(searchSeparator)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, resultsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(installFromFileButton)
                            .add(installSelectedButton)
                            .add(resultsLabel)
                            .add(installFromURLButton)
                            .add(searchLabel)
                            .add(layout.createSequentialGroup()
                                .add(searchComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 288, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(searchButton)))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(installFromFileButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installFromURLButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(searchButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resultsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resultsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installSelectedButton)
                .addContainerGap())
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
				} finally {
					
					// Install the app if parsing was successful
					if (app != null) {
						try {
							appManager.installApp(app);
						} catch (AppCopyException e) {
							System.out.println("Error copying app: " + e.getMessage());
						}
					}
				}
        	}
        }
    }

    private void installFromURLButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void installSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {
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
    
    private void setupResultsTable() {
    	// resultsTable.setCellSelectionEnabled(false);

    	// resultsTable.setDefaultEditor(null, null);
    	resultsTable.setOpaque(true);
    }
}
