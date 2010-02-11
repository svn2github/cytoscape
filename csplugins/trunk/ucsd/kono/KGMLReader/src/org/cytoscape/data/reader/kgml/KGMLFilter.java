package org.cytoscape.data.reader.kgml;

import java.io.File;
import java.io.IOException;

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;

public class KGMLFilter extends CyFileFilter {
	
	private static final String KEGG_DTD = "http://www.genome.jp/kegg/xml/KGML_v0.7.0_.dtd";
	
	/**
	 * KGML Files are Graphs.
	 */
	private static String fileNature = ImportHandler.GRAPH_NATURE;

	/**
	 * File Extensions.
	 */
	private static String[] fileExtensions = { "xml", "kgml" };

	/**
	 * Filter Description.
	 */
	private static String description = "KGML files";

	/**
	 * Constructor.
	 */
	public KGMLFilter() {
		super(fileExtensions, description, fileNature);
	}

	/**
	 * Indicates which files the BioPaxFilter accepts.
	 * <p/>
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
		for (int i = 0; i < fileExtensions.length; i++) {
			if (fileName.endsWith(fileExtensions[i])) {
				firstPass = true;
			}
		}

		if (firstPass) {
			//  Second test:  file header must contain the KGML declaration
			try {
				final String header = getHeader(file);

				if (header.indexOf(KEGG_DTD) > 0) {
					System.out.println("======== This is KGML file.");
					return true;
				}
			} catch (IOException e) {
			}
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
		return new KGMLReader(fileName);
	}
}
