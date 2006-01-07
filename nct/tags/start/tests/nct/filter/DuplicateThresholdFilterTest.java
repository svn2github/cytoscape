package nct.filter;

import junit.framework.*;
import java.util.*;
import java.util.logging.Level;
import nct.graph.basic.BasicGraph;
import nct.graph.Graph;
import nct.networkblast.NetworkBlast;

public class DuplicateThresholdFilterTest extends TestCase {
    BasicGraph<String,Double> a,b,c,d;
    Filter f;
    protected void setUp() {
	NetworkBlast.setUpLogging(Level.WARNING);       
	a = new BasicGraph<String,Double>();
	b = new BasicGraph<String,Double>();
	c = new BasicGraph<String,Double>();
	d = new BasicGraph<String,Double>();
    }
    public void testfilter() {
	assertTrue(a.addNode("a"));
	assertTrue(a.addNode("b"));
	assertTrue(a.addNode("c"));
	assertTrue(a.addNode("d"));
	assertTrue(a.addNode("e"));
	assertTrue(a.addNode("f"));
	assertTrue(a.addNode("g"));
	assertTrue(a.addNode("h"));
	assertTrue(a.addNode("i"));
	assertTrue(a.addNode("j"));

	assertTrue(b.addNode("a"));
	assertTrue(b.addNode("b"));
	assertTrue(b.addNode("c"));
	assertTrue(b.addNode("d"));
	assertTrue(b.addNode("e"));
	assertTrue(b.addNode("f"));
	assertTrue(b.addNode("g"));
	assertTrue(b.addNode("h"));
	assertTrue(b.addNode("i"));
	assertTrue(b.addNode("j"));

	assertTrue(c.addNode("b"));
	assertTrue(c.addNode("c"));
	assertTrue(c.addNode("d"));
	assertTrue(c.addNode("e"));
	assertTrue(c.addNode("f"));
	assertTrue(c.addNode("g"));
	assertTrue(c.addNode("h"));
	assertTrue(c.addNode("i"));
	assertTrue(c.addNode("j"));

	assertTrue(d.addNode("a"));
	assertTrue(d.addNode("b"));
	assertTrue(d.addNode("c"));
	assertTrue(d.addNode("d"));
	assertTrue(d.addNode("e"));
	assertTrue(d.addNode("f"));
	assertTrue(d.addNode("g"));
	assertTrue(d.addNode("h"));

	f = new DuplicateThresholdFilter(.9);
	List s = new LinkedList();
	s.add(a);
	s.add(b);
	s.add(c);
	s.add(d);
	assertTrue(f.filter(s).size() == 2);
    }
}
