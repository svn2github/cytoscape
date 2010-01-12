package fr.pasteur.sysbio.rdfscape;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public abstract class AbstractModulePanel extends JPanel implements InteractivePanel{
	ImageIcon myImageIcon=null;
	protected String myTabText="";
	protected String redLightText="<html>Cannot operate</html>";
	protected String yellowLightText="<html>Ready to operate</html>";
	protected String greenLightText="<html>Can operate</html>";
	protected String myTabTooltip="";
	public String getStatusMessage() {
		if(getStatusLevel()==1) {
			return redLightText;
		}
		else if(getStatusLevel()==2) {
			return yellowLightText;
		}
		else if(getStatusLevel()==3) {
			return greenLightText;
		}
		else return "";
	}
	public ImageIcon getStatusIcon() {
		//System.out.println("Get level:"+getStatusLevel());
		if(getStatusLevel()==1) {
			return Utilities.getRedlightIcon();
		} 
		else if(getStatusLevel()==2) {
			return Utilities.getYellowlightIcon();
		}
		else if(getStatusLevel()==3) {
			return Utilities.getGreenlightIcon();
		}
		else return new ImageIcon();
		
	}
	
	public String getTabText() {
		
		return myTabText;
		
	}
	public String getTabTooltip() {
		
		return myTabTooltip;
		
	}
	
	
}
