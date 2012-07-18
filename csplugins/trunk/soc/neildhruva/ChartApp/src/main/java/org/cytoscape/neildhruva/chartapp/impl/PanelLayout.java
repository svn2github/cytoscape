package org.cytoscape.neildhruva.chartapp.impl;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class PanelLayout {

	private JPanel jpanel;
	private JScrollPane jScrollPane1;
    private JCheckBox[] checkBoxArray;
    private GroupLayout layout;
    private int tableColumnCount;
    private ChartPanel myChartPanel;
    private CytoChart cytoChart;
    private JComboBox chartTypeComboBox;
    private JFreeChart chart;
	
    public PanelLayout () {
    		this.jpanel = new JPanel();
    }
    
    
	/**
	 * Initializes the JTable and the JCheckBox[] array to be added to jpanel.
	 * @param jtable The JTable to be displayed in jpanel.
	 * @param tableColumnCount The initial column count of the JTable .
	 * @param checkBoxArray The JCheckBox[] array to be displayed in jpanel. 
	 * @param chartTypeComboBox Used to select the type of chart.  
	 */
	public JPanel initLayout(JTable table, int tableColumnCount, JCheckBox[] checkBoxArray, JComboBox chartTypeComboBox,
							  ChartPanel myChartPanel){
		
		if(jpanel.getComponents().length>0)
			jpanel.removeAll();
		
		jpanel.setBounds(0, 0, 2000, 2000);
		jpanel.setPreferredSize(new Dimension(2000, 2000));
		
		this.checkBoxArray = checkBoxArray;
		this.tableColumnCount = tableColumnCount;
		this.myChartPanel = myChartPanel;
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(table);
		jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane .HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		this.chartTypeComboBox = chartTypeComboBox; 
		
		layout = new GroupLayout(jpanel);
		jpanel.setLayout(layout);
			
		initPanel();
			
        return jpanel;
        
	}
	
	/**
	 * Adds the JTable and the JCheckBox[] array to the layout of jpanel.
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
	 * Sets the new chart within the {@link ChartPanel}.
	 */
	public void refreshChartPanel(String chartType) {
		chart = cytoChart.createChart(chartType);
		myChartPanel.setChart(chart);
	}
}
