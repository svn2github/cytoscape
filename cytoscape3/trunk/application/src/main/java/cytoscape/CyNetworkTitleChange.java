/**
 * 
 */
package cytoscape;

/**
 * Class used for passing along information about the network when the
 * network title has changed (firing an event)
 */
public class CyNetworkTitleChange {
		private String networkId;
		private String networkName;
		
		public CyNetworkTitleChange(String netId, String title) {
			networkId = netId;
			networkName = title;
		}
		
		public String getNetworkIdentifier() {
			return networkId;
		}
		
		public String getNetworkTitle() {
			return networkName;
		}

	
}
