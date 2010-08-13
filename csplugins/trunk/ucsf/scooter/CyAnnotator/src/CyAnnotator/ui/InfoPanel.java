/*
 * InfoPanel.java
 *
 * Created on Jun 10, 2010, 3:16:26 PM
 */

package CyAnnotator.ui;

/**
 *
 * @author Avinash Thummala
 */

//This JPanel contains JLabels with important information about ways to use this CyAnnotator

public class InfoPanel extends javax.swing.JPanel {

    public InfoPanel() {
        initComponents();
    }

    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Some Information"));
        setToolTipText("Some Information");

        setLayout(null);

        //Used Absolute layout here
        //Initially I used GroupLayout offerered in Netbeans, But it wasn't part of J2SE 5.0

        jLabel1.setText("*   Drag-n-drop annotations below onto the Network to create new ones.");
        add(jLabel1);
        jLabel1.setBounds(20, 30, jLabel1.getPreferredSize().width, jLabel1.getPreferredSize().height);

        jLabel2.setText("*   Right click the Annotations to modify their properties.");
        add(jLabel2);
        jLabel2.setBounds(20, 60, jLabel2.getPreferredSize().width, jLabel2.getPreferredSize().height);

        jLabel3.setText("*   Use Mouse Wheel to Zoom in and out.");
        add(jLabel3);
        jLabel3.setBounds(20, 90, jLabel3.getPreferredSize().width, jLabel3.getPreferredSize().height);

        jLabel4.setText("*   Drag and drop a created annotation to change its location");
        add(jLabel4);
        jLabel4.setBounds(20, 120, jLabel4.getPreferredSize().width, jLabel4.getPreferredSize().height);

        jLabel5.setText("*   Select \"Add arrow\", then Drag-n-drop the annotation to point there.");
        add(jLabel5);
        jLabel5.setBounds(20, 150, jLabel5.getPreferredSize().width, jLabel5.getPreferredSize().height);

        jLabel6.setText("*   Add as many arrows from an annotation as you want.");
        add(jLabel6);
        jLabel6.setBounds(20, 180, jLabel6.getPreferredSize().width, jLabel6.getPreferredSize().height);

        jLabel7.setText("*   Double click an Annotation to select it.");
        add(jLabel7);
        jLabel7.setBounds(20, 210, jLabel7.getPreferredSize().width, jLabel7.getPreferredSize().height);

        jLabel8.setText("*   Use arrow keys to move selected annotations");
        add(jLabel8);
        jLabel8.setBounds(20, 240, jLabel8.getPreferredSize().width, jLabel8.getPreferredSize().height);

        jLabel9.setText("*   Use Mouse wheel to change sizes of selected annotations.");
        add(jLabel9);
        jLabel9.setBounds(20, 270, jLabel9.getPreferredSize().width, jLabel9.getPreferredSize().height);

        jLabel10.setText("*   Click somewhere else to deselect annotations");
        add(jLabel10);
        jLabel10.setBounds(20, 300, jLabel10.getPreferredSize().width, jLabel10.getPreferredSize().height);

        setMaximumSize(new java.awt.Dimension(390, 340));
        setMinimumSize(new java.awt.Dimension(390, 340));
        setPreferredSize(new java.awt.Dimension(390, 340));
        
    }


    // Variables declaration - do not modify
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel10;
    // End of variables declaration

}
