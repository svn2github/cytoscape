package Utils;

import javax.swing.JButton;

public class myButton extends JButton{

	private Boolean selected;
	
	public void setselected(Boolean value){
		selected = value;
	}
	
	public Boolean getselected(){
		return selected;
	}
	
	
}