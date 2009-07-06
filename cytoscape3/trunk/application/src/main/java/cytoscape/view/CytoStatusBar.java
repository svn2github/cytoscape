package cytoscape.view;

import javax.swing.Icon;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

/**
 * @author Pasteur
 */
public interface CytoStatusBar
{
	public void setMessage(String message, Icon icon);
	public void addActionListener(ActionListener listener);
	public JPanel getPanel();
}
