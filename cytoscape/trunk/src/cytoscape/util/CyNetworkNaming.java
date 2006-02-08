package cytoscape.util;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import java.awt.Component;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

public class CyNetworkNaming
{

  public static String getSuggestedSubnetworkTitle(CyNetwork parentNetwork)
  {
    for (int i = 0; true; i++) {
      String nameCandidate =
        parentNetwork.getTitle() + "->child" + ((i == 0) ? "" : ("." + i));
      if (!isNetworkTitleTaken(nameCandidate)) return nameCandidate; }
  }

  public static String getSuggestedNetworkTitle(String desiredTitle)
  {
    for (int i = 0; true; i++) {
      String titleCandidate = desiredTitle + ((i == 0) ? "" : ("." + i));
      if (!isNetworkTitleTaken(titleCandidate)) return titleCandidate; }
  }

  private static boolean isNetworkTitleTaken(String titleCandidate)
  {
    Set existingNetworks = Cytoscape.getNetworkSet();
    Iterator iter = existingNetworks.iterator();
    while (iter.hasNext()) {
      CyNetwork existingNetwork = (CyNetwork) iter.next();
      if (existingNetwork.getTitle().equals(titleCandidate))
        return true; }
    return false;
  }
  
  /**
   * This will prompt the user to edit the title of a given CyNetork,
   * and after ensuring that the network title is not already in use,
   * this will assign that title to the given CyNetwork 
   * @para network is the CyNetwork whose title is to be changed 
   */
  public static void editNetworkTitle (CyNetwork network) {
		Component parent = Cytoscape.getDesktop();
		String pname = network.getTitle();
		String name = null;
		String sname = "";
		Object[] options = {"Try Again", "Cancel", "Use Suggestion"};
		int value = JOptionPane.NO_OPTION;
		
		while (true) {
			name = JOptionPane.showInputDialog(parent,
					"Please enter new network title: ", "Edit Network Title",
							JOptionPane.QUESTION_MESSAGE);
			if (name == pname)
				break;
			else if (name == null)
			{
				name = pname;
				break;
			}
			else if (isNetworkTitleTaken(name)) { 
				sname = getSuggestedNetworkTitle(name);
				value = JOptionPane.showOptionDialog(parent, 
					"That network title already exists, try again or use \""
					+ sname	+"\" instead: ", "Duplicate Network Title", 
					JOptionPane.WARNING_MESSAGE,
					JOptionPane.YES_NO_CANCEL_OPTION,
					null, options,
					options[2]);
			
				if (value == JOptionPane.NO_OPTION) {
					name = pname;
					break;
				}
				else if (value == JOptionPane.CANCEL_OPTION ){
					name = sname;
					break;
				}
			}
			else
				break;
		}
		network.setTitle(name);
	  }
}
