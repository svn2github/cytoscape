/**
 * 
 */
package cytoscape;

/**
 * Class used for passing along information about the network when the
 * network title has changed (firing an event)
 */
public class CyNetworkTitleChange {
		private Long networkId;
		private String networkName;
		
		public CyNetworkTitleChange(Long netId, String title) {
			networkId = netId;
			networkName = title;
		}
		
		public Long getNetworkIdentifier() {
			return networkId;
		}
		
		public String getNetworkTitle() {
			return networkName;
		}
		
		public String toString() {
			return networkName + ":" + networkId;
		}

	
}
