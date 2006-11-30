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

public class WeightedEdgeSearch {
	/*
	 * Asume we are dealing with a basic pvalue type file And also a file of
	 * tabbed triples with gene one gene two and interaction score
	 */

	protected static double[][] edgeScores;
	protected static Vector<String> idx2Name = new Vector<String>();
	protected static double SIZE_FACTOR = Math.log(0.6);
	protected static final int MAX_SIZE = 15;
	protected static final int MIN_SIZE = 10;
	protected static final int TRIALS = 2;
	//protected static final double DEFAULT_FILL = -10;
	
	Map<Integer, Double> idx2Expression;

	public static void main(String[] args) {
		boolean random = true;
		System.err.println("Version 0.36");
		System.err.println("Building graph");
		readData(args[0]);
		if (random) {
			double [] scores = new double[TRIALS];
			shuffleExpression();
			createGraph();
			for(int idx = 0;idx < TRIALS; idx += 1){
				System.err.println("Trial "+idx);
				SearchResult bestResult = search();
				scores[idx] = bestResult.score;
				shuffleExpression();
				createGraph();
			}
			outputTrials(scores);
			
		} else {
			createGraph();
			System.err.println("Finished building graph");
			System.err.println("Starting search");

			int trials = 20;
			for (int idx = 0; idx < trials; idx += 1) {
				System.err.println("Starting iteration " + idx);
				SearchResult bestResult = search();
				outputResult(idx, bestResult);

				System.err.println("Resetting edge scores");
				resetScores(bestResult);
				System.err.println("Finished resetting edge scores");
			}
		}
		
		
	}
	
	protected static void shuffleExpression(){
		Collections.shuffle(Arrays.asList(idx2NodeLLR));
	}
	
	protected static double harmonicMean(double x1, double x2){
		return 2*x1*x2/(x1+x2);
		//return (x1+x2)/2;
	}
	
