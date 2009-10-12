/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.io.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.CyFileFilterable;
import org.cytoscape.io.CyIOFactoryManager;

/**
 * Central registry for all Cytoscape import classes.
 */
public class CyIOFactoryManagerImpl<F extends CyFileFilterable> implements CyIOFactoryManager<F> {

	private Set<F> factories;

	/**
	 * Constructor.
	 */
	public CyIOFactoryManagerImpl() {
		factories = new HashSet<F>();
	}

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void addFactory(F factory, Map props) {
		factories.add(factory);
	}

	public Set<F> getAllFactories()
	{
		return new HashSet<F>(factories);
	}

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void removeFactory(F factory, Map props) {
		factories.remove(factory);
	}

	public F getFactoryFromURI(URI uri) throws IOException
	{
		for (F factory : factories)
		{
			CyFileFilter fileFilter = factory.getCyFileFilter();
			if (fileFilter.accept(uri))
				return factory;
		}
		return null;
	}

	public F getFactoryFromExtensionType(String extensionType)
	{
		for (F factory : factories)
		{
			CyFileFilter fileFilter = factory.getCyFileFilter();
			if (fileFilter.getExtensions().contains(extensionType))
				return factory;
		}
		return null;
	}

	public F getFactoryFromContentType(String contentType)
	{
		for (F factory : factories)
		{
			CyFileFilter fileFilter = factory.getCyFileFilter();
			if (fileFilter.getContentTypes().contains(contentType))
				return factory;
		}
		return null;
	}
}
