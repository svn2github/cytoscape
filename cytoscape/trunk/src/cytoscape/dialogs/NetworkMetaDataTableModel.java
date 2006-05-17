package cytoscape.dialogs;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.MetadataParser;
import cytoscape.generated2.Date;
import cytoscape.generated2.Description;
import cytoscape.generated2.Format;
import cytoscape.generated2.Identifier;
import cytoscape.generated2.RdfDescription;
import cytoscape.generated2.RdfRDF;
import cytoscape.generated2.Source;
import cytoscape.generated2.Title;
import cytoscape.generated2.Type;
import cytoscape.generated2.impl.DateImpl;
import cytoscape.generated2.impl.DescriptionImpl;
import cytoscape.generated2.impl.FormatImpl;
import cytoscape.generated2.impl.IdentifierImpl;
import cytoscape.generated2.impl.SourceImpl;
import cytoscape.generated2.impl.TitleImpl;
import cytoscape.generated2.impl.TypeImpl;

/**
 * @author kono
 *
 */
public class NetworkMetaDataTableModel extends DefaultTableModel {
	private static final String METADATA_ATTR_NAME = "Network Metadata";
	private CyNetwork network;
	private CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

	Object[][] dataVector;
	HashMap data;

	String description;

	// Define Column names
	private static String[] columnHeader = new String[] { "Data Label", "Value" };
	// Define default entries. This determins the order in the table.
	private static String[] defaultEntries = { "Title", "Identifier", "Source",
			"Type", "Format", "Date" };
	
	// Default values
	//
	private static final String DEF_URI = "http://www.cytoscape.org"; 
	private static final String DEF_FORMAT = "Cytoscape-XGMML";
	private static final String DEF_TYPE = "Protein-Protein Interaction";
	/**
	 * Constructor.
	 * 
	 */

	public NetworkMetaDataTableModel(CyNetwork network) {
		super();
		this.network = network;
	}

	/*
	 * Initialize data
	 */
//	protected void initialize() throws JAXBException {
//		data = loadMetadata();
//	}

	public void setTableData() {

		boolean isNew = false;

		if (networkAttributes.getAttributeMap(network.getIdentifier(),METADATA_ATTR_NAME) == null) {
			isNew = true;
		}

		try {
			setTable(isNew);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void setTable(boolean isNew) throws JAXBException, URISyntaxException {

		Object[] column_names = new Object[2];
		column_names[0] = "Data Label";
		column_names[1] = "Value";

		if (isNew) {
			dataVector = new Object[defaultEntries.length][2];
			data = createNewMetadata();
			Set keySet = data.keySet();

			// "-1" is necessary since data includes desctiption.
			dataVector = new Object[keySet.size()][2];
			description = "N/A";
		} else {
			data = (HashMap) networkAttributes.getAttributeMap(network.getIdentifier(), METADATA_ATTR_NAME);
			Set keySet = data.keySet();

			// "-1" is necessary since data includes desctiption.
			dataVector = new Object[keySet.size()][2];
			
			description = (String) data.get("Description");
		}
		// Set actual data

		// Order vector based on the labels

		for (int i = 0; i < defaultEntries.length; i++) {
			String key = defaultEntries[i];
			dataVector[i][0] = key;
			dataVector[i][1] = data.get(key);
//			System.out.println("Debug: value = " + data.get(key) + ", key is "
//					+ key);
		}

		setDataVector(dataVector, columnHeader);
		
		

	}

	/**
	 * If no Metadata is available, create one for the file.
	 * @return
	 * @throws JAXBException
	 * @throws URISyntaxException 
	 */
	public HashMap createNewMetadata() throws JAXBException, URISyntaxException {

		System.out.println("No metadata is available for this network. Creating new...");

		HashMap dataMap = new HashMap();
		for (int i = 0; i < defaultEntries.length; i++) {
			if (defaultEntries[i] == "Date") {
				java.util.Date now = new java.util.Date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataMap.put(defaultEntries[i], df.format(now));
			} else if (defaultEntries[i] == "Title") {
				dataMap.put(defaultEntries[i], network.getTitle());
			} else if(defaultEntries[i] == "Source") {
				URI sourceURI = new URI(DEF_URI);
				dataMap.put(defaultEntries[i], sourceURI.toASCIIString());
			} else if(defaultEntries[i] == "Type") {
				dataMap.put(defaultEntries[i], DEF_TYPE);
			} else if(defaultEntries[i] == "Format") {
				dataMap.put(defaultEntries[i], DEF_FORMAT);
			} else {
				dataMap.put(defaultEntries[i], "N/A");
			}

		}
		description = "N/A";
		return dataMap;
	}

	/*
	 * Load data from JAXB object
	 */
	public HashMap loadMetadata() throws JAXBException, URISyntaxException {

		// Load RDF Metadata from network's client data
		
		MetadataParser mdp = new MetadataParser(network);
		
		
		RdfRDF metadata = mdp.getMetadata();
		HashMap dataMap = new HashMap();

		// Get metadata from RDF data object
		List descList = metadata.getDescription();
		RdfDescription descriptionObject = (RdfDescription) descList.get(0);

		// Get add DC meta data
		Iterator it = descriptionObject.getDcmes().iterator();

		// Extract data from the description object
		while (it.hasNext()) {
			Object curObj = it.next();
			if (curObj != null) {
				Class dataType = curObj.getClass();

				if (dataType == DateImpl.class) {
					Date dt = (Date) curObj;
					String dateStr = (String) dt.getContent().get(0);

					if (dateStr == null) {
						java.util.Date now = new java.util.Date();
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dateStr = df.format(now);
					}
					dataMap.put("Date", dateStr);
					
				} else if (dataType == TitleImpl.class) {
					Title tl = (Title) curObj;
//					dataMap.put("Title", tl.getContent().get(0));
					dataMap.put("Title",network.getTitle());
					
				} else if (dataType == DescriptionImpl.class) {
					// Keep this information outside of the table data.
					Description ds = (Description) curObj;
					description = (String) ds.getContent().get(0);
				} else if (dataType == IdentifierImpl.class) {
					Identifier id = (Identifier) curObj;
					dataMap.put("Identifier", id.getContent().get(0));
				} else if (dataType == TypeImpl.class) {
					dataMap.put("Type", ((Type) curObj).getContent().get(0));
				} else if (dataType == SourceImpl.class) {
					dataMap
							.put("Source", ((Source) curObj).getContent()
									.get(0));
				} else if (dataType == FormatImpl.class) {
					dataMap
							.put("Format", ((Format) curObj).getContent()
									.get(0));
				}
			}
		}
		return dataMap;

	}

	public void getMetadata() {

	}

	public void setMetadata(String type, Object data) {

	}

	public String getDescription() {

		return description;
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnHeader.length;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		if (data == null) {

			return 0;
		}

		return data.size();

	}

	// Data Label is not editible.
	public boolean isCellEditable(int row, int column) {
		if (column == 0) {
			// Do not allow to edit data names.
			return false;
		} else if( row == 0 ) {
			return false;
		} else {
			return true;
		}
	}

	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return dataVector[arg0][arg1];
	}

	public void setValueAt(Object obj, int row, int col) {

		dataVector[row][col] = obj;
		setDataVector(dataVector, columnHeader);
		fireTableCellUpdated(row, col);
	}

}
