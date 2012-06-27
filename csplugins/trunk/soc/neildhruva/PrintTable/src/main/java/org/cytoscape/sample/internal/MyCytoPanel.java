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
import org.jfree.chart.ChartPanel;

public class MyCytoPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;

    private JScrollPane jScrollPane1;
    private JTable table;
    private JCheckBox[] checkBoxArray;
    private GroupLayout layout;
    private int tableColumnCount;
    private ChartPanel myChart;
    private CytoChart cytoChart;
    
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
	public void initComponents(JTable table, JCheckBox[] checkBoxArray, int tableColumnCount){
		
		//if(this.table!=null)
		//	this.removeAll();
		if(this.getComponents().length>0)
			this.removeAll();
		
		cytoChart = new CytoChart();
		myChart = cytoChart.createChart();
		
        this.table = table;
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
		for(int i=0;i<tableColumnCount;i++) {
        	checkBoxGroupHor.addComponent(checkBoxArray[i]);
        }
        
		layout.setHorizontalGroup(
	            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(checkBoxGroupHor)
	                .addGap(16, 16, 16)
	                .addComponent(myChart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addGap(35, 35, 35))
	        );
		
        SequentialGroup checkBoxGroupVert = layout.createSequentialGroup();
        checkBoxGroupVert.addContainerGap();
        for(int i=0;i<tableColumnCount;i++){
        	checkBoxGroupVert.addComponent(checkBoxArray[i]);
        	if(!(i==(tableColumnCount-1))) {
        		checkBoxGroupVert.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
        	}
        }
        
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(myChart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(184, 184, 184))
                .addGroup(checkBoxGroupVert
                    .addContainerGap())
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
