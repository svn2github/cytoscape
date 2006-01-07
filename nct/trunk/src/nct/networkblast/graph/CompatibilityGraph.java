package nct.networkblast.graph;

import java.lang.*;
import java.util.*;
import java.util.logging.Logger;

import nct.networkblast.score.ScoreModel;
import nct.graph.Graph;
import nct.graph.DistanceGraph;
import nct.graph.KPartiteGraph;
import nct.graph.Edge;
import nct.graph.basic.BasicGraph;

/**
 * This class creates a compatibility graph based on the homology
 * of proteins between two species and the interaction graphs for
 * the given species.
 */
public class CompatibilityGraph extends BasicGraph<String,Double> {

	public static double ORTHOLOGY_THRESHOLD = 0.01;

	protected double blastThreshold; 
	protected Map<String,Map<String,Double>> homologyMap; 
	protected Map<String,Map<String,String>> edgeDescMap; 
	protected List<? extends DistanceGraph<String,Double>> interactionGraphs;
	protected KPartiteGraph<String,Double,? extends DistanceGraph<String,Double>> homologyGraph;
	protected ScoreModel scoreModel;

	private static Logger log = Logger.getLogger("networkblast");

	/**
	 * Constructor.
	 * @param homologyGraph A k-partite graph where edges represent homology relations between
	 * proteins and partitions represent species/organisms.
	 * @param interactionGraphs 
	 * @param blastThreshold The threshold used to decide which homology mappings 
	 * are significant enough to include.
	 * @param scoreModel The ScoreModel used to score an edge in the graph. 
	 */
	public CompatibilityGraph(KPartiteGraph<String,Double,? extends DistanceGraph<String,Double>> homologyGraph, 
				  List<? extends DistanceGraph<String,Double>> interactionGraphs, 
				  double blastThreshold,  
				  ScoreModel scoreModel) {
		super();

		try {

		if ( homologyGraph.getNumPartitions() != 2 )
			throw new Exception("We can only handle 2 graphs at the moment");

		this.homologyGraph = homologyGraph;
		this.interactionGraphs = interactionGraphs;
		this.blastThreshold = blastThreshold;
		this.scoreModel = scoreModel;
		edgeDescMap = new HashMap<String,Map<String,String>>(); 

		createCompatGraph();

		} catch (Exception e) { e.printStackTrace(); }

		assert( weightMap.size() > 0 ) : "No edges added to graph!";

		log.info("compat graph construction finished");
		log.info("number of nodes: " + numberOfNodes());
		log.info("number of edges: " + numberOfEdges());
	}

	/**
	 * Creates the compatibility graph.  
	 */
	private void createCompatGraph() {

		List<String[]> listOfCompatibilityNodes = getCompatNodes();
		

		List<? extends DistanceGraph<String,Double>> graphs = homologyGraph.getPartitions();
		int numGraphs = graphs.size(); 

		for ( int x = 0; x < listOfCompatibilityNodes.size(); x++ ) {
			String[] nodeBase = listOfCompatibilityNodes.get(x);
			for ( int y = x+1; y < listOfCompatibilityNodes.size(); y++ ) {
				String[] nodeBranch = listOfCompatibilityNodes.get(y);

				// first do the distances
				byte[] distance = new byte[numGraphs]; 

				boolean foundOne = false;
//				boolean foundZero = false;
				for ( int z = 0; z < numGraphs; z++ ) {
					distance[z] = graphs.get(z).getDistance(nodeBase[z],nodeBranch[z]);
					if ( distance[z] == (byte)1 )
						foundOne = true;
//					if ( distance[z] == (byte)0 )
//						foundZero = true;
				}

				if ( !foundOne ) 
					continue;
//				if ( foundZero ) 
//					continue;

				// then the weights
				double edgeWeight = 0;
				for ( int z = 0; z < numGraphs; z++ ) 
					edgeWeight += scoreModel.scoreEdge(nodeBase[z],nodeBranch[z],graphs.get(z));
				if ( edgeWeight < ORTHOLOGY_THRESHOLD ) 
					continue;

				String node1 = createNode( nodeBranch );
				String node2 = createNode( nodeBase );

				StringBuffer distDesc = new StringBuffer();
				for ( int z = 0; z < numGraphs; z++ ) 
					distDesc.append( Byte.toString(distance[z] ));
				//System.out.println( "final distance " + distDesc.toString() );

				addNode(node1);
				addNode(node2);
				addEdge(node1,node2, new Double(edgeWeight), distDesc.toString());
			}
		}
	}

	// TODO move to Graph/BasicGraph
	private void addEdge(String nodeA, String nodeB, Double weight, String desc) {

		//System.out.println("attempting edge add: " + nodeA + " " + nodeB + " " + weight + " " + desc);
		if ( !super.addEdge(nodeA,nodeB,weight) ) {
			log.warning("add edge failed: " + nodeA + " " + nodeB + "  " + weight + " " + desc);
			return;
		}
		setEdgeDescription(nodeA,nodeB,desc);

		log.config("edge added: " + nodeA + " " + nodeB + " " + weight + " " + desc);
		//System.out.println("edge added: " + nodeA + " " + nodeB + " " + weight + " " + desc);
	}

	/**
	 * In general, nodes in the compatibility graph are k-cliques in the homology graph
	 * which means we "only" have to enumerate the k-cliques in the k-partite graph. For
	 * k=2, this is easy: all edges represent a 2-clique and thus a compatibility node.  
	 * For k&gt;2, the problem is (much) harder and we haven't implemented a solution yet. 
	 */
	private List<String[]> getCompatNodes() {

		List<String[]> compatNodes = new ArrayList<String[]>();

		List<? extends DistanceGraph<String,Double>> graphs = homologyGraph.getPartitions();

		// at the momement this only works for graphs of size 2
		Set<Edge<String,Double>> edges = homologyGraph.getEdges();		
		for ( Edge<String,Double> e: edges ) {
			String src = e.getSourceNode();	
			String tar = e.getTargetNode();	
			// TODO Super hacky!!!!!
			// nodes need to be listed in the order in which the graphs are listed!!!
			if ( graphs.get(0).isNode(src) ) {
				String[] s = { src, tar };
				compatNodes.add(s);
			} else {
				String[] s = { tar, src };
				compatNodes.add(s);
			}
		}
		return compatNodes;
	}
	

	private String createNode( String[] nodes ) {
		StringBuffer node1 = new StringBuffer(); 
		int numGraphs = nodes.length;
		for ( int z = 0; z < numGraphs-1; z++ ) {
			node1.append(nodes[z]);
			node1.append("|");
		}
		node1.append(nodes[numGraphs-1]);
		
		return node1.toString();
	}
}
