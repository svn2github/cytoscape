package cytoscape.data.readers.unitTests;

import junit.framework.*;

import cytoscape.data.KeggPathways;
import cytoscape.data.readers.KeggPathwaysReader;

//----------------------------------------------------------------------------
public class KeggPathwaysReaderTest extends TestCase {

//----------------------------------------------------------------------------
    public KeggPathwaysReaderTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testReader () throws Exception {
	String filename = "pathways.txt";
	KeggPathwaysReader reader = new KeggPathwaysReader(filename);
	KeggPathways pathways = reader.read();
	pathways.print();
    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (KeggPathwaysReaderTest.class));
    }
//----------------------------------------------------------------------------
}
