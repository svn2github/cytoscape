package cytoscape.tutorial12;

import gov.nih.nlm.ncbi.www.soap.eutils.einfo.EInfoRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.einfo.EInfoResult;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceSoap;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceException;

/**
 * 
 */
public class Tutorial12 extends CytoscapePlugin {

	/**
	 * create a menu item
	 */
	public Tutorial12() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial12 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial12");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			WebServiceClient<EUtilsServiceSoap> client = WebServiceClientManager.getClient("tutorial13");

			if (client == null) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Web service client tutorial13 is not found!");
				return;
			}
			
			// Case (1) Use the client stub
		    // eInfo utility returns a list of available databases
	        try
	        {
	        	EUtilsServiceSoap utils = (EUtilsServiceSoap) client.getClientStub();
	        	
	            // call NCBI EInfo utility
	            EInfoResult res = utils.run_eInfo(new EInfoRequest());
	            
	            // results output
				String message = "Available databases from NCBI\n";
	            for(int i=0; i<res.getDbList().getDbName().length; i++)
	            {
	                message += res.getDbList().getDbName(i) + "\n";
	            }
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),message);
	        }
	        catch(Exception ex) {
				System.out.println("Exception caught: " + e.toString());
	        }	        

	        // case 2: Send an event to the client and let the client do the job
	        try {
		        WebServiceClientManager.getCyWebServiceEventSupport().
	            fireCyWebServiceEvent(new CyWebServiceEvent("tutorial13", CyWebServiceEvent.WSEventType.SEARCH_DATABASE, null));	        	
	        }
	        catch (CyWebServiceException ex) {
	        	System.out.println("Failed to fire a CyWebServiceEvent in tutorial12!");
	        }
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInToolBar() {
			return false;
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInMenuBar() {
			return true;
		}
	}

}
