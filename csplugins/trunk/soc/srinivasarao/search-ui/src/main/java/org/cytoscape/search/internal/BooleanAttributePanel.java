package org.cytoscape.search.internal;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class BooleanAttributePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private String attrName = null;
	private JLabel jLabel = null;
	private JCheckBox trueButton = null;
	private JCheckBox falseButton = null;

	/**
	 * This is the default constructor
	 */
	public BooleanAttributePanel(String attrName) {
		super();
		this.attrName = attrName;
		initialize();
		
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getLabel());
		this.add(getTrueButton());
		this.add(getFalseButton());
	}

	private JLabel getLabel(){
		if(jLabel==null){
			jLabel = new JLabel();
			jLabel.setText(attrName);
			jLabel.setFont(new Font("TimesRoman",Font.BOLD,16));
		}
		return jLabel;
	}
	
	private JCheckBox getTrueButton(){
		if(trueButton==null){
			trueButton = new JCheckBox();
			trueButton.setText("True");
			trueButton.setActionCommand("True");
			trueButton.setFont(new Font("TimesRoman", Font.PLAIN, 14));
		}
		return trueButton;
	}
	
	private JCheckBox getFalseButton(){
		if(falseButton==null){
			falseButton = new JCheckBox();
			falseButton.setText("False");
			falseButton.setActionCommand("False");
			falseButton.setFont(new Font("TimesRoman",Font.PLAIN,14));
		}
		return falseButton;
	}
	
	public void getCheckedValues(){
		if(trueButton.isSelected())
			System.out.println("True Label checked");
		if(falseButton.isSelected())
			System.out.println("False Label checked");
	}
}
