/*
 * Temp.java
 *
 * Created on Aug 9, 2010, 6:08:01 AM
 */

package CyAnnotator.ui;

import java.awt.*;

/**
 *
 * @author Avinash Thummala
 */
public class SelectColor extends javax.swing.JFrame {

    private CreateShapeAnnotation prevFrame=null;
    private int flag=0;

    public SelectColor(CreateShapeAnnotation prevFrame, int flag) {

        initComponents(this.getContentPane());

        this.prevFrame=prevFrame;
        this.flag=flag;
    }

    private void initComponents(Container pane) {

        jColorChooser1 = new javax.swing.JColorChooser();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Color");
        setAlwaysOnTop(true);
        setResizable(false);
        setMinimumSize(new Dimension(450, 430));

        pane.setLayout(null);

        pane.add(jColorChooser1);
        jColorChooser1.setBounds(0, 0, (int)jColorChooser1.getPreferredSize().getWidth(), (int)jColorChooser1.getPreferredSize().getHeight());

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pane.add(jButton1);
        jButton1.setBounds(116, 358, 67, (int)jButton1.getPreferredSize().getHeight());
        
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        pane.add(jButton2);
        jButton2.setBounds(249, 358, (int)jButton2.getPreferredSize().getWidth(), (int)jButton2.getPreferredSize().getHeight());
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

        //Depending on the value of a flag set the chosen color as either the fillColor or edgeColor

        if(flag==1)
            prevFrame.setFillColor(jColorChooser1.getColor());

        else if(flag==2)
            prevFrame.setEdgeColor(jColorChooser1.getColor());

        dispose();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {

        dispose();
    }


    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JColorChooser jColorChooser1;
    // End of variables declaration

}
