package org.cytoscape.neildhruva.chartapp.impl;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import org.jfree.chart.ChartPanel;

public class PanelLayout {

	private final int MAX_HEIGHT = 1800;
	private final int MAX_WIDTH = 1800;
	private final int MIN_WIDTH = 0;
	private final int MIN_HEIGHT = 0;
	
	private JPanel jpanel;
	private JCheckBox[] checkBoxArray;
    private GroupLayout layout;
    private int tableColumnCount;
    private ChartPanel myChartPanel;
    private JComboBox chartTypeComboBox;
	
    public PanelLayout () {
    		this.jpanel = new JPanel();
    }
    
    
	/**
	 * Initializes the JTable and the JCheckBox[] array to be added to jpanel.
	 * @param tableColumnCount The initial column count of the JTable .
	 * @param checkBoxArray The JCheckBox[] array to be displayed in jpanel. 
	 * @param chartTypeComboBox Used to select the type of chart.  
	 * @param myChartPanel The <code>ChartPanel</code> containing the chart.
	 */
	public JPanel initLayout(int tableColumnCount, JCheckBox[] checkBoxArray, JComboBox chartTypeComboBox,
							  ChartPanel myChartPanel){
		
		if(jpanel.getComponents().length>0)
			jpanel.removeAll();
		
		//jpanel.setBounds(0, 0, 2000, 2000);
		
		jpanel.setPreferredSize(jpanel.getLayout().minimumLayoutSize(jpanel));
		
		this.checkBoxArray = checkBoxArray;
		this.tableColumnCount = tableColumnCount;
		this.myChartPanel = myChartPanel;
		
		this.chartTypeComboBox = chartTypeComboBox; 
		
		layout = new GroupLayout(jpanel);
		jpanel.setLayout(layout);
			
		initPanel();
		
        return jpanel;
        
	}
	
	/**
	 * Adds the <code>JFreeChart</code> the <code>JCheckBox[]</code> and the <code>JComboBox</code> array to the layout of jpanel.
	 */
	public void initPanel(){
		
		GroupLayout.ParallelGroup checkBoxGroupHor = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		for(int i=0;i<tableColumnCount;i++) {
        	checkBoxGroupHor.addComponent(checkBoxArray[i]);
        }
		
        SequentialGroup checkBoxGroupVert = layout.createSequentialGroup();
        checkBoxGroupVert.addContainerGap();
        for(int i=0;i<tableColumnCount;i++){
        	checkBoxGroupVert.addComponent(checkBoxArray[i]);
        	if(i!=(tableColumnCount-1)) {
        		checkBoxGroupVert.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
        	}
        }
       
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                		.addComponent(myChartPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(checkBoxGroupHor
                        .addComponent(chartTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(443, 443, 443))
        );
        
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    	.addComponent(myChartPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(checkBoxGroupVert
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chartTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(183, Short.MAX_VALUE))
        );
    }
	
	/**
	 * When there are no plottable columns, this method is invoked.
	 * @return <code>JPanel</code> containing a warning.
	 */
	public JPanel nullJPanel() {
		
		if(jpanel.getComponents().length>0)
			jpanel.removeAll();
		
		JLabel label = new JLabel("No plottable columns. Please select/import another network");
    	jpanel.setLayout(new GridLayout());
		jpanel.add(label);
		
		return jpanel;
	}
	
	/**
	 * Gets the <code>JPanel</code> containing the chart, checkboxes, JComboBox.
	 * @return The <code>JPanel</code> containing the chart, checkboxes, JComboBox.
	 */
	public JPanel getJPanel() {
		return jpanel;
	}
	
	/**
	 * Sets the width of the <code>ChartPanel</code>.
	 * @param width The width of the <code>ChartPanel</code>.
	 */
	public void setWidth(int width) {
		if(tableColumnCount>0 && width<=MAX_WIDTH && width>=MIN_WIDTH) {
			myChartPanel.setPreferredSize(new Dimension(width, myChartPanel.getPreferredSize().height));
		}
	}
	
	/**
	 * Sets the height of the <code>ChartPanel</code>.
	 * @param height The height of the <code>ChartPanel</code>.
	 */
	public void setHeight(int height) {
		if(tableColumnCount>0 && height<=MAX_HEIGHT && height>=MIN_HEIGHT) {
			myChartPanel.setPreferredSize(new Dimension(myChartPanel.getPreferredSize().width, height));
		}
	}
}
