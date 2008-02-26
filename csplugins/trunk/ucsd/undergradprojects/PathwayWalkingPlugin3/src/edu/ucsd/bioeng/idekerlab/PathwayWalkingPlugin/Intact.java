package edu.ucsd.bioeng.idekerlab.PathwayWalkingPlugin;

import static cytoscape.visual.VisualPropertyType.NODE_LABEL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import giny.model.Edge;
import giny.model.Node;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;
import edu.ucsd.bioeng.idekerlab.intactplugin.IntactClient;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.CyWebServiceEvent; 
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.layout.Tunable;
import uk.ac.ebi.intact.binarysearch.wsclient.BinarySearchServiceClient;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.Alias;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.BinaryInteraction;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.Confidence;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.CrossReference;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.InteractionDetectionMethod;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.Interactor;
import uk.ac.ebi.intact.binarysearch.wsclient.generated.SearchResult;
import cytoscape.util.ModuleProperties;
import cytoscape.util.ModulePropertiesImpl;

public class Intact extends Thread{

	private static final String CLIENT_ID = "intact";
    Object stub;
    ModuleProperties props;
    private String nodeId;
    private Node node;
    private javax.swing.JProgressBar jProgressBar1;
    private int button;
    
    public Intact(String nodeID, Node node1, javax.swing.JProgressBar jBar1, int buttonpress){
    	nodeId = nodeID;
    	node=node1;
    	jProgressBar1 = jBar1;
    	button = buttonpress;
    }
	
	//public void startSearch(String nodeId, Node node){
    public void run(){
	    
        WebServiceClientImpl try2 = (IntactClient) IntactClient.getClient();
        try{
        	setProperty();

        	
        	Object blah1 = try2.execute("findBinaryInteractions", new Class[]{String.class}, new Object[]{nodeId});

        	SearchResult result = (SearchResult) blah1;
        	  	
        	Cytoscape.firePropertyChange("SEARCH_RESULT", "uk.ac.ebi.intact.binarysearch.wsclient", new DatabaseSearchResult(result.getTotalCount(), result, WSEventType.IMPORT_NETWORK));

        	CyWebServiceEvent cyweb1 = new CyWebServiceEvent("IntAct", WSEventType.SEARCH_DATABASE, node);
        	
        	
        	System.out.println("RESULTS.GETINTERACTIONS() RETURNS...");
        	System.out.println(result.getInteractions().toString());
        	System.out.println("END OF RESULTS.GETINTERACTIONS");
        	
        	
        	search(cyweb1.getParameter().toString(), cyweb1);
        	
        	System.out.println("SEARCH RESULTS (blah1):");
        	System.out.println(blah1);
        	System.out.println("SEARCH RESULTS (cyweb1.getParameter):");
        	System.out.println(cyweb1.getParameter());
        	
        	//this will modify the current network 
        	//(which could be a newly created one)
        	if (button == 1){
        		importNetwork(blah1, Cytoscape.getCurrentNetwork());
        	}
        	if (button == 2){
        		importNetwork(blah1, null);	
        	}
        	jProgressBar1.setIndeterminate(false);
			//This will create a new network
//        	importNetwork(blah1, null);
        	
        } catch(Exception e){
        	System.out.println(e.toString());
        }
    	
    }

    
    
	private void setProperty() {
		props = new ModulePropertiesImpl("edu.ucsd.bioeng.idekerlab.intactplugin.IntactClient", "wsc");

		List<String> searchType = new ArrayList<String>();
		searchType.add("Lucene");
		searchType.add("Keyword Search");

		//		props.add(new Tunable("search_type", "The edge attribute that contains the weights",
		//                Tunable.LIST, searchType));
		props.add(new Tunable("search_mode", "Enable keyword search", Tunable.BOOLEAN,
		                      new Boolean(false)));

		props.add(new Tunable("max_interactions", "Maximum number of interactions",
		                      Tunable.INTEGER, new Integer(500)));
		props.add(new Tunable("search_depth", "Search depth", Tunable.INTEGER, new Integer(0)));
		props.add(new Tunable("select_interaction", "Import only selected interactions",
		                      Tunable.BOOLEAN, new Boolean(false)));
	}
	
