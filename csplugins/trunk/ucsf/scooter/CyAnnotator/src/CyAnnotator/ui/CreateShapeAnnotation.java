/*
 * CreateShapeAnnotation.java
 *
 * Created on Aug 9, 2010, 5:09:02 AM
 */

package CyAnnotator.ui;

import CyAnnotator.CyAnnotator;

import java.awt.Color;
import java.awt.Container;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 *
 * @author Avinash Thummala
 */
public class CreateShapeAnnotation extends JFrame {

    private Color fillColor=null, edgeColor=Color.BLACK;
    private int x=0, y=0;

    private CyAnnotator cyAnnotator;

    public CreateShapeAnnotation(int x, int y, CyAnnotator cyAnnotator) {

        initComponents(this.getContentPane());

        this.x=x;
        this.y=y;
        this.cyAnnotator=cyAnnotator;
    }

    private void initComponents(Container pane) {

        jScrollPane1 = new JScrollPane();
        jList1 = new JList();
        jCheckBox1 = new JCheckBox();
        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jButton4 = new JButton();
        jLabel1 = new JLabel();
        jComboBox1 = new JComboBox();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select a shape and its properties");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(392, 185));
        setResizable(false);
        
        pane.setLayout(null);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Rectangle", "Rounded Rectangle", "Oval"};
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });

        jList1.setSelectedIndex(0);
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jScrollPane1.setViewportView(jList1);

        pane.add(jScrollPane1);
        jScrollPane1.setBounds(18, 11, 98, 120);


        jCheckBox1.setText("Fill Color");

        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        pane.add(jCheckBox1);
        jCheckBox1.setBounds(144, 11, (int)jCheckBox1.getPreferredSize().getWidth(), (int)jCheckBox1.getPreferredSize().getHeight());
        
        jButton1.setText("Select Color");
        jButton1.setEnabled(false);

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pane.add(jButton1);
        jButton1.setBounds(260, 10, 110, (int)jButton1.getPreferredSize().getHeight());

        jButton2.setText("Edge Color");

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        pane.add(jButton2);
        jButton2.setBounds(140, 60, (int)jButton2.getPreferredSize().getWidth(), (int)jButton2.getPreferredSize().getHeight());

        jLabel1.setText("Edge Thickness");

        pane.add(jLabel1);
        jLabel1.setBounds(260, 64, (int)jLabel1.getPreferredSize().getWidth(), (int)jLabel1.getPreferredSize().getHeight());

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "3", "4", "5", "6", "7", "8" }));
        jComboBox1.setSelectedIndex(0);

        pane.add(jComboBox1);
        jComboBox1.setBounds(351, 61, (int)jComboBox1.getPreferredSize().getWidth(), (int)jComboBox1.getPreferredSize().getHeight());

        jButton3.setText("OK");

        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        pane.add(jButton3);
        jButton3.setBounds(144, 109, 85, (int)jButton3.getPreferredSize().getHeight());

        jButton4.setText("Cancel");

        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        pane.add(jButton4);
        jButton4.setBounds(260, 109, 105, (int)jButton4.getPreferredSize().getHeight());
    }

    public void setEdgeColor(Color color){

        this.edgeColor=color;
    }

    public void setFillColor(Color color){

        this.fillColor=color;
    }

    public Color getEdgeColor(){

        return edgeColor;
    }

    public Color getFillCoor(){

        return fillColor;
    }

    public int getShapeType(){

        return jList1.getSelectedIndex();
    }

    public float getEdgeThickness(){

        return Float.parseFloat((String)jComboBox1.getModel().getSelectedItem());
    }

    @Override
    public int getX(){

        return x;
    }

    @Override
    public int getY(){

        return y;
    }

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {

        if(jCheckBox1.isSelected())
            jButton1.setEnabled(true);
        
        else{
            jButton1.setEnabled(false);
            fillColor=null;
        }
    }

    private void jButton1ActionPerformed(ActionEvent evt) {

        SelectColor selectColor=new SelectColor(this, 1);

        selectColor.setVisible(true);
    }

    private void jButton2ActionPerformed(ActionEvent evt) {
        
        SelectColor selectColor=new SelectColor(this, 2);

        selectColor.setVisible(true);
    }

    private void jButton3ActionPerformed(ActionEvent evt) {

        //Set drawShape value to true in CyAnnotator
        cyAnnotator.startDrawShape(this);
       
        dispose();
    }

    private void jButton4ActionPerformed(ActionEvent evt) {

        dispose();
    }


    // Variables declaration - do not modify
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JCheckBox jCheckBox1;
    private JList jList1;
    private JScrollPane jScrollPane1;
    private JComboBox jComboBox1;
    private JLabel jLabel1;
    // End of variables declaration

}
