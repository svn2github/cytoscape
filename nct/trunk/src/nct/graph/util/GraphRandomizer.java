package graph.util;

import nct.graph.Graph;
import java.util.Random;

public interface GraphRandomizer {

	public <N extends Comparable<? super N>,W extends Comparable<? super W>> void randomize(Graph<N,W> g); 

}
