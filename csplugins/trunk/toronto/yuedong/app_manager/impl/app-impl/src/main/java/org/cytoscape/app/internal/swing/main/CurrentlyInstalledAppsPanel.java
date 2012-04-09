package org.cytoscape.app.internal.swing.main;

import javax.swing.table.DefaultTableModel;

import org.cytoscape.app.internal.event.AppEvent;
import org.cytoscape.app.internal.event.AppListener;
import org.cytoscape.app.internal.manager.App;
import org.cytoscape.app.internal.manager.AppManager;

public class CurrentlyInstalledAppsPanel extends javax.swing.JPanel {

    private javax.swing.JLabel appsAvailableCountLabel;
    private javax.swing.JLabel appsAvailableLabel;
    private javax.swing.JTable appsAvailableTable;
    private javax.swing.JLabel appsInstalledCountLabel;
    private javax.swing.JLabel appsInstalledLabel;
    private javax.swing.JButton disableSelectedButton;
    private javax.swing.JButton enableSelectedButton;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox showTypeComboxBox;
    private javax.swing.JLabel showTypeLabel;
	
    private AppManager appManager;
    private AppListener appListener;
    
    public CurrentlyInstalledAppsPanel(AppManager appManager) {
        initComponents();
        
        this.appManager = appManager;
        
        setupAppListener();
        populateTable();
    }

    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        appsAvailableTable = new javax.swing.JTable();
        appsInstalledLabel = new javax.swing.JLabel();
        appsAvailableLabel = new javax.swing.JLabel();
        appsInstalledCountLabel = new javax.swing.JLabel();
        appsAvailableCountLabel = new javax.swing.JLabel();
        enableSelectedButton = new javax.swing.JButton();
        disableSelectedButton = new javax.swing.JButton();
        showTypeComboxBox = new javax.swing.JComboBox();
        showTypeLabel = new javax.swing.JLabel();

        appsAvailableTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "App", "Name", "Version", "Author", "Description", "Status"
            }
        ) {
			private static final long serialVersionUID = 572679933790969414L;

			public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        appsAvailableTable.removeColumn(appsAvailableTable.getColumn("App"));
        
        jScrollPane2.setViewportView(appsAvailableTable);

        appsInstalledLabel.setText("Number of Apps Installed:");

        appsAvailableLabel.setText("Apps available: ");

        appsInstalledCountLabel.setText("0");

        appsAvailableCountLabel.setText("0");

        enableSelectedButton.setText("Enable Selected");
        enableSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableSelectedButtonActionPerformed(evt);
            }
        });

        disableSelectedButton.setText("Disable Selected");
        disableSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disableSelectedButtonActionPerformed(evt);
            }
        });

        showTypeComboxBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Installed", "Uninstalled"}));
        showTypeComboxBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTypeComboxBoxActionPerformed(evt);
            }
        });

        showTypeLabel.setText("Show:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(appsAvailableLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(appsAvailableCountLabel))
                            .add(layout.createSequentialGroup()
                                .add(appsInstalledLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(appsInstalledCountLabel))
                            .add(layout.createSequentialGroup()
                                .add(showTypeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(showTypeComboxBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(enableSelectedButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(disableSelectedButton)))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(appsInstalledLabel)
                    .add(appsInstalledCountLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(appsAvailableLabel)
                    .add(appsAvailableCountLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(showTypeLabel)
                    .add(showTypeComboxBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(enableSelectedButton)
                    .add(disableSelectedButton))
                .addContainerGap())
        );
    }

    private void enableSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void disableSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void showTypeComboxBoxActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }
    
    private void setupAppListener() {
    	appListener = new AppListener() {

			@Override
			public void appInstalled(AppEvent event) {
				
				// Prepare to add a new row containing the app's data to the table
				DefaultTableModel tableModel = (DefaultTableModel) appsAvailableTable.getModel();
				
				App app = event.getApp();
				tableModel.addRow(new Object[]{
						app,
						app.getAppName(),
						app.getVersion(),
						app.getAuthors(),
						app.getDescription(),
						app.getStatus()
				});
				
				updateLabels();
			}

			@Override
			public void appUninstalled(AppEvent event) {
				
				// Prepare to remove the row containing the app from the table
				DefaultTableModel tableModel = (DefaultTableModel) appsAvailableTable.getModel();
				
				App app = event.getApp();
				int rowToBeRemoved = -1;
				for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
					if (tableModel.getValueAt(rowIndex, 0) == app) {
						rowToBeRemoved = rowIndex;
					}
				}
				
				tableModel.removeRow(rowToBeRemoved);
				
				updateLabels();
			}
    	};
    	
    	appManager.addAppListener(appListener);
    }
    
    private void populateTable() {
    	System.out.println("populateTable() call, installed apps: " + appManager.getInstalledApps().size());
    	
    	for (App app : appManager.getInstalledApps()) {
    		DefaultTableModel tableModel = (DefaultTableModel) appsAvailableTable.getModel();
    		
    		tableModel.addRow(new Object[]{
					app,
					app.getAppName(),
					app.getVersion(),
					app.getAuthors(),
					app.getDescription(),
					app.getStatus()
			});
    	}
    	
    	updateLabels();
    }
    
    private void updateLabels() {
    	appsInstalledCountLabel.setText(String.valueOf(appManager.getInstalledApps().size()));
    	appsAvailableCountLabel.setText(String.valueOf(appManager.getInstalledApps().size()));
    }
}
