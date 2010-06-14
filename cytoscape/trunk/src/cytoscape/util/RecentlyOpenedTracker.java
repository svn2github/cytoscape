/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
import cytoscape.CytoscapeInit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.LinkedList;


/**
 *  A class to keep track of a short list of recently opened files.
 */
public class RecentlyOpenedTracker {
	private static final int MAX_TRACK_COUNT = 5;
	private final String trackerFileName;
	private final LinkedList<String> fileNames;

	/**
	 *  Creates a "recently opened" file tracker.
	 *
	 *  @param trackerFileName the name of the file in the Cytoscape config directory to read
	 *         saved file names from.
	 */
	public RecentlyOpenedTracker(final String trackerFileName) throws IOException {
		this.trackerFileName = trackerFileName;
		fileNames = new LinkedList<String>();
		final File input = new File(CytoscapeInit.getConfigVersionDirectory(), trackerFileName);
		final BufferedReader reader = new BufferedReader(new FileReader(input));
		String line;
		while ((line = reader.readLine()) != null && fileNames.size() < MAX_TRACK_COUNT) {
			final String newFileName = line.trim();
			if (newFileName.length() > 0)
				fileNames.addLast(newFileName);
		}
	}

	/**
	 *  @returns the current list of recently opened file names
	 */
	@SuppressWarnings("unchecked") public List<String> getRecentlyOpenedFiles() {
		 return (List<String>)fileNames.clone();
	}

	/**
	 *  Adds "newFileName" to the list of recently opened file names and trims the list if it has
	 *  exceeded its maximum length.
	 */
	public void addFileName(final String newFileName) {
		fileNames.remove(newFileName);
		if (fileNames.size() == MAX_TRACK_COUNT)
			fileNames.removeLast();
		fileNames.addFirst(newFileName);
	}

	/**
	 *  Writes the list of recently opened files to the file specified by the constructor argument.
	 */
	public void writeOut() throws FileNotFoundException {
		final PrintWriter writer = new PrintWriter(new File(CytoscapeInit.getConfigVersionDirectory(), trackerFileName));
		for (final String fileName : fileNames)
			writer.println(fileName);
		writer.close();
	}
}