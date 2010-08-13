
package CyAnnotator.ui;

import CyAnnotator.annotations.TextWithShapeAnnotation;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import cytoscape.Cytoscape;

public class ModifyShape extends JFrame {

    public ModifyShape(TextWithShapeAnnotation val) {

        this.annotation=val;
        initComponents(this.getContentPane());
    }

    private void initComponents(Container pane) {

        jScrollPane1 = new JScrollPane();
        jList1 = new JList();
        jButton1 = new JButton();
        jButton2 = new JButton();
        jCheckBox1 = new JCheckBox();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Change Shape");
        setAlwaysOnTop(true);
        setResizable(false);

        pane.setLayout(null);

        jList1.setModel(new AbstractListModel() {
            String[] strings = { "Rectangle", "Rounded Rectangle", "Oval" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList1.setSelectedIndex(annotation.getShapeType());

        jScrollPane1.setViewportView(jList1);
        pane.add(jScrollPane1);
        jScrollPane1.setBounds(10, 11, 107, 55);

        jButton1.setText("OK");
        pane.add(jButton1);
        jButton1.setBounds(135, 43, jButton1.getPreferredSize().width, jButton1.getPreferredSize().height);

        jButton1.addActionListener( new ActionListener(){

            public void actionPerformed(ActionEvent e) {

                //Modify the values of shapeType and fillVal in the stored Annotation

                annotation.setShapeType(jList1.getSelectedIndex());
                annotation.setFillVal(jCheckBox1.isSelected());
                
                Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();

                ModifyShape.this.dispose();
            }

        });

        jButton2.setText("Cancel");
        pane.add(jButton2);
        jButton2.setBounds(200, 43, jButton2.getPreferredSize().width, jButton2.getPreferredSize().height);

        jButton2.addActionListener( new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                ModifyShape.this.dispose();
            }

        });

        jCheckBox1.setText("Fill Shape");
        jCheckBox1.setSelected(annotation.getFillVal());
        pane.add(jCheckBox1);
        jCheckBox1.setBounds(164, 11, jCheckBox1.getPreferredSize().width, jCheckBox1.getPreferredSize().height);

        this.setContentPane(pane);
        this.setSize(290, 110);

    }


    // Variables declaration - do not modify
    private JButton jButton1;
    private JButton jButton2;
    private JCheckBox jCheckBox1;
    private JList jList1;
    private JScrollPane jScrollPane1;
    private TextWithShapeAnnotation annotation;
    // End of variables declaration

}
