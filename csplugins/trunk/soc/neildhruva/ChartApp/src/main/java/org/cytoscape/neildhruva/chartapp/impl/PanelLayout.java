package org.cytoscape.neildhruva.chartapp.impl;

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

	private JPanel jpanel;
	private JCheckBox[] checkBoxArray;
    private GroupLayout layout;
    private ChartPanel myChartPanel;
    private JComboBox chartTypeComboBox;
	private int checkBoxCount;
	
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
	public JPanel initLayout(int checkBoxCount, JCheckBox[] checkBoxArray, 
							 JComboBox chartTypeComboBox, ChartPanel myChartPanel) {
		
		if(jpanel.getComponents().length>0)
			jpanel.removeAll();
		
		//jpanel.setBounds(0, 0, 2000, 2000);
		
		jpanel.setPreferredSize(jpanel.getLayout().minimumLayoutSize(jpanel));
		
		this.checkBoxArray = checkBoxArray;
		this.checkBoxCount = checkBoxCount;
		this.myChartPanel = myChartPanel;
		
		this.chartTypeComboBox = chartTypeComboBox; 
		
		layout = new GroupLayout(jpanel);
		jpanel.setLayout(layout);
			
		initPanel();
		System.out.println(chartTypeComboBox.getSelectedItem());
		jpanel.setName("NOT NULL");
        return jpanel;
        
	}
	
	/**
	 * Adds the <code>JFreeChart</code> the <code>JCheckBox[]</code> and the <code>JComboBox</code> array to the layout of jpanel.
	 */
	public void initPanel(){
		
		GroupLayout.ParallelGroup checkBoxGroupHor = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		for(int i=0;i<checkBoxCount;i++) {
        	checkBoxGroupHor.addComponent(checkBoxArray[i]);
        }
		
        SequentialGroup checkBoxGroupVert = layout.createSequentialGroup();
        checkBoxGroupVert.addContainerGap();
        for(int i=0;i<checkBoxCount;i++){
        	checkBoxGroupVert.addComponent(checkBoxArray[i]);
        	if(i!=(checkBoxCount-1)) {
        		checkBoxGroupVert.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
        	}
        }
       
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent(myChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 520, (Short.MAX_VALUE-10))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(checkBoxGroupHor
                        .addComponent(chartTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        ))
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkBoxGroupVert
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chartTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(183, Short.MAX_VALUE))
            .addComponent(myChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 311, (Short.MAX_VALUE-10))
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
		jpanel.setName("NULL");
		return jpanel;
	}
	
	/**
	 * Gets the <code>JPanel</code> containing the chart, checkboxes, JComboBox.
	 * @return The <code>JPanel</code> containing the chart, checkboxes, JComboBox.
	 */
	public JPanel getJPanel() {
		return jpanel;
	}
	
}
