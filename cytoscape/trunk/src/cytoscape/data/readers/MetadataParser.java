package cytoscape.data.readers;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBException;

import cytoscape.CyNetwork;
import cytoscape.generated2.Date;
import cytoscape.generated2.Description;
import cytoscape.generated2.Format;
import cytoscape.generated2.Identifier;
import cytoscape.generated2.ObjectFactory;
import cytoscape.generated2.RdfDescription;
import cytoscape.generated2.RdfRDF;
import cytoscape.generated2.Source;
import cytoscape.generated2.Title;
import cytoscape.generated2.Type;

/**
 * This class manipulates network meta data.
 * 
 * @author kono
 * 
 */
public class MetadataParser {

	private String metadataLabel;
	private CyNetwork network;

	// 
	private RdfRDF metadata;

	// Metadata used in cytoscape.  This is a subset of dublin core.
	private static String[] defaultLabels = { "Title", "Identifier", "Source",
			"Type", "Format", "Date", "Description" };

	private static final String DEF_URI = "http://www.cytoscape.org";

	
	public MetadataParser(CyNetwork network) {
		this.metadataLabel = "RDF";
		this.network = network;
		this.metadata = (RdfRDF) network.getClientData(metadataLabel);
	}

	public MetadataParser(CyNetwork network, String metadataLabel) {
		this.metadataLabel = metadataLabel;
		this.network = network;
		this.metadata = (RdfRDF) network.getClientData(metadataLabel);
	}

	/**
	 * Build metadata RDF structure based on given network information.
	 * 
	 * Data items in "defaultLabels" will be created and inserted into RDF
	 * structure.
	 * 
	 * @throws URISyntaxException
	 * 
	 */
	protected HashMap makeNewMetadataMap() throws URISyntaxException {

		HashMap dataMap = new HashMap();
		for (int i = 0; i < defaultLabels.length; i++) {
			if (defaultLabels[i] == "Date") {
				java.util.Date now = new java.util.Date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataMap.put(defaultLabels[i], df.format(now));
			} else if (defaultLabels[i] == "Title") {
				dataMap.put(defaultLabels[i], network.getTitle());
			} else if (defaultLabels[i] == "Source") {
				URI sourceURI = new URI(DEF_URI);
				dataMap.put(defaultLabels[i], sourceURI.toASCIIString());
			} else if (defaultLabels[i] == "Type") {
				dataMap.put(defaultLabels[i], "Foo");
			}

			else {
				dataMap.put(defaultLabels[i], "N/A");
			}

		}
		return dataMap;
	}

	public RdfRDF getMetadata() throws JAXBException, URISyntaxException {

		if (metadata != null) {
			return metadata;
		} else {

			// JAXBContext jc = JAXBContext.newInstance(XGMML_PACKAGE);
			ObjectFactory objFactory = new ObjectFactory();
			metadata = objFactory.createRdfRDF();
			RdfDescription dc = objFactory.createRdfDescription();
			dc.setAbout(DEF_URI);

			HashMap dataMap = makeNewMetadataMap();

			Set labels = dataMap.keySet();
			Object value = null;
			String key = null;

			Iterator it = labels.iterator();
			while (it.hasNext()) {
				key = (String) it.next();
				value = dataMap.get(key);
				dc.getDcmes().add(set(key, value));
				System.out.println("Writing: " + key + ", " + value);
			}

			metadata.getDescription().add(dc);

			network.putClientData(metadataLabel, metadata);
			return metadata;
		}
	}

	private Object set(String label, Object value) throws JAXBException {
		ObjectFactory objF = new ObjectFactory();
		Object newObj = null;

		if (label == "Date") {
			Date dt = objF.createDate();
			dt.getContent().add(value);
			return dt;
		} else if (label == "Title") {
			Title tl = objF.createTitle();
			tl.getContent().add(value);
			return tl;
		} else if (label == "Identifier") {
			newObj = objF.createIdentifier();
			((Identifier) newObj).getContent().add(value);
		} else if (label == "Description") {

			Description dsc = objF.createDescription();
			System.out.println("Description found: " + value);

			dsc.getContent().add(value);
			return dsc;
		} else if (label == "Source") {
			newObj = objF.createSource();
			((Source) newObj).getContent().add(value);
		} else if (label == "Type") {
			newObj = objF.createType();
			((Type) newObj).getContent().add(value);
		} else if (label == "Format") {
			newObj = objF.createFormat();
			((Format) newObj).getContent().add(value);
		} else {
			return null;
		}
		return newObj;
	}

	public boolean hasMetadata() {
		if (metadata == null) {
			return false;
		} else {
			return true;
		}
	}

}
