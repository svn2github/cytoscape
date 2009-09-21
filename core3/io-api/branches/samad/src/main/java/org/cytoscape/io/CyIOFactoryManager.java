package org.cytoscape.io;

public interface CyIOFactoryManager<F extends CyFileFilterable>
{
	@SuppressWarnings("unchecked")
	public void addFactory(F factory, Map props);

	@SuppressWarnings("unchecked")
	public void removeFactory(F factory, Map props);

	public F getFactoryFromURI(URI resourceLocation) throws IOException;

	public F getFactoryFromExtensionType(String extensionType);

	public F getFactoryFromContentType(String contentType);

}
