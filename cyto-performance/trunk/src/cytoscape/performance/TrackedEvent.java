package cytoscape.performance;

import cytoscape.*;

import java.util.regex.*;

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

	public TrackedEvent(String parsableString) { 
		try {
		Pattern p = Pattern.compile("^(.+);(\\d+);(\\d+);(\\d+)$");
		Matcher m = p.matcher(parsableString);

		if ( m.matches() )  {
			signature = m.group(1);
			begin = new Long(m.group(2));
			end = new Long(m.group(3));
			level = new Integer(m.group(4));
		} else {
			System.out.println("no match");
		}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(parsableString);
		}
	}

	public String toString() {
		return signature + ": " + begin + " -> " + end + " -- " + level;
	}

	public String toParsable() {
		return signature + ";" + begin + ";" + end + ";" + level;
	}
}
