package cytoscape.data.writers;

import giny.model.GraphObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import cytoscape.Cytoscape;
import cytoscape.data.AttributeSaverDialog;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMap;


/**
 * CyAttributeWriter extracted from AttributeSaverDialog.
 *
 */
public class CyAttributesWriter2 {

	public static String newline = System.getProperty("line.separator");

	private final int type;
	private final Vector<Boolean> selectedAttributes;
	private final File saveDirectory;
	private final Vector<String> fileNames;
	private final Vector<String> attributeNames;

	public CyAttributesWriter2(final int type, final File saveDirectory,
			final Vector<Boolean> selectedAttributes, final Vector<String> fileNames, final Vector<String> attributeNames) {
		this.type = type;
		this.selectedAttributes = selectedAttributes;
		this.saveDirectory = saveDirectory;
		this.fileNames = fileNames;
		this.attributeNames = attributeNames;
	}

	/**
	 * Write out the state for the given attributes
	 * 
	 * @param selectedRows
	 * 
	 * @return number of files successfully saved, the better way to do this
	 *         would just be to throw the error and display a specific message
	 *         for each failure, but oh well.
	 * @throws IOException
	 * 
	 */
	public int writeAttributesToFiles() throws IOException {

		final List graphObjects;
		final CyAttributes cyAttributes;
		if (type == AttributeSaverDialog.NODES) {
			cyAttributes = Cytoscape.getNodeAttributes();
			graphObjects = Cytoscape.getCyNodesList();
		} else {
			cyAttributes = Cytoscape.getEdgeAttributes();
			graphObjects = Cytoscape.getCyEdgesList();
		}

		final List<String> objectIDs = new ArrayList<String>();
		String objectID = null;
		List<GraphObject> objects = graphObjects;
		for (GraphObject obj : objects) {
			objectID = obj.getIdentifier();
			if (objectID != null) {
				objectIDs.add(objectID);
			}
		}

		int count = 0;
		for (int idx = 0; idx < attributeNames.size(); idx++) {
			if (selectedAttributes.get(idx)) {

				final String attributeName = attributeNames.get(idx);

				final File attributeFile = new File(saveDirectory,
						fileNames.get(idx));
				final FileWriter fileWriter = new FileWriter(attributeFile);
				fileWriter.write(attributeName + newline);

				final MultiHashMap attributeMap = cyAttributes
						.getMultiHashMap();
				if (attributeMap != null) {
					for (String name : objectIDs) {
						Object value = attributeMap.getAttributeValue(name,
								attributeName, null);

						if (value != null) {
							if (value instanceof Collection) {
								String result = name + " = ";
								Collection collection = (Collection) value;
								if (collection.size() > 0) {
									Iterator objIt = collection.iterator();
									result += "(" + objIt.next();
									while (objIt.hasNext()) {
										result += "::" + objIt.next();
									}
									result += ")" + newline;
									fileWriter.write(result);
								}
							} else {
								fileWriter
										.write(name + " = " + value + newline);
							}
						}
					}
				}
				fileWriter.close();
				count++;

			}
		}
		return count;
	}

}
