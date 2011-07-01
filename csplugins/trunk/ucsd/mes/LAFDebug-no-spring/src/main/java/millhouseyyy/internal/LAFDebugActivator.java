
package millhouseyyy.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class LAFDebugActivator implements BundleActivator {

	public void start(BundleContext bc) {
		new LAFDebug();
	}

	public void stop(BundleContext bc) {
	}
}
