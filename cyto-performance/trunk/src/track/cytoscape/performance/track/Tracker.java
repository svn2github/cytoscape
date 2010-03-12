package cytoscape.performance.track;

import cytoscape.*;
import java.util.*;
import java.util.Iterator;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.GraphicsEnvironment;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;


public class Tracker {

	private static final Stack<Trackable> beginStack = new Stack<Trackable>();
	private static final List<TrackedEvent> results = new ArrayList<TrackedEvent>();

	public synchronized static void track(State s, String sig) {
		Trackable t = new Trackable(s,sig);
		if ( t.state == State.BEGIN ) 
			beginStack.push(t);
		else 
			createEvent(beginStack.pop(),t);
	}

	private synchronized static void createEvent( Trackable begin, Trackable end ) {
		if ( begin == null || end == null || !begin.signature.equals(end.signature) )
			throw new RuntimeException(begin.toString() +  "  " + end.toString());
		results.add(new TrackedEvent(begin.signature,begin.timeStamp,end.timeStamp,beginStack.size()));
	}

	public synchronized static List<TrackedEvent> getEvents() {
		return results;
	}

	public static void dumpResults() {
		for (TrackedEvent t : results)
			System.out.println(t.toString());
	}

	static class Trackable {

		State state;
		long timeStamp;
		String signature;

		Trackable(State s, String sig, long ts) { 
			state = s;
			signature = sig;
			timeStamp = ts; 
		}

		Trackable(State s, String sig) { 
			this(s,sig,System.nanoTime());
		}

		public String toString() {
			return state.toString() + " " + signature + ": " + timeStamp;
		}
	}
}
