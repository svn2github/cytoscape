package org.cytoscape.app.internal.swing.main;

public class CheckForUpdatesPanel extends javax.swing.JPanel {

    private javax.swing.JButton installAllTable;
    private javax.swing.JButton installSelectedButton;
    private javax.swing.JLabel lastCheckForUpdatesLabel;
    private javax.swing.JLabel updateCheckTimeLabel;
    private javax.swing.JLabel updatesAvailableCountLabel;
    private javax.swing.JLabel updatesAvailableLabel;
    private javax.swing.JScrollPane updatesScrollPane;
    private javax.swing.JTable updatesTable;
	
    public CheckForUpdatesPanel() {
        initComponents();
    }

    private void initComponents() {

        updatesAvailableLabel = new javax.swing.JLabel();
        updatesAvailableCountLabel = new javax.swing.JLabel();
        installSelectedButton = new javax.swing.JButton();
        installAllTable = new javax.swing.JButton();
        updatesScrollPane = new javax.swing.JScrollPane();
        updatesTable = new javax.swing.JTable();
        lastCheckForUpdatesLabel = new javax.swing.JLabel();
        updateCheckTimeLabel = new javax.swing.JLabel();

        updatesAvailableLabel.setText("Updates available:");

        updatesAvailableCountLabel.setText("0");

        installSelectedButton.setText("Install Selected");
        installSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installSelectedButtonActionPerformed(evt);
            }
        });

        installAllTable.setText("Install All");
        installAllTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installAllTableActionPerformed(evt);
            }
        });

        updatesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "App Name", "Version", "Update Description"
            }
        ));
        updatesScrollPane.setViewportView(updatesTable);

        lastCheckForUpdatesLabel.setText("Last check for updates:");

        updateCheckTimeLabel.setText("Today, at 6:00 pm");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(updatesScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(updatesAvailableLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(updatesAvailableCountLabel)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(installSelectedButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(installAllTable))
                            .add(layout.createSequentialGroup()
                                .add(lastCheckForUpdatesLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(updateCheckTimeLabel)))
                        .add(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(updatesAvailableLabel)
                    .add(updatesAvailableCountLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lastCheckForUpdatesLabel)
                    .add(updateCheckTimeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(updatesScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(installSelectedButton)
                    .add(installAllTable))
                .addContainerGap())
        );
    }

    private void installSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void installAllTableActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }
}
