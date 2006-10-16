package org.mskcc.biopax_plugin.plugin;

import cytoscape.util.CyFileFilter;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.ImportHandler;

import java.io.File;
import java.io.IOException;

/**
 * BioPax Filer class.  Extends CyFileFilter for integration into the Cytoscape ImportHandler
 * framework.
 *
 * @author Ethan Cerami.
 */
public class BioPaxFilter extends CyFileFilter {

    /**
     * XGMML Files are Graphs.
     */
    private static String fileNature = ImportHandler.GRAPH_NATURE;

    /**
     * File Extensions.
     */
    private static String[] fileExtensions = {"xml", "owl"};

    /**
     * Filter Description.
     */
    private static String description = "XML files";

    /**
     * Constructor.
     */
    public BioPaxFilter() {
        super(fileExtensions, description, fileNature);
    }

    /**
     * Indicates which files the BioPaxFilter accepts.
     *
     * This method will return true only if:
     * <UL>
     * <LI>File ends in .xml or .owl;  and
     * <LI>File headers includes the www.biopax.org namespace declaration.
     * </UL>
     *
     * @param file File
     * @return true or false.
     */
    public boolean accept(File file) {
        String fileName = file.getName();
        boolean firstPass = false;
        //  First test:  file must end with one of the registered file extensions.
        for (int i=0; i<fileExtensions.length; i++) {
            if (fileName.endsWith(fileExtensions[i])) {
                firstPass = true;
            }
        }
        if (firstPass) {
            //  Second test:  file header must contain the biopax declaration
            try {
                String header = getHeader(file);
                if (header.indexOf("www.biopax.org") >0) {
                    return true;
                }
            } catch (IOException e) {
            }
        }
        return false;
    }

    /**
     * Gets the appropirate GraphReader object.
     * @param fileName File Name.
     * @return GraphReader Object.
     */
    public GraphReader getReader(String fileName) {
        return new BioPaxGraphReader(fileName);
    }
}