	static Map<String, Integer> name2Idx = new HashMap<String, Integer>();
	static Vector<String> sources = new Vector<String>();
	static Vector<String> targets = new Vector<String>();
	static Vector<Double> scores = new Vector<Double>();
	static double minEdgeScore = Double.POSITIVE_INFINITY;
	static double [] idx2NodeLLR;

	
	protected static void readData(String nodeAttributeFile) {
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

		System.err.println("Min edge score is " + minEdgeScore);
		/*
		 * Read in the llr scores for the nodes
		 */
		idx2NodeLLR = new double[name2Idx.size()];
		System.err.println("Reading in node scores");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					nodeAttributeFile));
			String line = reader.readLine();
			Vector<String> names = new Vector<String>();
			Vector<Double> LLRs = new Vector<Double>();
			double LLRsum = 0;
			while (line != null) {
				String[] splat = line.split("\t");
				names.add(splat[0]);
				Double LLR = new Double(splat[1]);
				LLRs.add(LLR);
				LLRsum += LLR.doubleValue();
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
				if (name2Idx.containsKey(name)) {
					int nodeIndex = name2Idx.get(name);
					idx2NodeLLR[nodeIndex] = LLR.doubleValue();
				}
			}
		} catch (Exception e) {
			System.err.println("Error when reading in node scores");
			System.err.println(e);
			System.exit(-1);
		}
		System.err.println("Finished reading in node scores");

		/*
		 * Build the edge scores data structure
		 */
		System.err.println("Building edge array data structure");
		try {
			edgeScores = new double[name2Idx.size()][];
			for (int idx = 0; idx < edgeScores.length; idx += 1) {
				edgeScores[idx] = new double[idx];

			}
		} catch (OutOfMemoryError e) {
			System.err.println("Need more memory to create edges scores array");
			System.exit(-1);
		}
		System.err.println("Finished building edge array data structure");
	}

	protected static void createGraph() {
		System.err.println("Writing scores into data structure");
		System.err.println("Filling background scores");
		try {
			for (int idx = 0; idx < edgeScores.length; idx += 1) {
				/*
				 * Fill in a default value for all edges based on the min score,
				 * this may be overwritten later with a higher score
				 */
				double sourceScore = idx2NodeLLR[idx];
				for (int idy = 0; idy < edgeScores[idx].length; idy += 1) {
					double targetScore = idx2NodeLLR[idy];
					double averageNodeScore = harmonicMean(sourceScore,
							targetScore);
					edgeScores[idx][idy] = Math.log(harmonicMean(minEdgeScore,
							averageNodeScore))
							- SIZE_FACTOR;
				}
			}
		} catch (OutOfMemoryError e) {
			System.err.println("Need more memory to create edges scores array");
			System.exit(-1);
		}
		for (int idx = 0; idx < sources.size(); idx += 1) {
			int sourceID = name2Idx.get(sources.get(idx));
			int targetID = name2Idx.get(targets.get(idx));
			double averageNodeScore = harmonicMean(idx2NodeLLR[sourceID],
					idx2NodeLLR[targetID]);
			if (sourceID > targetID) {
				edgeScores[sourceID][targetID] = Math.log(harmonicMean(
						averageNodeScore, scores.get(idx)))
						- SIZE_FACTOR;
			} else {
				edgeScores[targetID][sourceID] = Math.log(harmonicMean(
						averageNodeScore, scores.get(idx)))
						- SIZE_FACTOR;
			}
		}
		System.err.println("Finished writing scores into data structure");

	}

	protected static void resetScores(SearchResult result){
		Vector<Integer> members = new Vector<Integer>(result.members);
		for(int idx = 0;idx < members.size() - 1; idx +=1 ){
			int source = members.get(idx);
			for(int idy = idx + 1; idy < members.size() ; idy += 1){
				int target = members.get(idy);
				if(source > target){
					edgeScores[source][target] = Double.NEGATIVE_INFINITY;
				}
				else{
					edgeScores[target][source] = Double.NEGATIVE_INFINITY;
				}
			}
		}
	}
	
	protected static void outputTrials(double [] scores){
		try {
			FileWriter fw = new FileWriter("random.scores");
			for(int idx = 0;idx<scores.length;idx += 1){
				fw.write(""+scores[idx]+"\n");
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
			fw.write("Score:\t"+result.score+"\n");
			fw.write("Size Factor:\t"+SIZE_FACTOR+"\n");
			fw.write("Size:\t"+result.members.size()+"\n");                                                          
			for (Integer member : result.members) {
				fw.write(idx2Name.get(member) + "\n");
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	protected static SearchResult search(){
		SearchResult bestResult = null;
		for(int seed = 0;seed < edgeScores.length; seed += 1){
			//System.err.println("Searching from seed "+seed+", best so far: size ="+bestResult.members.size());
			SearchResult currentResult = new SearchResult();
			currentResult.score = 0;
			currentResult.sum = 0;
			double [] neighbors = new double[edgeScores.length];
			int bestNeighbor = seed;
			double newScore = 0;
			do{
				currentResult.score = newScore;
				currentResult.sum += neighbors[bestNeighbor];
				/*
				 * Add the current neighbor to result
				 */
				currentResult.members.add(bestNeighbor);
				neighbors[bestNeighbor] = Double.NEGATIVE_INFINITY;
				
				/*
				 * Update the neighbors array
				 */
				for(int neighbor=0;neighbor < neighbors.length;neighbor += 1){
					if(neighbor == bestNeighbor){
						continue;
					}
					if(neighbor > bestNeighbor){
						neighbors[neighbor] += edgeScores[neighbor][bestNeighbor];
					}
					else{
						neighbors[neighbor] += edgeScores[bestNeighbor][neighbor];
					}
				}
				
				bestNeighbor = maxEntry(neighbors);
				//int size = currentResult.members.size()+1;
				//newScore = (currentResult.sum + neighbors[bestNeighbor])/Math.pow(size*(size-1)/2,0.75);
				//newScore = currentResult.sum + neighbors[bestNeighbor];
				newScore = (currentResult.sum + neighbors[bestNeighbor]);
		//	}while(currentResult.members.size() < MIN_SIZE || ( currentResult.members.size() < MAX_SIZE && newScore > currentResult.score));
			}while( currentResult.members.size() < MAX_SIZE && newScore > currentResult.score);
					
			if(bestResult == null || currentResult.score > bestResult.score){
				bestResult = currentResult;
			}
		}
		return bestResult;
	}

	protected static int maxEntry(double [] array){
		int result = 0;
		for(int idx = 0 ;idx < array.length ; idx += 1){
			if(array[idx] > array[result]){
				result = idx;
			}
		}
		return result;
	}
}


class SearchResult{
	public SearchResult(){
		score = 0;
		sum = 0;
		
		members = new HashSet<Integer>();
	}

	public double score;
	public double sum;
	public Set<Integer> members;
}