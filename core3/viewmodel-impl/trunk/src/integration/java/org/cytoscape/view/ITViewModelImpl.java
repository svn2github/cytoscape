package org.cytoscape.view;

import java.util.jar.Manifest;

import org.cytoscape.integration.AbstractIntegrationTester;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.RootVisualLexicon;

/**
 * Integration test for viewmodel-impl bundle.
 * 
 * @author kono
 * 
 */
public class ITViewModelImpl extends AbstractIntegrationTester {

	public ITViewModelImpl() {
		super("org.cytoscape.viewmodel-impl", 
				new String[] {
					"org.cytoscape, event-api, 1.0-SNAPSHOT",
					"org.cytoscape, event-impl, 1.0-SNAPSHOT",
					"org.cytoscape, model-api, 1.0-SNAPSHOT",
					"org.cytoscape, integration-test-support, 1.0-SNAPSHOT",
					"org.cytoscape, service-util, 1.0-SNAPSHOT",
					"org.cytoscape, viewmodel-api, 1.0-SNAPSHOT",
					"org.cytoscape, viewmodel-impl, 1.0-SNAPSHOT", }, 
				new String[] {
					"rootVisualLexicon", "cyNetworkViewFactory" }, 
				new Class[] {
					RootVisualLexicon.class, CyNetworkViewFactory.class });
	}

	/**
	 * Provide correct metadata for on-the-fly bundle
	 * 
	 */
	@Override
	protected Manifest getManifest() {
		// let the testing framework create/load the manifest
		Manifest mf = super.getManifest();
		
		// add import statement
		String original = mf.getMainAttributes().getValue("Import-Package");
		mf.getMainAttributes().putValue("Import-Package",
				original + ",org.cytoscape.view.model");

		for (Object key : mf.getMainAttributes().keySet()) {
			System.out.print("############ MF KEY = " + key);
			System.out.println(", VAL = "
					+ mf.getMainAttributes().getValue(key.toString()));
		}

		return mf;
	}
}
