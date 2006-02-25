package nct.graph.util;

import nct.graph.Graph;
import java.util.Random;

public interface GraphRandomizer<N extends Comparable<? super N>,W extends Comparable<? super W>> {

	public void randomize(Graph<N,W> g); 

}
