package org.cytoscape.work.utils;

import javax.swing.JButton;


@SuppressWarnings("serial")
public class myButton extends JButton{

	private Boolean selected;
	
	public void setselected(Boolean value){
		selected = value;
	}
	
	public Boolean getselected(){
		return selected;
	}
	
	
}