	public void executeService(CyWebServiceEvent e) {
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(WSEventType.IMPORT_NETWORK)) {
				importNetwork(e.getParameter(), null);
			} else if (e.getEventType().equals(WSEventType.EXPAND_NETWORK)) {
				importNetwork(e.getParameter(), Cytoscape.getCurrentNetwork());
			} else if (e.getEventType().equals(WSEventType.SEARCH_DATABASE)) {
				
				search(e.getParameter().toString(), e);
			}
		}
	}
	
	private void search(String query, CyWebServiceEvent e) {
		if (stub == null) {
			stub = new BinarySearchServiceClient();
		}

		BinarySearchServiceClient client = (BinarySearchServiceClient) stub;
		System.out.println("=========CLASS = " + client.getClass());

		SearchResult result = client.findBinaryInteractions(query);
		if(e.getNextMove() != null) {
			Cytoscape.firePropertyChange("SEARCH_RESULT", "edu.ucsd.bioeng.idekerlab.intactplugin.IntactClient", new DatabaseSearchResult(result.getTotalCount(), result, e.getNextMove()));
		} else {
			Cytoscape.firePropertyChange("SEARCH_RESULT", "edu.ucsd.bioeng.idekerlab.intactplugin.IntactClient", new DatabaseSearchResult(result.getTotalCount(), result, WSEventType.IMPORT_NETWORK));
		}
	}
	
	//adds search result to network
	//move to merger class later
    public void importNetwork(Object searchResult, CyNetwork net) {
		try {
			if (stub == null) {
				stub = new BinarySearchServiceClient();
			}
	
			if(searchResult instanceof SearchResult == false) {
				return;
			}

			System.out.println("searchResult:");
			System.out.println(searchResult);
			System.out.println("end");
			
			BinarySearchServiceClient client = (BinarySearchServiceClient) stub;
			SearchResult result = (SearchResult) searchResult;
			List<BinaryInteraction> binaryInteractions = result.getInteractions();
			
			System.out.println("result:");
			System.out.println(result);
			System.out.println("end");
			
			System.out.println("result.getInteractions:");
			System.out.println(result.getInteractions());
			System.out.println("end");

			final Integer max = (Integer) props.get("max_interactions").getValue();
			int i = 0;
			Set<Node> nodes = new HashSet<Node>();
			Set<Edge> edges = new HashSet<Edge>();

			Node n1 = null;
			Node n2 = null;
			Edge e = null;

			List<Alias> alias1;
			List<Alias> alias2;

			String a1;
			String a2;

			Interactor a;
			Interactor b;
			CyAttributes attr = Cytoscape.getNodeAttributes();
			CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();

			List<String> nonUP = new ArrayList<String>();

			for (BinaryInteraction bin : binaryInteractions) {
				// do stuff with interactions here
				a = bin.getInteractorA();
				b = bin.getInteractorB();

				List<CrossReference> ids = a.getIdentifiers();

				for (CrossReference ref : ids) {
					System.out.println("ID A: " + ref.getDatabase() + ": " + ref.getIdentifier()
							+ ", " + ref.getText());

					if (ref.getDatabase().equals("uniprotkb") == false) {
						nonUP.add(ref.getDatabase() + ":" + ref.getIdentifier());
					}
				}

				String aID = a.getIdentifiers().get(0).getIdentifier();

				List<Alias> al = a.getAliases();

				for (Alias cr : al) {
					System.out.println("--------Alias A: " + cr.getAliasType() + ": "
							+ cr.getDbSource() + ", " + cr.getName());
				}

				List<CrossReference> altA = a.getAlternativeIdentifiers();

				for (CrossReference ref : altA) {
					attr.setAttribute(aID, "Official Symbol", ref.getIdentifier());
					System.out.println("########Alt A: " + ref.getDatabase() + ": "
							+ ref.getIdentifier() + ", " + ref.getText());

					break;
				}

				ids = b.getIdentifiers();

				for (CrossReference ref : ids) {
					System.out.println("ID B: " + ref.getDatabase() + ": " + ref.getIdentifier()
							+ ", " + ref.getText());

					if (ref.getDatabase().equals("uniprotkb") == false) {
						nonUP.add(ref.getDatabase() + ":" + ref.getIdentifier());
					}
				}

				String bID = b.getIdentifiers().get(0).getIdentifier();

				List<Alias> al2 = b.getAliases();

				for (Alias cr : al2) {
					System.out.println("--------Alias B: " + cr.getAliasType() + ": "
							+ cr.getDbSource() + ", " + cr.getName());
				}

				List<CrossReference> altB = b.getAlternativeIdentifiers();

				for (CrossReference ref : altB) {
					attr.setAttribute(bID, "Official Symbol", ref.getIdentifier());
					System.out.println("########Alt B: " + ref.getDatabase() + ": "
							+ ref.getIdentifier() + ", " + ref.getText());

					break;
				}

				n1 = Cytoscape.getCyNode(a.getIdentifiers().get(0).getIdentifier(), true);
				n2 = Cytoscape.getCyNode(b.getIdentifiers().get(0).getIdentifier(), true);

				if ((a.getOrganism() != null) && (a.getOrganism().getIdentifiers() != null)
						&& (a.getOrganism().getIdentifiers().get(0) != null)) {
					attr.setAttribute(n1.getIdentifier(), "species",
							a.getOrganism().getIdentifiers().get(0).getText());
				}

				if ((b.getOrganism() != null) && (b.getOrganism().getIdentifiers() != null)
						&& (b.getOrganism().getIdentifiers().get(0) != null)) {
					attr.setAttribute(n2.getIdentifier(), "species",
							b.getOrganism().getIdentifiers().get(0).getText());
				}

				if ((bin.getInteractorA().getAliases() != null)
						&& (bin.getInteractorA().getAliases().size() != 0)) {
					alias1 = bin.getInteractorA().getAliases();
				}

				if ((bin.getInteractorB().getAliases() != null)
						&& (bin.getInteractorB().getAliases().size() != 0)) {
					alias2 = bin.getInteractorB().getAliases();
				}

				e = Cytoscape.getCyEdge(n1, n2, "interaction",
						bin.getInteractionTypes().get(0).getText(), true);
				System.out.println("-----------> " + bin.getInteractorA() + " interacts with "
						+ bin.getInteractorB());

				nodes.add(n1);
				nodes.add(n2);
				edges.add(e);

				List<Confidence> confs = bin.getConfidenceValues();

				for (Confidence c : confs) {
					edgeAttr.setAttribute(e.getIdentifier(), "confidence", c.getValue());
				}

				List<InteractionDetectionMethod> iType = bin.getDetectionMethods();

				for (InteractionDetectionMethod it : iType) {
					edgeAttr.setAttribute(e.getIdentifier(), "interaction detection method",
							it.getText());
				}

				edgeAttr.setAttribute(e.getIdentifier(), "Source Database",
						bin.getSourceDatabases().get(0).getDatabase());

				i++;

				if (i > max) {
					break;
				}
			}

			for (String n : nonUP) {
				System.out.println("!!!!!!!!Non UP id = " + n);
			}

			if (net == null) {
				Cytoscape.createNetwork(nodes, edges, "IntAct: ", null);
				Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, null);
			} else {
				for (Node node : nodes) {
					net.addNode(node);
				}

				for (Edge edge : edges) {
					net.addEdge(edge);
				}

				net.setSelectedNodeState(nodes, true);
				Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
			}

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

}
