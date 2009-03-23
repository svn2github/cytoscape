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
package org.cytoscape.io.read.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderFactory;
import org.cytoscape.io.read.CyReaderManager;

/**
 * Central registry for all Cytoscape import classes.
 */
public class CyReaderManagerImpl implements CyReaderManager {

	private Set<CyReaderFactory> factories;

	/**
	 * Constructor.
	 */
	public CyReaderManagerImpl() {
		factories = new HashSet<CyReaderFactory>();
	}

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void addReaderFactory(CyReaderFactory factory, Map props) {
		factories.add(factory);
	}

	/**
	 * Listener for OSGi service
	 * 
	 * @param factory
	 * @param props
	 */
	@SuppressWarnings("unchecked")
	public void removeReaderFactory(CyReaderFactory factory, Map props) {
		factories.remove(factory);
	}

	/**
	 * Gets the GraphReader that is capable of reading the specified file.
	 * 
	 * @param fileName
	 *            File name or null if no reader is capable of reading the file.
	 * @return GraphReader capable of reading the specified file.
	 */
	public CyReader getReader(URI fileLocation, DataCategory category)
			throws IllegalArgumentException {
		return getReader(fileLocation, null, category);
	}

	public CyReader getReader(InputStream stream, DataCategory category)
			throws IllegalArgumentException {
		return getReader(null, stream, category);
	}

	private CyReader getReader(URI uri, InputStream stream,
			DataCategory category) {

		CyFileFilter cff;
		CyReader reader = null;

		for (CyReaderFactory factory : factories) {
			cff = factory.getCyFileFilter();

			try {
				if (uri != null) {
					if (cff.accept(uri, category))
						reader = factory.getReader(uri);
				} else {
					System.out.println("################# " + cff.getClass());
					//if (cff.accept(stream, category))
						reader = factory.getReader(stream);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(
						"Could not get proper reader for the file.", e);
			}
		}

		if (reader == null) {
			throw new IllegalArgumentException("File type is not supported.");
		}

		return reader;

	}

}
