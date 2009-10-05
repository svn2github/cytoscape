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
package org.cytoscape.ide.eclipse.setup;

import java.io.File;
import java.util.Properties;

import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.SVNProviderPlugin;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;

/**
 *
 */
public class MavenSetup {
	// SVN location 1: public repository
	private static final String openRepo = "http://chianti.ucsd.edu/svn";

	// SVN location 2: CORE DEVELOPERS ONLY.
	private static final String coreRepo = "svn+ssh://grenache.ucsd.edu/cellar/common/svn";
	private static final String[] repos = { openRepo, coreRepo };

	/**
	 * DOCUMENT ME!
	 */
	public static void setRepository() {

		final SVNProviderPlugin provider = SVNProviderPlugin.getPlugin();
		ISVNClientAdapter svnClient = null;
		try {
			svnClient = provider.getSVNClient();
		} catch (SVNException e) {
			System.err.println("Could not create SVN client.");
			e.printStackTrace();
		}

		for (String url : repos) {
			System.out.println("Trying to add repo: " + url);

			// If already registered, ignore.
			try {
				if (provider.getRepositories().isKnownRepository(url, false)
						|| provider.getRepository(url) != null) {
					System.out.println("SVN Repository already exists: " + url);
					continue;
				}
			} catch (SVNException e) {
				System.err.println("Could not get repository: " + url);
				e.printStackTrace();
			}

			File path = new File(url);

			if (!path.exists())
				path.mkdirs();

			try {
				svnClient.createRepository(path,
						ISVNClientAdapter.REPOSITORY_FSTYPE_FSFS);

				Properties properties = new Properties();
				properties.setProperty("url", url);

				ISVNRepositoryLocation repository = provider.getRepositories()
						.createRepository(properties);

				provider.getRepositories().addOrUpdateRepository(repository);
			} catch (Exception e) {
				System.err.println("Exception occured when adding repository: "
						+ url);
			}
			System.out.println("Repo added: " + url);
		}

	}
}
