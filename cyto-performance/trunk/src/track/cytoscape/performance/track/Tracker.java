package cytoscape.performance;

import cytoscape.*;
import cytoscape.performance.ui.*;
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

	protected static Stack<Trackable> beginStack = new Stack<Trackable>();
	protected static List<TrackedEvent> results = new ArrayList<TrackedEvent>();
	protected static int totalDuration = 0;


	public static void track(State s, String sig) {
		Trackable t = new Trackable(s,sig);
		if ( t.state == State.BEGIN ) 
			beginStack.push(t);
		else 
			compare(beginStack.pop(),t);	

	}

	public static void compare( Trackable begin, Trackable end ) {
		if ( begin == null || end == null ||
		     ! begin.signature.equals(end.signature) )
		     	throw new RuntimeException(begin.toString() +  "  " + end.toString());
		long dur = end.timeStamp - begin.timeStamp;
		totalDuration += dur;
		results.add(new TrackedEvent(begin.signature,begin.timeStamp,end.timeStamp,beginStack.size()));
	}

	public static List<TrackedEvent> getEvents() {
		return results;
	}

	public static void dumpResults() {
		for (TrackedEvent t : results)
			System.out.println(t.toString());

		//new CyPerformanceUI(results);
		/*
		ResultPanel rp = new ResultPanel(results);
		BufferedImage bi = new BufferedImage(ResultPanel.width,ResultPanel.height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		rp.paint(g2);
		try {
		ImageIO.write(bi,"png",new File("homer.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/

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
