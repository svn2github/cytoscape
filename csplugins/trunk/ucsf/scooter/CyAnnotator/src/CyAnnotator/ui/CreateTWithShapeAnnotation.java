/*
 * CreateTWithShapeAnnotation.java
 *
 * Created on Jun 11, 2010, 10:12:36 AM
 */

package CyAnnotator.ui;

import CyAnnotator.annotations.TextAnnotation;
import CyAnnotator.annotations.TextWithShapeAnnotation;

import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;

import ding.view.DGraphView;

import java.awt.Container;
import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author Avinash Thummala
 */

public class CreateTWithShapeAnnotation extends JFrame {

    private Color shapeColor=Color.BLUE;
    private int x=0,y=0;

    public CreateTWithShapeAnnotation(int x, int y) {

        this.x=x;
        this.y=y;

        initComponents(this.getContentPane());
    }


    public void setShapeColor(Color newColor){
        shapeColor=newColor;
    }

    private void initComponents(Container pane) {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Annotation bounded by a shape");
        setAlwaysOnTop(true);
        setResizable(false);

        pane.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Enter Text");
        pane.add(jLabel1);
        jLabel1.setBounds(21, 22, jLabel1.getPreferredSize().width, jLabel1.getPreferredSize().height);

        jTextField1.setText("Text Annotation");
        pane.add(jTextField1);
        jTextField1.setBounds(103, 20, 167, jTextField1.getPreferredSize().height);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Rectangle", "Rounded Rectangle", "Oval" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setSelectedIndex(1);
        jScrollPane1.setViewportView(jList1);
        pane.add(jScrollPane1);
        jScrollPane1.setBounds(21, 58, 126, 58);

        jCheckBox1.setText("Fill Shape");
        pane.add(jCheckBox1);
        jCheckBox1.setBounds(215, 58, jCheckBox1.getPreferredSize().width, jCheckBox1.getPreferredSize().height);             

        jButton1.setText("Select Color");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pane.add(jButton1);
        jButton1.setBounds(197, 93, jButton1.getPreferredSize().width, jButton1.getPreferredSize().height);

        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        pane.add(jButton2);
        jButton2.setBounds(65, 135, 65, jButton2.getPreferredSize().height);

        jButton3.setText("Cancel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        pane.add(jButton3);
        jButton3.setBounds(148, 135, jButton3.getPreferredSize().width, jButton3.getPreferredSize().height);

        this.setContentPane(pane);

        this.setSize(298, 200);
        
    }    

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

        //Pressed the "Select Color" button
        //Open up a new JFrame to select a Color

        ChooseColorForShape chooseColorForShape=new ChooseColorForShape(this);
        chooseColorForShape.setVisible(true);

    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {

        //Pressed the "OK" Button
        //Create a TextWithShapeAnnotation, add it to foregroundCanvas, repaint the whole network and then finally dispose this frame

        //The attributes are x, y, Text, componentNumber, scaleFactor, shapeColor, A boolean value to indicate whether to fill or draw, shapeType

        TextWithShapeAnnotation newOne=new TextWithShapeAnnotation(x, y, jTextField1.getText(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom(), shapeColor, jCheckBox1.isSelected(),jList1.getSelectedIndex());

        ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newOne);

        Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();

        this.dispose();
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {

        //Pressed the "Cancel" button
        //Just dispose this window

        this.dispose();
    }


    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration

}
