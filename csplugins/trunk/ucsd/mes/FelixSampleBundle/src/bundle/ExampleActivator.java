package bundle;

import org.osgi.framework.*;

import cytoscape.Cytoscape;
import cytoscape.view.CyMenus;

public class ExampleActivator implements BundleActivator {
	public void start(BundleContext context) {
		System.out.println("Example Bundle doing something");
		ExampleAction ca = new ExampleAction();
		Cytoscape.getDesktop().getCyMenus().addAction( ca ); 
	}

	public void stop(BundleContext context) {
		System.out.println("Example Bundle NOT doing something");
	}
}

