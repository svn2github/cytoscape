/*
 * PathSearchPanel.java
 *
 * Created on May 12, 2006, 11:09 AM
 */

package NetworkBLAST.panels;

import NetworkBLAST.NetworkBLASTDialog;
import NetworkBLAST.actions.PathSearch;
import NetworkBLAST.comboBoxes.NetworkComboBox;

/**
 *
 * @author  slotia
 */
public class PathSearchPanel extends javax.swing.JPanel {
    
    /** Creates new form PathSearchPanel */
    public PathSearchPanel(NetworkBLASTDialog _parentDialog) {
        parentDialog = _parentDialog;
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        graphComboBox = new NetworkComboBox();
        pathSizeTextField = new javax.swing.JTextField();
        numPathsTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        pathSizeWarningLabel = new javax.swing.JLabel();
        pathSizeWarningLabel.setVisible(false);
        numPathsWarningLabel = new javax.swing.JLabel();
        numPathsWarningLabel.setVisible(false);

        jLabel1.setText("Graph to Search:");

        jLabel2.setText("Path Size:");

        jLabel3.setText("Number of Paths:");

        pathSizeTextField.setText("4");
        pathSizeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pathSizeActionPerfomed(evt);
            }
        });
        pathSizeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                pathSizeFocusLost(evt);
            }
        });

        numPathsTextField.setText("10");
        numPathsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numPathsActionPerformed(evt);
            }
        });
        numPathsTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                numPathsFocusLost(evt);
            }
        });

        searchButton.setText("Search");
        searchButton.addActionListener(new PathSearch(parentDialog));

        jButton1.setText("Restore Defaults");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreDefaults(evt);
            }
        });

        pathSizeWarningLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        pathSizeWarningLabel.setForeground(new java.awt.Color(204, 51, 0));
        pathSizeWarningLabel.setText("Warning: Path Size must be an integer.");

        numPathsWarningLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        numPathsWarningLabel.setForeground(new java.awt.Color(204, 51, 0));
        numPathsWarningLabel.setText("Warning: Number of Paths must be an integer.");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3)
                            .add(jLabel1))
                        .add(39, 39, 39)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(pathSizeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pathSizeWarningLabel))
                            .add(graphComboBox, 0, 384, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(numPathsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(numPathsWarningLabel))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, searchButton)
                    .add(jButton1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(graphComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(pathSizeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(pathSizeWarningLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(numPathsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(numPathsWarningLabel))
                .add(45, 45, 45)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 59, Short.MAX_VALUE)
                .add(searchButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void restoreDefaults(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreDefaults
        pathSizeTextField.setText("4");
        numPathsTextField.setText("10");
        pathSizeWarningLabel.setVisible(false);
        numPathsWarningLabel.setVisible(false);
    }//GEN-LAST:event_restoreDefaults

    private void numPathsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_numPathsFocusLost
        checkNumPathsInput();
    }//GEN-LAST:event_numPathsFocusLost

    private void numPathsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numPathsActionPerformed
        checkNumPathsInput();
    }//GEN-LAST:event_numPathsActionPerformed

    private void pathSizeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pathSizeFocusLost
        checkPathSizeInput();
    }//GEN-LAST:event_pathSizeFocusLost

    private void pathSizeActionPerfomed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pathSizeActionPerfomed
        checkPathSizeInput();
    }//GEN-LAST:event_pathSizeActionPerfomed
    
    public NetworkComboBox getGraphComboBox()
    {
        return graphComboBox;
    }
    
    public javax.swing.JTextField getPathSizeTextField()
    {
        return pathSizeTextField;
    }
    
    public javax.swing.JTextField getNumPathsTextField()
    {
        return numPathsTextField;
    }
    
    public javax.swing.JButton getSearchButton()
    {
        return searchButton;
    }
    
    private void checkPathSizeInput()
    {
        try
        {
            Integer.parseInt(pathSizeTextField.getText());
        }
        catch (NumberFormatException e)
        {
            pathSizeWarningLabel.setVisible(true);
            return;
        }
        pathSizeWarningLabel.setVisible(false);
    }
    
    private void checkNumPathsInput()
    {
        try
        {
            Integer.parseInt(numPathsTextField.getText());
        }
        catch (NumberFormatException e)
        {
            numPathsWarningLabel.setVisible(true);
            return;
        }
        numPathsWarningLabel.setVisible(false);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private NetworkComboBox graphComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField numPathsTextField;
    private javax.swing.JLabel numPathsWarningLabel;
    private javax.swing.JTextField pathSizeTextField;
    private javax.swing.JLabel pathSizeWarningLabel;
    private javax.swing.JButton searchButton;
    // End of variables declaration//GEN-END:variables
    
    NetworkBLASTDialog parentDialog;
}
