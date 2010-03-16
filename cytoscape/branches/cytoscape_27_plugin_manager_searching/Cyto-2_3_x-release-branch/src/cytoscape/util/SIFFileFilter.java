package cytoscape.util;

import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;
import java.util.*;
import java.io.*;
import cytoscape.util.CyFileFilter;
import cytoscape.*;
import cytoscape.data.ImportHandler;


public class SIFFileFilter
  extends 
  	CyFileFilter {

	private static String fileNature = ImportHandler.GRAPH_NATURE;
	private static String inter = "sif";
	private static String description = "SIF files";

	public SIFFileFilter() {
		super(inter, description, fileNature);
	}
	
	
	public GraphReader getReader(String fileName) {
		reader = new InteractionsReader(null, null, fileName);
		return reader;
	}

}
