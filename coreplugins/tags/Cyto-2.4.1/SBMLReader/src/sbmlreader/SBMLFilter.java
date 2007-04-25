package sbmlreader; 

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;

import java.io.File;
import java.io.IOException;

/**
 * SBMLReader extends CyFileFilter for integration into the Cytoscape ImportHandler
 * framework.
 *
 */
public class SBMLFilter extends CyFileFilter {

    /**
     * XGMML Files are Graphs.
     */
    private static String fileNature = ImportHandler.GRAPH_NATURE;

    /**
     * File Extensions.
     */
    private static String[] fileExtensions = {"xml", "sbml"};

    /**
     * Filter Description.
     */
    private static String description = "SBML files";

    /**
     * Constructor.
     */
    public SBMLFilter() {
        super(fileExtensions, description, fileNature);
    }

    /**
     * Indicates which files the SBMLFilter accepts.
     * <p/>
     * This method will return true only if:
     * <UL>
     * <LI>File ends in .xml or .sbml;  and
     * <LI>File headers includes the www.sbml.org namespace declaration.
     * </UL>
     *
     * @param file File
     * @return true or false.
     */
    public boolean accept(File file) {
        String fileName = file.getName();
        boolean firstPass = false;
        //  First test:  file must end with one of the registered file extensions.
        for (int i = 0; i < fileExtensions.length; i++) {
            if (fileName.endsWith(fileExtensions[i])) {
                firstPass = true;
            }
        }
        if (firstPass) {
            //  Second test:  file header must contain the biopax declaration
            try {
                String header = getHeader(file);
                if (header.indexOf("www.sbml.org") > 0) {
                    return true;
                }
            } catch (IOException e) { }
        }
        return false;
    }

    /**
     * Gets the appropirate GraphReader object.
     *
     * @param fileName File Name.
     * @return GraphReader Object.
     */
    public GraphReader getReader(String fileName) {
        return new SBMLGraphReader(fileName);
    }
}
