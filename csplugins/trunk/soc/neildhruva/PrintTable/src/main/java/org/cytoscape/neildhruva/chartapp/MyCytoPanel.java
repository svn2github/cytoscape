package org.cytoscape.neildhruva.chartapp;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class MyCytoPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;

    private JScrollPane jScrollPane1;
    private JCheckBox[] checkBoxArray;
    private GroupLayout layout;
    private int tableColumnCount;
    private ChartPanel myChart;
    private CytoChart cytoChart;
    private JComboBox jComboBox1;
    private JFreeChart chart;
    
    public MyCytoPanel() {
    	JLabel label = new JLabel("Please select/import a network");
		this.setLayout(new GridLayout());
		this.add(label);
		this.setVisible(true);
		
	}

	/**
	 * Initializes the <code>JTable</code> and the <code>JCheckBox</code>[] array to be added to this <code>JPanel</code> 
	 * 
	 * @param jtable The <code>JTable</code> to be displayed in this <code>JPanel</code>
	 * @param checkBoxArray The <code>JCheckBox</code>[] array to be displayed in this <code>JPanel</code>
	 * @param tableColumnCount The initial column count of the <code>JTable</code>
	 */
	public void initComponents(JTable table, JCheckBox[] checkBoxArray, int tableColumnCount, JComboBox jComboBox1){
		
		if(this.getComponents().length>0)
			this.removeAll();
		
		this.setBounds(0, 0, 2000, 2000);
		this.setPreferredSize(new Dimension(2000, 2000));
		
		this.checkBoxArray = checkBoxArray;
        this.tableColumnCount = tableColumnCount;
        
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
	
	public Component getComponent() {
		return this;
	}
	
	/**
	 * @return Location of the CytoPanel
	 */
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/**
	 * @return Title of the CytoPanel
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
	
	/**
	 * @return Instance of {@link CytoChart} used to get access to the current chart or create a new chart.
	 */
	public CytoChart getCytoChart() {
		return this.cytoChart;
	}
	
	/**
	 * Sets the new chart within the {@link ChartPanel}.
	 * 
	 */
	public void setChartPanel(String chartType) {
		chart = cytoChart.createChart(chartType);
		myChart.setChart(chart);
	}
}
