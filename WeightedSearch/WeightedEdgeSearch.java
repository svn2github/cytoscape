import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
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

	protected static double SIZE_FACTOR = Math.log(0.6);

	protected static final int MAX_SIZE = 15;

	protected static final int MIN_SIZE = 10;

	protected static final int RANDOM_TRIALS = 100;

	protected static final double CUTOFF_PERCENT = 0.95;

	// protected static final double DEFAULT_FILL = -10;

	Map<Integer, Double> idx2Expression;

	public static void main(String[] args) {

		System.err.println("Version 0.47");
		Vector<SearchResult> searchResults = new Vector<SearchResult>();
		System.err.println("Reading edges scores");
		readEdgeScores();
		System.err.println("Reading initial node scores");
		readNodeScores(args[0]);
		System.err.println("Setting up graph data structures");
		setupGraph();
		System.err.println("Scoring original graph");
		scoreGraph();
		double lastScore = 9999999;
		double cutoff = lastScore - 1;
		int searchIteration = 0;
		while (lastScore > cutoff) {
			System.err.println("Starting real search " + searchIteration);
			SearchResult bestResult = search();
			searchResults.add(bestResult);
			outputResult(searchIteration, bestResult);
			lastScore = bestResult.score;
			if (bestResult.score < cutoff) {
				DoubleArrayList scores = new DoubleArrayList(RANDOM_TRIALS);
				System.err
						.println("Failed cutoff check, running randomization trials (total = "
								+ RANDOM_TRIALS + ")");
				for (int idx = 0; idx < RANDOM_TRIALS; idx += 1) {
					System.err.print("" + idx + " ");
					/*
					 * read in the node scores
					 */
					readNodeScores(args[0]);
					/*
					 * eliminate the scores correspond to the previous best
					 * reslts
					 */
					for (int idy = 0; idy < searchResults.size() - 1; idy += 1) {
						removeScores(searchResults.get(idy));
					}
					/*
					 * randomize the expression
					 */
					shuffleExpression();

					/*
					 * regenerat the scoring matrix
					 */
					scoreGraph();
					/*
					 * find the best result and record the score
					 */
					SearchResult randomResult = search();
					scores.add(randomResult.score);
				}
				System.err.println();
				/*
				 * Figure out hte cutoff value
				 */
				scores.sort();
				outputRandomTrials(searchIteration, scores);
				double prev_cutoff = cutoff;
				cutoff = scores.get((int) (scores.size() * CUTOFF_PERCENT - 1));
				if(cutoff > prev_cutoff){
					cutoff = prev_cutoff;
				}
				/*
				 * read hte node scores again and remove hte last n-1 node
				 * scores
				 */
				readNodeScores(args[0]);
				for (int idy = 0; idy < searchResults.size() - 1; idy += 1) {
					removeScores(searchResults.get(idy));
				}
			}
			/*
			 * remove the node scores for the previous result and regenerate hte
			 * edge score matrix
			 */
			removeScores(searchResults.get(searchResults.size() - 1));
			scoreGraph();
			searchIteration += 1;
		}
		System.err
				.println("Last result failed cutoff, even after new randomization trial");

	}

	protected static void shuffleExpression() {
		(new DoubleArrayList(idx2NodeLLR)).shuffle();
	}

	protected static double harmonicMean(double x1, double x2) {
		return 2 * x1 * x2 / (x1 + x2);
		// return (x1+x2)/2;
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

			}
		} catch (OutOfMemoryError e) {
			System.err.println("Need more memory to create edges scores array");
			System.exit(-1);
		}
		System.err.println("Finished building edge array data structure");
	}

	protected static void scoreGraph() {

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
			fw.write("Score:\t" + result.score + "\n");
			fw.write("Size Factor:\t" + SIZE_FACTOR + "\n");
			fw.write("Size:\t" + result.members.size() + "\n");
			for (Integer member : result.members) {
				fw.write(idx2Name.get(member) + "\n");
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected static SearchResult search() {
		SearchResult bestResult = null;
		for (int seed = 0; seed < edgeScores.length; seed += 1) {
			// System.err.println("Searching from seed "+seed+", best so far:
			// size ="+bestResult.members.size());
			SearchResult currentResult = new SearchResult();
			currentResult.score = 0;
			currentResult.sum = 0;
			double[] neighbors = new double[edgeScores.length];
			int bestNeighbor = seed;
			double newScore = 0;
			do {
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
				for (int neighbor = 0; neighbor < neighbors.length; neighbor += 1) {
					if (neighbor == bestNeighbor) {
						continue;
					}
					if (neighbor > bestNeighbor) {
						neighbors[neighbor] += edgeScores[neighbor][bestNeighbor];
					} else {
						neighbors[neighbor] += edgeScores[bestNeighbor][neighbor];
					}
				}

				bestNeighbor = maxEntry(neighbors);
				// int size = currentResult.members.size()+1;
				// newScore = (currentResult.sum +
				// neighbors[bestNeighbor])/Math.pow(size*(size-1)/2,0.75);
				// newScore = currentResult.sum + neighbors[bestNeighbor];
				newScore = (currentResult.sum + neighbors[bestNeighbor]);
				// }while(currentResult.members.size() < MIN_SIZE || (
				// currentResult.members.size() < MAX_SIZE && newScore >
				// currentResult.score));
			} while (currentResult.members.size() < MAX_SIZE
					&& newScore > currentResult.score);

			if (bestResult == null || currentResult.score > bestResult.score) {
				bestResult = currentResult;
			}
		}
		return bestResult;
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
}

class SearchResult {
	public SearchResult() {
		score = 0;
		sum = 0;

		members = new HashSet<Integer>();
	}

	public double score;

	public double sum;

	public Set<Integer> members;
}
