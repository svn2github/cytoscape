package org.cytoscape.data.reader.kgml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.data.reader.kgml.generated.Pathway;

import cytoscape.CyNetwork;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.util.URLUtil;

public class KGMLReader extends AbstractGraphReader {

	private static final String PACKAGE_NAME = "org.cytoscape.data.reader.kgml.generated";

	private URL targetURL;
	
	private int[] nodeIdx;
	private int[] edgeIdx;
	
	private String networkName;
	
	private PathwayMapper mapper;

	public KGMLReader(final String fileName) {
		super(fileName);
		System.out.println("File name = " + fileName);
		try {
			this.targetURL = (new File(fileName)).toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doPostProcessing(CyNetwork network) {
		mapper.updateView(network);
	}

	@Override
	public int[] getEdgeIndicesArray() {
		return edgeIdx;
	}

	@Override
	public String getNetworkName() {
		return networkName;
	}

	@Override
	public int[] getNodeIndicesArray() {
		return nodeIdx;
	}

	@Override
	public void read() throws IOException {
		System.out.println("Reading KGML");
		InputStream is = null;
		Pathway pathway;
		
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(
					PACKAGE_NAME, this.getClass().getClassLoader());
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			

			is = URLUtil.getBasicInputStream(targetURL);
			pathway = (Pathway) unmarshaller.unmarshal(is);
			networkName = pathway.getTitle();
			
		} catch (Exception e) {
			e.printStackTrace();

			throw new IOException("Could not unmarshall KGML");
		} finally {
			if (is != null) {
				is.close();
			}
		}
			
		mapper = new PathwayMapper(pathway);
		mapper.doMapping();
		nodeIdx = mapper.getNodeIdx();

	}

}
