package cytoscape.util;

import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.GMLReader;
import java.util.*;
import java.io.*;
import cytoscape.util.CyFileFilter;
import cytoscape.*;
import cytoscape.data.ImportHandler;


public class GMLFileFilter
  extends 
  	CyFileFilter {

	private static String fileNature = ImportHandler.GRAPH_NATURE;
	private static String gml = "gml";
	private static String description = "GML files";

	public GMLFileFilter() {
		super(gml, description, fileNature);
	}
	
	public GraphReader getReader(String fileName) {
		reader = new GMLReader(fileName);
		return reader;
	}
}
