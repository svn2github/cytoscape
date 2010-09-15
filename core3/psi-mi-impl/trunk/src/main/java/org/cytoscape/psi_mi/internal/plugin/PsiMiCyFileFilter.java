package org.cytoscape.psi_mi.internal.plugin;

import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;

public class PsiMiCyFileFilter implements CyFileFilter {
	private Set<String> extensions;
	private Set<String> contentTypes;

	public PsiMiCyFileFilter() {
		extensions = new HashSet<String>();
		extensions.add("psi");
		
		contentTypes = new HashSet<String>();
		contentTypes.add("text/psi-mi");
		contentTypes.add("text/psi-mi+xml");
	}
	
	@Override
	public boolean accept(URI uri, DataCategory category) {
		return category.equals(DataCategory.NETWORK);
	}

	@Override
	public boolean accept(InputStream stream, DataCategory category) {
		return category.equals(DataCategory.NETWORK);
	}

	@Override
	public Set<String> getExtensions() {
		return extensions;
	}

	@Override
	public Set<String> getContentTypes() {
		return contentTypes;
	}

	@Override
	public String getDescription() {
		return "PSI-MI files";
	}

	@Override
	public DataCategory getDataCategory() {
		return DataCategory.NETWORK;
	}
}
