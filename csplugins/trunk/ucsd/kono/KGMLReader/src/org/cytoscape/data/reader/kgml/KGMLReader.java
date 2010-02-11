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
import cytoscape.data.readers.GraphReader;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.util.URLUtil;

public class KGMLReader implements GraphReader {

	private static final String PACKAGE_NAME = "org.cytoscape.data.reader.kgml.generated";

	private URL targetURL;

	public KGMLReader(final String fileName) {
		System.out.println("File name = " + fileName);
		try {
			this.targetURL = (new File(fileName)).toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doPostProcessing(CyNetwork arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getEdgeIndicesArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CyLayoutAlgorithm getLayoutAlgorithm() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNetworkName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getNodeIndicesArray() {
		// TODO Auto-generated method stub
		return null;
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
		} catch (Exception e) {
			e.printStackTrace();

			throw new IOException("Could not unmarshall KGML");
		} finally {
			if (is != null) {
				is.close();
			}
		}
		
		System.out.println("Got Pathway: " + pathway.getName());
		
		final PathwayMapper mapper = new PathwayMapper(pathway);
		mapper.doMapping();
		                                                  

	}

}
