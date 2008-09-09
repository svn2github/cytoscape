package org.mskcc.biopax_plugin.mapping;

import org.jdom.Element;
import org.mskcc.biopax_plugin.util.rdf.RdfQuery;

import java.util.List;

/**
 * Extracts a BioPAX name, via rules of precedence.
 *
 */
public class BioPaxNameUtil {
    private RdfQuery rdfQuery;

    /**
     * Constructor.
     * @param rdfQuery RDF Query Object.
     */
    public BioPaxNameUtil (RdfQuery rdfQuery) {
        this.rdfQuery = rdfQuery;
    }

    /**
     * Gets Node Name.
     * @param id    Node ID.
     * @param e     JDOM Element.
     * @return      Node Name.
     */
    public String getNodeName(String id, Element e) {

		String nodeName = null;
		List nameList = null;
		Element nameElement = null;

		// short name
		nameList = rdfQuery.getNodes(e, "SHORT-NAME");

		if ((nameList != null) && (nameList.size() > 0)) {
			nameElement = (Element) nameList.get(0);
			nodeName = nameElement.getTextNormalize();
		}

		if ((nodeName != null) && (nodeName.length() > 0)) {
			return nodeName;
		}

		// name
		nameList = rdfQuery.getNodes(e, "NAME");

		if ((nameList != null) & (nameList.size() > 0)) {
			nameElement = (Element) nameList.get(0);
			nodeName = nameElement.getTextNormalize();
		}

		if ((nodeName != null) && (nodeName.length() > 0)) {
			return nodeName;
		}

		// shortest synonym
		int shortestSynonymIndex = -1;
		nameList = rdfQuery.getNodes(e, "SYNONYMS");

		if ((nameList != null) && (nameList.size() > 0)) {
			int minLength = -1;

			for (int lc = 0; lc < nameList.size(); lc++) {
				nameElement = (Element) nameList.get(lc);

				String curNodeName = nameElement.getTextNormalize();

				if ((minLength == -1) || (curNodeName.length() < minLength)) {
					minLength = curNodeName.length();
					nodeName = curNodeName;
				}
			}

			if (shortestSynonymIndex > -1) {
				return nodeName;
			}
		}

		// made it this far, outta here
		return null;
	}
}
