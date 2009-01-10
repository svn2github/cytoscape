/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.coreplugin.psi_mi.plugin;

import cytoscape.data.readers.AbstractGraphReader;

import org.cytoscape.coreplugin.psi_mi.cyto_mapper.MapToCytoscape;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiOneToInteractions;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiTwoFiveToInteractions;
import org.cytoscape.coreplugin.psi_mi.util.ContentReader;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * GraphReader Implementation for PSI-MI Files.
 *
 * @author Ethan Cerami.
 */
public class PsiMiGraphReader extends AbstractGraphReader {
	private int[] nodeIndices;
	private int[] edgeIndices;
	private String networkName;

	/**
	 * Constructor
	 *
	 * @param fileName File Name.
	 */
	public PsiMiGraphReader(String fileName) {
		super(fileName);
	}

	/**
	 * Read file.
	 *
	 * @throws IOException IO Error.
	 */
	public void read() throws IOException {
		try {
			//  set network name - use pathway name
			networkName = fileName;

			ContentReader reader = new ContentReader();
			String xml = reader.retrieveContent(fileName);

			//  Map BioPAX Data to Cytoscape Nodes/Edges
			List interactions = new ArrayList();

			//  Pick one of two mappers
			int level2 = xml.indexOf("level=\"2\"");

			if ((level2 > 0) && (level2 < 500)) {
				MapPsiTwoFiveToInteractions mapper = new MapPsiTwoFiveToInteractions(xml,
				                                                                     interactions);
				mapper.doMapping();
			} else {
				MapPsiOneToInteractions mapper = new MapPsiOneToInteractions(xml, interactions);
				mapper.doMapping();
			}

			//  Now Map to Cytocape Network Objects.
			MapToCytoscape mapper2 = new MapToCytoscape(interactions, MapToCytoscape.SPOKE_VIEW);
			mapper2.doMapping();
			nodeIndices = mapper2.getNodeIndices();
			edgeIndices = mapper2.getEdgeIndices();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Get Node Indices.
	 *
	 * @return array of root graph node indices.
	 */
	public int[] getNodeIndicesArray() {
		return nodeIndices;
	}

	/**
	 * Get Edge Indices.
	 *
	 * @return array of root graph edge indices.
	 */
	public int[] getEdgeIndicesArray() {
		return edgeIndices;
	}

	/**
	 * Gets network name.
	 *
	 * @return network name.
	 */
	public String getNetworkName() {
		return networkName;
	}
}
