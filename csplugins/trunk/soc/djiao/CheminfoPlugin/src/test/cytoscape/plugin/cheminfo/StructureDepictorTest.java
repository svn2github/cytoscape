package cytoscape.plugin.cheminfo;

import junit.framework.TestCase;

public class StructureDepictorTest extends TestCase {

	public void testDepictWithUCSFSmi2Gif() {
		fail("Not yet implemented");
	}

	public void testConvertInchiToSmiles() {
		String inchi = "InChI=1/C4H10/c1-3-4-2/h3-4H2,1-2H3";
		String smiles = "CCCC";
		String result = StructureDepictor.convertInchiToSmiles(inchi);
		assertEquals(smiles, result);
	}

	public void testDepiectInchi() {
		fail("Not yet implemented");
	}

}
