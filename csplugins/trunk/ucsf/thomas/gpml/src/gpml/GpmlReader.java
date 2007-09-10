package gpml;

import giny.view.GraphView;
import giny.view.NodeView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.GpmlFormat;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.GraphLink.GraphRefContainer;
import org.pathvisio.model.PathwayElement.MPoint;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.LayoutAdapter;
import cytoscape.layout.LayoutAlgorithm;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import ding.view.DingCanvas;

public class GpmlReader extends AbstractGraphReader {
	GpmlConverter converter;
	GpmlAttributeHandler gpmlHandler;
	
	public GpmlReader(String fileName, GpmlAttributeHandler gpmlHandler) {
		super(fileName);
		this.gpmlHandler = gpmlHandler;
	}

	public void read() throws IOException {
		try {
			Pathway pathway = new Pathway();
			pathway.readFromXml(new File(fileName), true);
			converter = new GpmlConverter(gpmlHandler, pathway);
		} catch(Exception ex) {
			throw new IOException(ex.getMessage());
		}
	}
	
	public CyLayoutAlgorithm getLayoutAlgorithm() {
		return new LayoutAdapter() {
			public void doLayout(CyNetworkView networkView, TaskMonitor monitor) {
				converter.layout(networkView);
			}
		};
	}
	
	public int[] getEdgeIndicesArray() {
		return converter.getEdgeIndicesArray();
	}
	
	public int[] getNodeIndicesArray() {
		return converter.getNodeIndicesArray();
	}
		
	public String getNetworkName() {
		String pwName = converter.getPathway().getMappInfo().getMapInfoName();
		return pwName == null ? super.getNetworkName() : pwName;
	}
}