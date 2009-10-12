package org.cytoscape.io;

import java.util.Map;
import java.util.Set;
import java.net.URI;
import java.io.IOException;

public interface CyIOFactoryManager<F extends CyFileFilterable>
{
	@SuppressWarnings("unchecked")
	public void addFactory(F factory, Map props);

	@SuppressWarnings("unchecked")
	public void removeFactory(F factory, Map props);

	public Set<F> getAllFactories();

	public F getFactoryFromURI(URI resourceLocation) throws IOException;

	public F getFactoryFromExtensionType(String extensionType);

	public F getFactoryFromContentType(String contentType);

}
