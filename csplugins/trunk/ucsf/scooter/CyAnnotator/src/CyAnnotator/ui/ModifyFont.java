package CyAnnotator.ui;

import CyAnnotator.annotations.TextAnnotation;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import cytoscape.Cytoscape;

//This class helps us in modifying the font of a basic TextAnnotation

public class ModifyFont extends javax.swing.JFrame {

    TextAnnotation annotation;

    public ModifyFont(TextAnnotation annotation) {

        //Basic Text Annotation, whose text color will be changed from this class
        
        this.annotation=annotation;
        initComponents(this.getContentPane());

        addListeners();
    }

    public void addListeners(){

        jList1.addListSelectionListener( new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                //A different Font Family is selected.
                //Modify the TextField above along with preview label in JPanel2

                jTextField1.setText((String)jList1.getModel().getElementAt(jList1.getSelectedIndex()));
                modifyPreviewLabel();
            }

        });

        jList2.addListSelectionListener( new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                //A different Font Style is selected.
                //Modify the TextField above along with preview label in JPanel2

                jTextField2.setText((String)jList2.getModel().getElementAt(jList2.getSelectedIndex()));
                modifyPreviewLabel();
            }

        });

        jList3.addListSelectionListener( new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                //A different Font Size is selected.
                //Modify the TextField above along with preview label in JPanel2

                jTextField3.setText((String)jList3.getModel().getElementAt(jList3.getSelectedIndex()));
                modifyPreviewLabel();
            }

        });

        jButton1.addActionListener( new ActionListener(){

            public void actionPerformed(ActionEvent evt) {

                //Apply button has been pressed
                //Modify the stored Annotation's font
                //Then repaint the whole network and dispose this window

                annotation.setFont(getNewFont());

                Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
                dispose();
            }

        });

        jButton2.addActionListener( new ActionListener(){

            public void actionPerformed(ActionEvent evt) {

                //Cancel Button has been pressed
                //Dispose this window
                dispose();
            }

        });

    }

    public Font getNewFont(){

        int fontStyle=0;
      
        if(jTextField2.getText().equals("Plain"))
            fontStyle=Font.PLAIN;

        else if(jTextField2.getText().equals("Bold"))
            fontStyle=Font.BOLD;

        else if(jTextField2.getText().equals("Italic"))
            fontStyle=Font.ITALIC;

        else if(jTextField2.getText().equals("Bold and Italic"))
            fontStyle=Font.ITALIC+Font.BOLD;

        return new Font(jTextField1.getText(), fontStyle, Integer.parseInt(jTextField3.getText()) );
    }

    public void modifyPreviewLabel(){

        preview.setFont(getNewFont());
        preview.setText(annotation.getText());
    }


    @SuppressWarnings("unchecked")
    private void initComponents(Container pane) {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        preview=new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setAlwaysOnTop(true);

        this.setTitle("Modify Font");
        pane.setLayout(null);

        //Used Absolute Layout here as well

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Font Type, Style and Size"));
        jPanel1.setLayout(null);

        jLabel1.setText("Font Type:");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(18, 33, jLabel1.getPreferredSize().width, jLabel1.getPreferredSize().height);

        //FontFamilyNames
        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        for(int i=0;i<jList1.getModel().getSize();i++){
            
            if(annotation.getFont().getFamily().equals((String)jList1.getModel().getElementAt(i))){
                jList1.setSelectedIndex(i);
                break;
            }
        }
        
        jTextField1.setEditable(false);
        jTextField1.setText((String)jList1.getModel().getElementAt(jList1.getSelectedIndex()));
        jPanel1.add(jTextField1);
        jTextField1.setBounds(18, 58, 133, jTextField1.getPreferredSize().height);

        jScrollPane1.setViewportView(jList1);
        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(18, 89, 133, jScrollPane1.getPreferredSize().height);

        jLabel2.setText("Style:");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(190, 33, jLabel2.getPreferredSize().width, jLabel2.getPreferredSize().height);

        //Font Style
        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Plain", "Bold", "Italic", "Bold and Italic" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        if(annotation.getFont().getStyle()==Font.PLAIN)
            jList2.setSelectedIndex(0);

        else if(annotation.getFont().getStyle()==Font.BOLD)
            jList2.setSelectedIndex(1);

        else if(annotation.getFont().getStyle()==Font.ITALIC) 
            jList2.setSelectedIndex(2);                    
        
        else
            jList2.setSelectedIndex(3);

        jTextField2.setEditable(false);
        jTextField2.setText((String)jList2.getModel().getElementAt(jList2.getSelectedIndex()));
        jPanel1.add(jTextField2);
        jTextField2.setBounds(190, 58, 110, jTextField2.getPreferredSize().height);

        jScrollPane2.setViewportView(jList2);
        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(190, 89, 110, jScrollPane2.getPreferredSize().height);

        jLabel3.setText("Size:");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(328, 33, jLabel3.getPreferredSize().width, jLabel3.getPreferredSize().height);

        //Font Size
        jList3.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        int fontSize=annotation.getFont().getSize();

        if(fontSize%2!=0)
            fontSize++;

        int i=0;

        for(i=0;i<jList3.getModel().getSize();i++){

            if(fontSize==Integer.parseInt((String)jList3.getModel().getElementAt(i)) ){
                jList3.setSelectedIndex(i);
                break;
            }
        }

        if(i==jList3.getModel().getSize())
            jList3.setSelectedIndex(2);

        jTextField3.setEditable(false);
        jTextField3.setText((String)jList3.getModel().getElementAt(jList3.getSelectedIndex()));
        jPanel1.add(jTextField3);
        jTextField3.setBounds(328, 58, 69, jTextField3.getPreferredSize().height);

        jScrollPane3.setViewportView(jList3);
        jPanel1.add(jScrollPane3);
        jScrollPane3.setBounds(328, 89, 69, jScrollPane3.getPreferredSize().height);

        pane.add(jPanel1);
        jPanel1.setBounds(0, 0, 420, 240);

        //JPanel2 previews the select font with it's selected properties

        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));
        
        modifyPreviewLabel();                        
        
        jPanel2.add(preview, BorderLayout.NORTH);

        pane.add(jPanel2);
        jPanel2.setBounds(0, 240, 420, 70);

        jButton1.setText("OK");
        pane.add(jButton1);
        jButton1.setBounds(127, 320, 60, jButton1.getPreferredSize().height);

        jButton2.setText("Cancel");
        pane.add(jButton2);
        jButton2.setBounds(220, 320, jButton2.getPreferredSize().width, jButton2.getPreferredSize().height);

        this.setContentPane(pane);
        this.setSize(420, 387);
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel preview;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration

}
