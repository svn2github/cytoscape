package cytoscape.util;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import java.util.Iterator;
import java.util.Set;

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

}
