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
package edu.ucsd.bioeng.idekerlab.uniprotplugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtRemoteServiceFactory;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.util.ModuleProperties;
import cytoscape.util.ModulePropertiesImpl;
import giny.model.Node;


/**
 * UniProt Remote Service API.
 * This client returns service factory as the stub.
 *
 */
public class UniProtClient extends WebServiceClientImpl {
	private static final String DISPLAY_NAME = "UniProt Web Service Client";
	private static final String CLIENT_ID = "uniprot";
	private static final String END_POINT = "";
	private static final UniProtClient client;

	static {
		client = new UniProtClient();
	}

	/**
	 * Creates a new factory object.
	 */
	public UniProtClient() {
		super(CLIENT_ID, DISPLAY_NAME, new ClientType[] { ClientType.ATTRIBUTE });

		stub = new UniProtRemoteServiceFactory();

		// Setup props
		ModuleProperties props = new ModulePropertiesImpl(displayName, "wsc");
		this.props = props;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static WebServiceClient getClient() {
		return client;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	@Override
	public void executeService(CyWebServiceEvent e) {
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(WSEventType.IMPORT_ATTRIBUTE)) {
				importAttributes((AttributeImportQuery) e.getParameter());
			}
		}
	}

	private void importAttributes(AttributeImportQuery parameter) {
		final CyAttributes attr = Cytoscape.getNodeAttributes();
		final List<String> ids = buildIdList(parameter);
		final Query q = UniProtQueryBuilder.buildIDListQuery(ids);
		
		final UniProtQueryService service = ((UniProtRemoteServiceFactory)stub).getUniProtQueryService();
		
		final EntryIterator<UniProtEntry> et1 = service.getEntryIterator(q);
		
		String acc = null;
		Collection<DatabaseCrossReference> refs;
		
		for (UniProtEntry en : et1) {
			acc = en.getPrimaryUniProtAccession().getValue();

			if (ids.contains(acc) == false) {
				acc = null;

				List<SecondaryUniProtAccession> secs = en.getSecondaryUniProtAccessions();

				for (SecondaryUniProtAccession sc : secs) {
					System.out.println("Search Secondary for "
					                   + en.getPrimaryUniProtAccession().getValue() + " = "
					                   + sc.getValue());

					if (ids.contains(sc.getValue())) {
						acc = sc.getValue();
						System.out.println("Found: " + acc);

						break;
					}
				}

				if (acc == null) {
					continue;
				}
				
				// Extract actual data fields.
				
				// Summary
				en.getDescription().get
				

			}
		}
		
	}

	private List<String> buildIdList(AttributeImportQuery parameter) {
		final List<String> ids = new ArrayList<String>();

		final String attrName = parameter.getKeyCyAttrName();
		List<Node> nodes = Cytoscape.getRootGraph().nodesList();

		if (attrName.equals("ID")) {
			// Use ID as the key
			for (Node n : nodes) {
				ids.add(n.getIdentifier());
			}
		} else {
			// Use Attributes for mapping
			final CyAttributes attrs = Cytoscape.getNodeAttributes();
			Map mapAttrs = CyAttributesUtils.getAttribute(attrName, attrs);

			if ((mapAttrs == null) || (mapAttrs.size() == 0))
				return null;

//			List acceptedClasses = Arrays.asList(mapping.getAcceptedDataClasses());
//			Class mapAttrClass = CyAttributesUtils.getClass(attrName, attrs);
//
//			if ((mapAttrClass == null) || !(acceptedClasses.contains(mapAttrClass)))
//				return null;

			ids.addAll(loadKeySet(mapAttrs));
			
		}

		return ids;
	}
	
	private Set<String> loadKeySet(final Map mapAttrs) {
		final Set<String> mappedKeys = new TreeSet<String>();

		final Iterator keyIter = mapAttrs.values().iterator();

		Object o = null;

		while (keyIter.hasNext()) {
			o = keyIter.next();

			if (o instanceof List) {
				List list = (List) o;

				for (int i = 0; i < list.size(); i++) {
					Object vo = list.get(i);

					if (!mappedKeys.contains(vo))
						mappedKeys.add(vo.toString());
				}
			} else {
				if (!mappedKeys.contains(o))
					mappedKeys.add(o.toString());
			}
		}

		return mappedKeys;
	}
}
