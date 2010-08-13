/*
 * ModifyText.java
 *
 * Created on May 18, 2010, 12:01:56 AM
 */

package CyAnnotator.ui;

import CyAnnotator.annotations.TextAnnotation;
import java.awt.event.*;
import cytoscape.Cytoscape;
import java.awt.Container;

/**
 *
 * @author Avinash Thummala
 */

//This class helps in modifying the text of Annotations

public class ModifyText extends javax.swing.JFrame{

    TextAnnotation prevAnnotation;

    public ModifyText(TextAnnotation prev) {
        this.prevAnnotation=prev;

        initComponents(getContentPane());
    }

    private void initComponents(Container pane) {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setAlwaysOnTop(true);

        setTitle("Enter new Text");

        pane.setLayout(null);

        //Used Absolute Layout here as well

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Old Text");        
        pane.add(jLabel1);
        jLabel1.setBounds(20, 11, jLabel1.getPreferredSize().width, jLabel1.getPreferredSize().height);

        jTextField1.setEditable(false);
        jTextField1.setText(prevAnnotation.getText());        
        pane.add(jTextField1);
        jTextField1.setBounds(96, 11, 158, jTextField1.getPreferredSize().height);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("New Text");        
        pane.add(jLabel2);
        jLabel2.setBounds(20, 49, jLabel2.getPreferredSize().width, jLabel2.getPreferredSize().height);

        jTextField2.setText(prevAnnotation.getText());        
        pane.add(jTextField2);
        jTextField2.setBounds(96, 49, 158, jTextField2.getPreferredSize().height);

        jButton1.setText("Apply");
        pane.add(jButton1);
        jButton1.setBounds(54, 88, jButton1.getPreferredSize().width, jButton1.getPreferredSize().height);

        jButton2.setText("Cancel");
        pane.add(jButton2);
        jButton2.setBounds(141, 88, jButton2.getPreferredSize().width, jButton2.getPreferredSize().height);

        jButton1.addActionListener( new ActionListener(){

            public void actionPerformed(ActionEvent evt){

                //Pressed Apply Button
                //Set the Text of the storedAnnotation to that obtained from the JTextField and then dispose the Window
                
               prevAnnotation.setText(jTextField2.getText());

               Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
               dispose();
            }

        });

        jButton2.addActionListener( new ActionListener(){

            public void actionPerformed(ActionEvent evt){

               //Pressed the Cancel Button
               //Repaint and dispose the window
                
               Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
               dispose();
            }

        });

        this.setContentPane(pane);
        this.setSize(280, 152);

    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration

}
