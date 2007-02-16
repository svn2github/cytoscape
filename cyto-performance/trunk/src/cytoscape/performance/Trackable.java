package cytoscape.performance;

import cytoscape.*;

public class Trackable {

	public State state;
	public long timeStamp;
	public String signature;

	public Trackable(State s, String sig, long ts) { 
		state = s;
		signature = sig;
		timeStamp = ts; 
	}

	public Trackable(State s, String sig) { 
		this(s,sig,System.nanoTime());
	}

	public String toString() {
		return state.toString() + " " + signature + ": " + Long.toString(timeStamp);
	}
}
