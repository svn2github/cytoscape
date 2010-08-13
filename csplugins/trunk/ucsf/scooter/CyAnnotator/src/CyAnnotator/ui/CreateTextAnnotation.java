/*
 * CreateTextAnnotation.java
 *
 * Created on Apr 7, 2010, 1:28:28 AM
 */

package CyAnnotator.ui;

import CyAnnotator.annotations.TextAnnotation;

import cytoscape.Cytoscape;

import cytoscape.ding.DingNetworkView;

import ding.view.DGraphView;

import java.awt.Container;
import javax.swing.JFrame;


/**
 *
 * @author Avinash Thummala
 */

//This class creates a JFrame for setting up properties to create a Basic TextAnnotation

public class CreateTextAnnotation extends JFrame {

    public CreateTextAnnotation(int x, int y) {

        //A drop has been made at this point (x,y)

        this.x=x;
        this.y=y;

        initComponents(this.getContentPane());
    }

   
    private void initComponents(Container pane) {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField("TextAnnotation");
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Enter Text");
        setAlwaysOnTop(true);
        setResizable(false);       

        pane.setLayout(null);

        //Used AbsoluteLayout here as well

        jLabel1.setText("Annotation Text");

        pane.add(jLabel1);
        jLabel1.setBounds(20, 19, jLabel1.getPreferredSize().width, 20);

        pane.add(jTextField1);
        jTextField1.setBounds(120, 15, 150, jTextField1.getPreferredSize().height);

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pane.add(jButton1);
        jButton1.setBounds(110, 51, 60, jButton1.getPreferredSize().height);

        this.setContentPane(pane);

        this.setSize(285, 116);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

        //Create a BasicTextAnnotation using the attributes : x, y, Text, componentNumber, scaleFactor

        TextAnnotation newOne=new TextAnnotation(x,y,jTextField1.getText(),((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom());

        //Add the Annotation to the foregroundCanvas

        ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newOne);

        //Repaint the whole network and dispose this JFrame

        Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();

        dispose();
    }


    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration

    private int x=0,y=0;
}
