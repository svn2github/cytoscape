import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import cern.colt.list.DoubleArrayList;

public class WeightedEdgeSearch {
	/*
	 * Asume we are dealing with a basic pvalue type file And also a file of
	 * tabbed triples with gene one gene two and interaction score
	 */

	protected static double[][] edgeScores;

	protected static Vector<String> idx2Name = new Vector<String>();

	protected static final int MAX_SIZE = 30;

	protected static final int MIN_SIZE = 15;
	
	protected static double INFLATE = 10;
	
	protected static double BACKGROUND = 0;


	Map<Integer, Double> idx2Expression;

	public static void main(String[] args) {

		System.err.println("Version 0.51");
		Vector<SearchResult> searchResults = new Vector<SearchResult>();
		System.err.println("Reading edges scores");
		readEdgeScores();
		System.err.println("Finished reading edges scores.\nReading node scores");
		readNodeScores(args[0]);
		setupGraph();
		scoreGraph();
		searchResults = modularSearch();
		Collections.sort(searchResults);
		pruneResults(searchResults);
		/*
		 * Sort the results
		 */
		for(int idx = 0;idx < 20; idx += 1){
			outputResult(idx, searchResults.get(idx));
		}
		
		

	}

	protected static void shuffleExpression() {
		(new DoubleArrayList(idx2NodeLLR)).shuffle();
	}

	protected static double harmonicMean(double x1, double x2) {
		//return 2 * x1 * x2 / (x1 + x2);
		return (x1+x2)/2;
	}

	static Map<String, Integer> name2Idx = new HashMap<String, Integer>();

	static Vector<String> sources = new Vector<String>();

	static Vector<String> targets = new Vector<String>();

	static Vector<Double> scores = new Vector<Double>();

	static double minEdgeScore = Double.POSITIVE_INFINITY;

	static double[] idx2NodeLLR;

