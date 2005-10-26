package exesto;

import cytoscape.data.CyAttributesImpl;
import cytoscape.data.CyAttributes;
import cytoscape.CyNetwork;
import java.util.Map;
import java.util.HashMap;

/**
 * This class provides a network-specific CyAttributes object until such time as the Cytoscape Core gets around to providing the same service.
 */

public abstract class NetworkAttributes {
  
  static Map edge_netToAtt = new HashMap();
  static Map node_netToAtt = new HashMap();

  public static CyAttributes getNodeAttributes ( CyNetwork net ) {
    CyAttributes ca;
    if ( node_netToAtt.get( net ) == null ) {
      ca = new CyAttributesImpl();
      node_netToAtt.put( net, ca );
    } else {
      ca = ( CyAttributes )node_netToAtt.get( null );
    } 
    return ca;
  }
  
  public static CyAttributes getEdgeAttributes ( CyNetwork net ) {
    CyAttributes ca;
    if ( edge_netToAtt.get( net ) == null ) {
      ca = new CyAttributesImpl();
      edge_netToAtt.put( net, ca );
    } else {
      ca = ( CyAttributes )edge_netToAtt.get( null );
    } 
    return ca;
  }


}