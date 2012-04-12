package org.cytoscape.app.internal.ui;

public class InstallAppsPanel extends javax.swing.JPanel {

    private javax.swing.JButton installFromFileButton;
    private javax.swing.JButton installFromURLButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
	
    /**
     * Creates new form InstallAppsPanel
     */
    public InstallAppsPanel() {
        initComponents();
    }

    private void initComponents() {

        installFromFileButton = new javax.swing.JButton();
        installFromURLButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        installFromFileButton.setText("Select File(s)");
        installFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installFromFileButtonActionPerformed(evt);
            }
        });

        installFromURLButton.setText("Select URL");
        installFromURLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installFromURLButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Install from File:");

        jLabel2.setText("Install from URL:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(installFromURLButton)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(installFromFileButton)
                            .add(jLabel2))
                        .addContainerGap(417, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installFromFileButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installFromURLButton)
                .addContainerGap(242, Short.MAX_VALUE))
        );
    }

    private void installFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void installFromURLButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }
}
