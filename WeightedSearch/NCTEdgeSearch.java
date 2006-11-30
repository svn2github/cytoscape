import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import nct.graph.Edge;
import nct.graph.WeightedNode;
import nct.graph.basic.BasicGraph;
import nct.graph.basic.BasicWeightedNode;
import nct.networkblast.score.JointLogLikelihoodScoreModel;

public class NCTEdgeSearch {
	/*
	 * Asume we are dealing with a basic pvalue type file And also a file of
	 * tabbed triples with gene one gene two and interaction score
	 */
	protected static BasicGraph<WeightedNode<String,Double>, Double> createGraph(String nodeProbabilities) {

		/*
		 * First create a map from node IDs to probability values
		 */
		BasicGraph<WeightedNode<String,Double>, Double> result = new BasicGraph<WeightedNode<String, Double>,Double>();
		HashMap<String,Double> nodeMap = new HashMap<String,Double>();

		double sum = 0;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(nodeProbabilities));
			String line = reader.readLine();
			while(line != null){
				String[] splat = line.split("\t");
				String nodeName = splat[0];
				Double nodeProbability = Double.parseDouble(splat[1]);
				if(!nodeMap.containsKey(nodeName)){
					nodeMap.put(nodeName,nodeProbability);
				}
				sum += nodeProbability;
				line = reader.readLine();
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
		double averageProbability = sum/nodeMap.size();
		HashMap<String,WeightedNode<String,Double>> name2Node = new HashMap<String,WeightedNode<String,Double>>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			int UPDATE_INTERVAL = 100000;
			int idx = 0;
			String line = reader.readLine();
			while (line != null) {
				if (idx % UPDATE_INTERVAL == 0) {
					System.err.println(idx + " interactions read");
				}
				idx += 1;
				String[] splat = line.split("\t");
				
				String source = splat[0];
				String target = splat[1];
				WeightedNode<String,Double> sourceNode = null;
				WeightedNode<String,Double> targetNode = null;
				if(name2Node.containsKey(source)){
					sourceNode = name2Node.get(source);
				}else{
					if(nodeMap.containsKey(source)){
						sourceNode = new BasicWeightedNode<String,Double>(source,nodeMap.get(source));
					}else{
						sourceNode = new BasicWeightedNode<String,Double>(source,averageProbability);
					}
					name2Node.put(source,sourceNode);
					result.addNode(sourceNode);
				}
				if(name2Node.containsKey(target)){
					targetNode = name2Node.get(target);
				}else{
					if(nodeMap.containsKey(target)){
						targetNode = new BasicWeightedNode<String,Double>(target,nodeMap.get(target));
					}else{
						targetNode = new BasicWeightedNode<String,Double>(target,averageProbability);
					}
					name2Node.put(target,targetNode);
					result.addNode(targetNode);
				}
				
				result.addEdge(sourceNode, targetNode, new Double(splat[2]), "");
				line = reader.readLine();
			}

			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return result;
	}

	public static void main(String[] args) {
		System.err.println("Building graph");
		BasicGraph<WeightedNode<String,Double>, Double> graph = createGraph(args[0]);
		System.err.println("Finished building graph");

		//SimpleEdgeScoreModel<String> model = new SimpleEdgeScoreModel<String>();
		//LogLikelihoodScoreModel<String> model = new LogLikelihoodScoreModel<String>(1,0.5,0.001);;
		JointLogLikelihoodScoreModel<WeightedNode<String,Double>> model = new JointLogLikelihoodScoreModel<WeightedNode<String,Double>>(1,0.5,0.5);
		
		/*
		 * First, let's just iterate through the nodes and get their scores
		 */
		for(Edge<WeightedNode<String,Double>,Double> edge : graph.getEdges()){
			System.err.println(model.scoreEdge(edge.getSourceNode(),edge.getTargetNode(),graph));
		}
		
		
//		NewComplexSearch<String> search = new NewComplexSearch<String>(0, 10);
//		
//		for (int iteration = 0; iteration < 20; iteration += 1) {
//
//			System.err.println("Starting search " + iteration);
//			List<Graph<String, Double>> result = search.searchGraph(graph,
//					model);
//			System.err.println("Finished search " + iteration);
//
//
//			try {
//				FileWriter fw = new FileWriter("" + iteration + ".ea");
//				double maxScore = Double.NEGATIVE_INFINITY;
//				Graph<String, Double> maxGraph = null;
//				System.err.println(result.size());
//				for (Graph<String, Double> resultGraph : result) {
//					if (resultGraph.getScore() > maxScore) {
//						maxGraph = resultGraph;
//					}
//				}
//				System.err.println(maxGraph);
//				for (Edge<String, Double> edge : maxGraph.getEdges()) {
//					fw.write(edge.getSourceNode() + " (fn) "
//							+ edge.getTargetNode() + " = " + edge.getWeight()
//							+ "\n");
//					graph
//							.removeEdge(edge.getSourceNode(), edge
//									.getTargetNode());
//				}
//				fw.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(-1);
//			}
//		}
	}
}
