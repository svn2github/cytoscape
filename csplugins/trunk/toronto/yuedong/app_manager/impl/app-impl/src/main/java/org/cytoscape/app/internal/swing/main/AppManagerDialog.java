package org.cytoscape.app.internal.swing.main;

import org.cytoscape.app.internal.manager.AppManager;


public class AppManagerDialog extends javax.swing.JDialog {

    private CheckForUpdatesPanel checkForUpdatesPanel1;
    private CurrentlyInstalledAppsPanel currentlyInstalledAppsPanel1;
    private InstallNewAppsPanel installNewAppsPanel1;
    private javax.swing.JTabbedPane mainTabbedPane;
    
    private AppManager appManager;
	
    public AppManagerDialog(AppManager appManager, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.appManager = appManager;
        
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        
        System.out.println("parent: " + parent);
    }
   

    private void initComponents() {

        mainTabbedPane = new javax.swing.JTabbedPane();
        installNewAppsPanel1 = new InstallNewAppsPanel();
        currentlyInstalledAppsPanel1 = new CurrentlyInstalledAppsPanel();
        checkForUpdatesPanel1 = new CheckForUpdatesPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainTabbedPane.addTab("Install New Apps", installNewAppsPanel1);
        mainTabbedPane.addTab("Currently Installed", currentlyInstalledAppsPanel1);
        mainTabbedPane.addTab("Check for Updates", checkForUpdatesPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(mainTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 554, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(mainTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 388, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }
}
