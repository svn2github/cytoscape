package cytoscape.performance;

import cytoscape.*;

public class TrackedEvent {

	public long begin;
	public long end;
	public String signature;
	public int level; 

	public TrackedEvent(String sig, long b, long e, int l) { 
		signature = sig;
		begin = b; 
		end = e; 
		level = l; 
	}

	public String toString() {
		return signature + ": " + begin + " -> " + end + " -- " + Integer.toString(level);
	}
}
