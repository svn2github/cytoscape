package org.cytoscape.StringClient;

import org.cytoscape.StringClient.StringStub;
import cytoscape.layout.Tunable;
import cytoscape.util.ModulePropertiesImpl;
import junit.framework.TestCase;

public class StringStubTest extends TestCase {
		
	
	public void testGetURLstr() {

		ModulePropertiesImpl props = new ModulePropertiesImpl("String", "wsc");

		props.add(new Tunable("additional_network_nodes", "Additional network nodes", Tunable.INTEGER,
		                      new Integer(0)));
		props.add(new Tunable("limit", "limit", Tunable.INTEGER, new Integer(10)));		
		props.add(new Tunable("network_depth", "Network depth", Tunable.INTEGER, new Integer(1)));
		props.add(new Tunable("required_score", "Required score", Tunable.INTEGER, new Integer(400)));
		props.add(new Tunable("species", "species", Tunable.STRING, new String("auto_detect")));

		StringStub anInstance = new StringStub();

		anInstance.setQueryParam(props, "Protein1");
		
		String URLstr = anInstance.getURLstr();
		System.out.println("URLstr = " + URLstr + "\n");

		String expectedURLstr = "http://string.embl.de/api/psi-mi/interactions?identifier=PGH1_HUMAN&species=auto_detect";
		assertEquals(expectedURLstr, anInstance.getURLstr());		

	}

}
