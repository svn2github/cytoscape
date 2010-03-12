package cytoscape.performance.track;

import cytoscape.*;

import java.util.regex.*;

public class TrackedEvent {

	public final long begin;
	public final long end;
	public final String signature;
	public final int level; 

	public TrackedEvent(String sig, long b, long e, int l) { 
		signature = sig;
		begin = b; 
		end = e; 
		level = l; 
	}

	public TrackedEvent(String parsableString) { 
		Pattern p = Pattern.compile("^(.+);(\\d+);(\\d+);(\\d+)$");
		Matcher m = p.matcher(parsableString);

		if ( m.matches() )  {
			signature = m.group(1);
			begin = new Long(m.group(2));
			end = new Long(m.group(3));
			level = new Integer(m.group(4));
		} else {
			signature = "failed to parse: " + parsableString; 
			begin = -1; 
			end = -1; 
			level = -1; 
		}
	}

	public String toString() {
		return signature + ": " + begin + " -> " + end + " -- " + level;
	}

	public String toParsable() {
		return signature + ";" + begin + ";" + end + ";" + level;
	}

	public boolean equals(Object o) {
		if ( o == this )
			return true;

		if ( o instanceof TrackedEvent ) {
			TrackedEvent t = (TrackedEvent)o;
			return t.signature.equals(this.signature);
		} else { 
			return false;
		}
	}
}
