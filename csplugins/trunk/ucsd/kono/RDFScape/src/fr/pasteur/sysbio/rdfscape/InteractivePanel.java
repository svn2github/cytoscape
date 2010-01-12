package fr.pasteur.sysbio.rdfscape;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public interface InteractivePanel  {
	public int getStatusLevel();
	public String getStatusMessage();
	public JPanel getHelpPanel();
	public String getHelpLink();
	public String getPanelName();
	public ImageIcon getStatusIcon();
	public String getTabText();
	public String getTabTooltip();
	public void refresh();
}