	protected static void readNodeScores(String nodeAttributeFile) {
		/*
		 * Read in the llr scores for the nodes
		 */
		idx2NodeLLR = new double[name2Idx.size()];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					nodeAttributeFile));
			String line = reader.readLine();
			Vector<String> names = new Vector<String>();
			Vector<Double> LLRs = new Vector<Double>();
			double LLRsum = 0;
			while (line != null) {
				String[] splat = line.split("\t");
				/*
				 * Only consider genes that are present in the network
				 */
				if (name2Idx.containsKey(splat[0])) {
					names.add(splat[0]);
					Double LLR = new Double(splat[1]);
					LLRs.add(LLR);
					LLRsum += LLR.doubleValue();
				}
				line = reader.readLine();
			}
			reader.close();
			double LLRaverage = LLRsum / names.size();
			/*
			 * For those nodes in the graph that we don't have expression data
			 * for, just use the expected value
			 */
			Arrays.fill(idx2NodeLLR, LLRaverage);

			for (int idx = 0; idx < names.size(); idx += 1) {
				String name = names.get(idx);
				Double LLR = LLRs.get(idx);
				int nodeIndex = name2Idx.get(name);
				idx2NodeLLR[nodeIndex] = LLR.doubleValue();
			}
		} catch (Exception e) {
			System.err.println("Error when reading in node scores");
			System.err.println(e);
			System.exit(-1);
		}
	}

	protected static void readEdgeScores() {
		idx2Name = new Vector<String>();
		name2Idx = new HashMap<String, Integer>();
		sources = new Vector<String>();
		targets = new Vector<String>();
		scores = new Vector<Double>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			int idx = 0;
			int UPDATE_INTERVAL = 1000000;
			String line = reader.readLine();
			while (line != null) {
				if (idx % UPDATE_INTERVAL == 0) {
					System.err.println(idx + " interactions read");
				}
				idx += 1;
				String[] splat = line.split("\t");
				String source = splat[0];
				String target = splat[1];
				double score = Double.parseDouble(splat[2]);
				if (score < minEdgeScore) {
					minEdgeScore = score;
				}

				if (!name2Idx.containsKey(source)) {
					name2Idx.put(source, name2Idx.size());
					idx2Name.add(source);
				} else {
					source = idx2Name.get(name2Idx.get(source));
				}
				if (!name2Idx.containsKey(target)) {
					name2Idx.put(target, name2Idx.size());
					idx2Name.add(target);
				} else {
					target = idx2Name.get(name2Idx.get(target));
				}
				sources.add(source);
				targets.add(target);
				scores.add(score);
				line = reader.readLine();
			}
			System.err.println("Network has " + name2Idx.size() + " nodes");

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void setupGraph() {
		System.err.println("Min edge score is " + minEdgeScore);
		/*
		 * Build the edge scores data structure
		 */
		System.err.println("Building edge array data structure");
		try {
			edgeScores = new double[name2Idx.size()][];
			for (int idx = 0; idx < edgeScores.length; idx += 1) {
				edgeScores[idx] = new double[idx];
				Arrays.fill(edgeScores[idx],BACKGROUND);

			}
		} catch (OutOfMemoryError e) {
			System.err.println("Need more memory to create edges scores array");
			System.exit(-1);
		}
		System.err.println("Finished building edge array data structure");
	}

	protected static void scoreGraph() {


		for (int idx = 0; idx < sources.size(); idx += 1) {
			int sourceID = name2Idx.get(sources.get(idx));
			int targetID = name2Idx.get(targets.get(idx));
			if (sourceID > targetID) {
				edgeScores[sourceID][targetID] = scores.get(idx);
						
			} else {
				edgeScores[targetID][sourceID] = scores.get(idx);
		
			}
		}
	}

	protected static void removeScores(SearchResult result) {
		Vector<Integer> members = new Vector<Integer>(result.members);
		for (int idx = 0; idx < members.size(); idx += 1) {
			int member = members.get(idx);
			idx2NodeLLR[member] = Double.NEGATIVE_INFINITY;
		}
	}

	protected static void resetScores(SearchResult result) {
		Vector<Integer> members = new Vector<Integer>(result.members);
		for (int idx = 0; idx < members.size() - 1; idx += 1) {
			int source = members.get(idx);
			for (int idy = idx + 1; idy < members.size(); idy += 1) {
				int target = members.get(idy);
				if (source > target) {
					edgeScores[source][target] = Double.NEGATIVE_INFINITY;
				} else {
					edgeScores[target][source] = Double.NEGATIVE_INFINITY;
				}
			}
		}
	}

	protected static void outputRandomTrials(int id, DoubleArrayList scores) {
		try {
			FileWriter fw = new FileWriter("" + id + ".random");
			for (int idx = 0; idx < scores.size(); idx += 1) {
				fw.write("" + scores.get(idx) + "\n");
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	protected static void outputResult(int id, SearchResult result) {
		try {
			FileWriter fw = new FileWriter("" + id + ".out");
			fw.write(result.toString());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected static double modularScore(double withinEdges, double totalEdges, double edgeSum, double nodeSum, int size){
		/**/
		double modifier = 1;
		double modularity = (withinEdges/edgeSum
				- (totalEdges / edgeSum)
				* (totalEdges / edgeSum));
		if(nodeSum < 0 && modularity < 0){
			modifier = -1;
		}
		double densityWithin = withinEdges/(size*(size-1)/2);
		double densityWithout = (totalEdges-withinEdges)/(size*edgeScores.length-size);
		return INFLATE*densityWithin - densityWithout;
		/**/
		//return withinEdges;
		/* 
		return withinEdges/totalEdges;
		*/
	}
	protected static Vector<SearchResult> modularSearch() {
		Vector<SearchResult> results = new Vector<SearchResult>();
		
		/*
		 * Figure out the degree of each edge in the graph
		 */
		double [] degree = new double[edgeScores.length];
		for(int idx = 0;idx < degree.length; idx += 1){
			for(int idy = 0; idy < degree.length; idy += 1){
				if(idx > idy){
					degree[idx] += edgeScores[idx][idy];
				}
				if(idx < idy){
					degree[idx] += edgeScores[idy][idx];
				}
			}
		}
		
		/*
		 * Figure out the total weight of all edges in the network
		 */
		double edgeSum = 0;
		for(int idx = 0;idx < degree.length; idx += 1){
			edgeSum += degree[idx];
		}
		edgeSum /= 2;
		System.err.println("Edge sum: "+edgeSum);

		/*
		 * Search for modular structures from each seed in the network
		 */
		for (int seed = 0; seed < edgeScores.length; seed += 1) {
			SearchResult currentResult = new SearchResult(idx2Name);
			currentResult.members.add(seed);
			boolean [] members = new boolean[edgeScores.length];
			members[seed] = true;
			/*
			 * Initialize scoring values
			 * withinEdgeCount
			 * totalEdgeCount
			 */
			double totalEdgeCount = degree[seed];
			double withinEdgeCount = 0;
			double nodeSum = 0;
			
			double[] clusterEdges = new double[edgeScores.length];
			for(int idx = 0; idx < edgeScores.length; idx += 1){
				if(seed > idx){
					clusterEdges[idx] = edgeScores[seed][idx];
				}
				if(seed < idx){
					clusterEdges[idx] = edgeScores[idx][seed];
				}
			}
			nodeSum = idx2NodeLLR[seed];
			double newScore = Double.NEGATIVE_INFINITY;
			do{
				/*
				 * Figure out the best neighbor
				 */
				currentResult.withinEdges = withinEdgeCount;
				currentResult.totalEdges = totalEdgeCount;
				currentResult.nodeSum = nodeSum;
				currentResult.score = modularScore(withinEdgeCount, totalEdgeCount, edgeSum, nodeSum,currentResult.members.size());

				newScore = Double.NEGATIVE_INFINITY;
				int newNeighbor = -1;
				for (int idx = 0; idx < edgeScores.length; idx += 1) {
					if (!members[idx]) {
						double newWithinEdges = withinEdgeCount + clusterEdges[idx];
						double newTotalEdges = totalEdgeCount + degree[idx] - newWithinEdges;
						double newNodeSum = nodeSum + idx2NodeLLR[idx];
						int newSize = currentResult.members.size() + 1;
						
						double tempScore = modularScore(newWithinEdges,newTotalEdges,edgeSum, newNodeSum,newSize);
						if (tempScore > newScore) {
							newScore = tempScore;
							newNeighbor = idx;
						}
					}
				}
				
				/*
				 * If score increase is positive, add the best neighbor
				 */
				if(newScore > currentResult.score || currentResult.members.size() < MIN_SIZE){
					/*
					 * Add to the result set
					 */
					currentResult.members.add(newNeighbor);
					members[newNeighbor] = true;
					withinEdgeCount += clusterEdges[newNeighbor];
					totalEdgeCount  += degree[newNeighbor]-clusterEdges[newNeighbor];
					/*
					 * Update within edge count for neighbors
					 */
					for(int idx = 0;idx < clusterEdges.length; idx += 1){
						if(idx != newNeighbor){
							if(idx > newNeighbor){
								clusterEdges[idx] += edgeScores[idx][newNeighbor];
							}
							if(idx < newNeighbor){
								clusterEdges[idx] += edgeScores[newNeighbor][idx];
							}
						}
					}
				}
			/*
			 * Do this while score increase is still positive
			 */
			}while((newScore > currentResult.score || currentResult.members.size() < MIN_SIZE) && currentResult.members.size() < MAX_SIZE);
			currentResult.withinEdges = withinEdgeCount;
			currentResult.totalEdges = totalEdgeCount;
			currentResult.nodeSum = nodeSum;
			currentResult.score = modularScore(withinEdgeCount, totalEdgeCount, edgeSum, nodeSum,currentResult.members.size());
			results.add(currentResult);
		}
		return results;
	}

	protected static int maxEntry(double[] array) {
		int result = 0;
		for (int idx = 0; idx < array.length; idx += 1) {
			if (array[idx] > array[result]) {
				result = idx;
			}
		}
		return result;
	}
	
	protected static <Type> double calculateOverlap(Set<Type> one, Set<Type> two){
		double overlap = 0;
		for(Type element: one){
			if(two.contains(element)){
				overlap += 1;
			}
		}
		return overlap/(one.size()-two.size()-overlap);
		
	}
    
	protected static void pruneResults(Vector<SearchResult > results){
		double OVERLAP = 0.6;
		boolean [] repeatResults = new boolean[results.size()];
		for(int idx = 0;idx < results.size()-1;idx += 1){
			for(int idy = 0; idy < results.size(); idy += 1){
				if(calculateOverlap(results.get(idx).members,results.get(idy).members) > OVERLAP){
					repeatResults[idy] = true;
				}
			}
		}
		for(int idx = repeatResults.length - 1 ; idx >= 0; idx -= 1){
			if(repeatResults[idx]){
				results.remove(idx);
			}
		}
	}
}

class SearchResult implements Comparable{
	Vector<String> idx2Name;
	public SearchResult(Vector<String> idx2Name) {
		score = 0;
		withinEdges = 0;
		totalEdges = 0;
		nodeSum = 0;
		this.idx2Name = idx2Name;
		
		members = new HashSet<Integer>();
	}
	
	public int compareTo(Object other){
		if(((SearchResult)other).score - score < 0){
			return -1;
		}
		if(((SearchResult)other).score - score > 0){
			return 1;
		}
		return 0;
	}
	
	public String toString(){
		String result = "";
		result += "#Score:\t" + score + "\n"
		+ "#Size:\t" + members.size() + "\n"
		+ "#Within Edges:\t" + withinEdges + "\n"
		+ "#Total Edges:\t" + totalEdges + "\n"
		+ "#Node Sum:\t" + nodeSum + "\n";
		for (Integer member : members) {
			result += idx2Name.get(member) + "\n";
		}
		return result;
	}

	public double score;

	public double withinEdges;
	public double totalEdges;
	public double nodeSum;

	public Set<Integer> members;
}
