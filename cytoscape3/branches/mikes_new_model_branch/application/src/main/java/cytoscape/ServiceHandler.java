
package cytoscape;

import org.osgi.framework.BundleContext;

public class ServiceHandler {

	private BundleContext bc;

	public ServiceHandler(BundleContext bc) {
		this.bc = bc;
		registerCyFileFilters();
	}

	public void registerCyFileFilters() {
		System.out.println("woohoo!  registering cy file filters!");
	}
	/*
	public void registerCyFileFilters() {
		try {
		ServiceReference[] sr = bc.getServiceReferences(GraphFileFilter.class.getName(), null);
		if ( sr != null )
			for (ServiceReference r : sr )
				Cytoscape.getImportHandler().addFilter( (CyFileFilter)bc.getService(r));
		} catch (Exception e) { e.printStackTrace(); }
	}
	public void applyVisualStyle(GraphView view, String visualStyleName) {
		try {
		ServiceReference[] sr = bc.getServiceReferences(VisualStyle.class.getName(), "(visualStyleName=" + visualStyleName + ")");
		for (ServiceReference r : sr ) {
			VisualStyle vs = (VisualStyle)bc.getService(r);
			vs.apply(view);
			//view.apply(vs);
			return;
		}
		} catch (Exception e) { e.printStackTrace(); }
	}
	*/
}
