package cytoscape.util;

import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.XGMMLReader;
import java.util.*;
import java.io.*;
import cytoscape.util.CyFileFilter;
import cytoscape.*;
import cytoscape.data.ImportHandler;


public class XGMMLFileFilter 
  extends 
  	CyFileFilter {

	private static String fileNature = ImportHandler.GRAPH_NATURE;
	private static String[] xgmml = {"xgmml", "xml"};
	private static String description = "XGMML files";

	public XGMMLFileFilter() {
		super(xgmml, description, fileNature);
	}
	
	
	public GraphReader getReader(String fileName) {
		reader = new XGMMLReader(fileName);
		return reader;
	}
}
