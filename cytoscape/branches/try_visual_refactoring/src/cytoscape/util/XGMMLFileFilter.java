package cytoscape.util;

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.XGMMLReader;

/**
 * FileFilter for Reading in XGMML Files.
 *
 * @author Cytoscape Development Team.
 */
public class XGMMLFileFilter extends CyFileFilter {

    /**
     * XGMML Files are Graphs.
     */
    private static String fileNature = ImportHandler.GRAPH_NATURE;

    /**
     * File Extensions.
     */
    private static String[] fileExtensions = {"xgmml", "xml"};

    /**
     * Filter Description.
     */
    private static String description = "XGMML files";

    /**
     * Constructor.
     */
    public XGMMLFileFilter() {
        super(fileExtensions, description, fileNature);
    }

    /**
     * Gets Graph Reader.
     * @param fileName File name.
     * @return GraphReader Object.
     */
    public GraphReader getReader(String fileName) {
        reader = new XGMMLReader(fileName);
        return reader;
    }
}