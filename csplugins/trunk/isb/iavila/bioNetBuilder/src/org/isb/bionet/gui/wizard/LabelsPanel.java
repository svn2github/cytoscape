
package org.isb.bionet.gui.wizard;

import java.awt.*;
import javax.swing.*;
import org.isb.bionet.datasource.synonyms.*;

public class LabelsPanel extends JPanel{

    protected String [] labelOps = {SynonymsSource.GENE_NAME, SynonymsSource.PROD_NAME, SynonymsSource.ORF_NAME, SynonymsSource.GI_ID, "Source Database ID"};
    protected JComboBox labelOptions1, labelOptions2, labelOptions3, labelOptions4, labelOptions5;
   
    
    public LabelsPanel (){
        create();
    }
    
    /**
     * @return the ordered options for labeling nodes. Options are:"Gene Name", "Product Name","ORF", "GI Number", "Source Database ID" 
     */
    public String [] getOrderedLabelOptions (){
        String [] ops = new String[5];
        ops[0] = (String)this.labelOptions1.getSelectedItem();
        ops[1] = (String)this.labelOptions2.getSelectedItem();
        ops[2] = (String)this.labelOptions3.getSelectedItem();
        ops[3] = (String)this.labelOptions4.getSelectedItem();
        ops[4] = (String)this.labelOptions5.getSelectedItem();
        return ops;
    }
    
    protected void create (){
        JPanel nodeLabelPanel = createNodeLabelPanel();
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(nodeLabelPanel);
    }
    
    protected JPanel createNodeLabelPanel (){
        
        GridBagLayout gridL = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.ipadx = 5;
        JPanel panel = new JPanel();
        panel.setLayout(gridL);
        
        JLabel label1 = new JLabel("Label Priority 1:");
        labelOptions1 = new JComboBox(this.labelOps);
        labelOptions1.setMaximumRowCount(18);
        gridL.setConstraints(label1,c);
        panel.add(label1);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridL.setConstraints(labelOptions1,c);
        panel.add(labelOptions1);
        
        c.gridwidth = 1;
        JLabel label2 = new JLabel("Label Priority 2:");
        labelOptions2 = new JComboBox(this.labelOps);
        labelOptions2.setMaximumRowCount(18);
        labelOptions2.setSelectedIndex(1);
        gridL.setConstraints(label2,c);
        panel.add(label2);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridL.setConstraints(labelOptions2,c);
        panel.add(labelOptions2);
        
        c.gridwidth = 1;
        JLabel label3 = new JLabel("Label Priority 3:");
        labelOptions3 = new JComboBox(this.labelOps);
        labelOptions3.setMaximumRowCount(18);
        labelOptions3.setSelectedIndex(2);
        gridL.setConstraints(label3,c);
        panel.add(label3);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridL.setConstraints(labelOptions3,c);
        panel.add(labelOptions3);
        
        c.gridwidth = 1;
        JLabel label4 = new JLabel("Label Priority 4:");
        labelOptions4 = new JComboBox(this.labelOps);
        labelOptions4.setMaximumRowCount(18);
        labelOptions4.setSelectedIndex(3);
        gridL.setConstraints(label4,c);
        panel.add(label4);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridL.setConstraints(labelOptions4,c);
        panel.add(labelOptions4);
        
        c.gridwidth = 1;
        JLabel label5 = new JLabel("Label Priority 5:");
        labelOptions5 = new JComboBox(this.labelOps);
        labelOptions5.setMaximumRowCount(18);
        labelOptions5.setSelectedIndex(4);
        gridL.setConstraints(label5,c);
        panel.add(label5);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridL.setConstraints(labelOptions5,c);
        panel.add(labelOptions5);
        
        return panel;
    }
    
}