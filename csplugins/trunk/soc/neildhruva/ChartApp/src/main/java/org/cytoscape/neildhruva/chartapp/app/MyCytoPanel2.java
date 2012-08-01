package org.cytoscape.neildhruva.chartapp.app;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

@SuppressWarnings("serial")
public class MyCytoPanel2 extends JPanel implements CytoPanelComponent {
	
    public MyCytoPanel2() {
    	JLabel label = new JLabel("Select/import a network");
    	this.setLayout(new GridLayout());
		this.add(label);
		this.setVisible(true);
	}
    
    /**
     * Resets the JPanel contained in this CytoPanel.
     * @param jpanel The new JPanel that contains the chart, checkboxes etc.
     */
    public void setJPanel(JPanel jpanel) {
    	this.removeAll();
    	JScrollPane jsp = new JScrollPane(jpanel);
    	jsp.setViewportView(jpanel);
    	this.add(jsp);
    	this.revalidate();
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
	
}
