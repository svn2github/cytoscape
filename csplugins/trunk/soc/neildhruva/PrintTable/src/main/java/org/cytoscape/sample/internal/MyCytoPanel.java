package org.cytoscape.sample.internal;

import java.awt.Component;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

public class MyCytoPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;

    private JScrollPane jScrollPane1;
    private JTable table;
    private JCheckBox[] checkBoxArray;
    private GroupLayout layout;
    private int tableColumnCount;
	
    
    public MyCytoPanel() {
	
		this.setVisible(true);	
	}

	/**
	 * Initializes the <code>JTable</code> and the <code>JCheckBox</code>[] array to be added to this <code>JPanel</code> 
	 * 
	 * @param jtable The <code>JTable</code> to be displayed in this <code>JPanel</code>
	 * @param checkBoxArray The <code>JCheckBox</code>[] array to be displayed in this <code>JPanel</code>
	 * @param tableColumnCount The initial column count of the <code>JTable</code>
	 */
	public void initComponents(JTable jtable, JCheckBox[] checkBoxArray, int tableColumnCount){
		
		if(this.table!=null)
			this.removeAll();
		
        this.table = jtable;
        this.checkBoxArray = checkBoxArray;
        this.tableColumnCount = tableColumnCount;
        
        jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(table);

        layout = new GroupLayout(this);
        this.setLayout(layout);
        
        initPanel();                                    
        this.revalidate();
	}
	
	/**
	 * Adds the <code>JTable</code> and the <code>JCheckBox</code>[] array to the layout of
	 * this <code>JPanel</code>.
	 * 
	 */
	public void initPanel(){
		
		GroupLayout.ParallelGroup checkBoxGroupHor = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        for(int i=0;i<tableColumnCount;i++){
        	checkBoxGroupHor.addComponent(checkBoxArray[i]);
        }
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(checkBoxGroupHor)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE))
        );
        
        SequentialGroup checkBoxGroupVert = layout.createSequentialGroup();
        checkBoxGroupVert.addContainerGap();
        for(int i=0;i<tableColumnCount;i++){
        	checkBoxGroupVert.addComponent(checkBoxArray[i]);
        	checkBoxGroupVert.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
        }
        checkBoxGroupVert.addContainerGap(192, Short.MAX_VALUE);
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
            .addGroup(checkBoxGroupVert)
        );
	}

	
	public Component getComponent() {
		return this;
	}
	
	/**
	 * @return CytoPanelName Location of the CytoPanel
	 */
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/**
	 * @return String Title of the CytoPanel
	 */
	public String getTitle() {
		return "Table View";
	}

	/**
	 * @return Icon
	 */
	public Icon getIcon() {
		return null;
	}
}
