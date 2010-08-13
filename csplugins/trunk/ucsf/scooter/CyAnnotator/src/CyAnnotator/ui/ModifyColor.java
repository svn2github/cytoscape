/*
 * ModifyColor.java
 *
 * Created on May 18, 2010, 12:37:36 AM
 */

package CyAnnotator.ui;

import CyAnnotator.annotations.TextAnnotation;
import cytoscape.Cytoscape;
import java.awt.Container;

/**
 *
 * @author Avinash Thummala
 */

//This class helps in changing the color of text

public class ModifyColor extends javax.swing.JFrame {

    TextAnnotation prevAnnotation;
   
    public ModifyColor(TextAnnotation prev) {

        //Basic Text Annotation, whose text color will be changed from this class
        this.prevAnnotation=prev;

        initComponents(this.getContentPane());
    }

    private void initComponents(Container pane) {

        jColorChooser1 = new javax.swing.JColorChooser();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setTitle("Select a Color");

        pane.setLayout(null);

        //Used Absolute Layout here as well

        pane.add(jColorChooser1);
        jColorChooser1.setBounds(0, 0, jColorChooser1.getPreferredSize().width, jColorChooser1.getPreferredSize().height);

        jButton1.setText("Apply");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pane.add(jButton1);
        jButton1.setBounds(130, 360, 70, jButton1.getPreferredSize().height);

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        pane.add(jButton2);
        jButton2.setBounds(220, 360, jButton2.getPreferredSize().width, jButton2.getPreferredSize().height);

        this.setContentPane(pane);

        this.setSize(429, 418);
        
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

        //Pressed Apply Button
        //Set the TextColor of the storedAnnotation to that obtained from the JColorChooser and then dispose the Window

        prevAnnotation.setColor(jColorChooser1.getColor());

        Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
        dispose();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {

        //Pressed the Cancel Button
        //Repaint and dispose the window

        Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
        dispose();

    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JColorChooser jColorChooser1;

}
