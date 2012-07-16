package org.cytoscape.neildhruva.chartapp;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class PanelLayout {

	private JPanel jpanel;
	private JScrollPane jScrollPane1;
    private JCheckBox[] checkBoxArray;
    private GroupLayout layout;
    private int tableColumnCount;
    private ChartPanel myChart;
    private CytoChart cytoChart;
    private JComboBox jComboBox1;
    private JFreeChart chart;
	
    public PanelLayout () {
    		this.jpanel = new JPanel();
    }
    
    
	/**
	 * Initializes the JTable and the JCheckBox[] array to be added to jpanel.
	 * @param jtable The JTable to be displayed in jpanel.
	 * @param tableColumnCount The initial column count of the JTable .
	 * @param checkBoxArray The JCheckBox[] array to be displayed in jpanel. 
	 * @param jComboBox1 Used to select the type of chart.  
	 */
	public JPanel initLayout(JTable table, int tableColumnCount, JCheckBox[] checkBoxArray, JComboBox jComboBox1){
		
		if(jpanel.getComponents().length>0)
			jpanel.removeAll();
		
		jpanel.setBounds(0, 0, 2000, 2000);
		jpanel.setPreferredSize(new Dimension(2000, 2000));
		
		this.checkBoxArray = checkBoxArray;
        this.tableColumnCount =tableColumnCount;
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		cytoChart = new CytoChart(table);
		chart = cytoChart.createChart(jComboBox1.getSelectedItem().toString());
		myChart = new ChartPanel(chart);
		myChart.setMouseWheelEnabled(true);
		
        jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(table);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane .HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        this.jComboBox1 = jComboBox1; 
        
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
		checkBoxGroupHor.addComponent(jComboBox1);
		
		layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(6, 6, 6)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(checkBoxGroupHor)
                            .addGap(81, 81, 81)
                            .addComponent(myChart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(98, 98, 98))
        );
        
        SequentialGroup checkBoxGroupVert = layout.createSequentialGroup();
        checkBoxGroupVert.addContainerGap();
        for(int i=0;i<tableColumnCount;i++){
        	checkBoxGroupVert.addComponent(checkBoxArray[i]);
        	if(i!=(tableColumnCount-1)) {
        		checkBoxGroupVert.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
        	}
        }
        
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    	.addComponent(myChart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(checkBoxGroupVert
                            .addGap(18, 18, 18)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
    }
	
	/**
	 * Sets the new chart within the {@link ChartPanel}.
	 */
	public void setChartPanel(String chartType) {
		chart = cytoChart.createChart(chartType);
		myChart.setChart(chart);
	}
}
