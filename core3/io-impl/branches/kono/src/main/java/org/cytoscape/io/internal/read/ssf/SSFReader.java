package org.cytoscape.io.internal.read.ssf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.io.internal.read.AbstractNetworkReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;

public class SSFReader extends AbstractNetworkReader {

	
	
	private final SSFParser parser;
	
	private final CyRootNetworkFactory rnFactory;

	public SSFReader(CyRootNetworkFactory rnFactory) {
		super();
		this.rnFactory = rnFactory;
		parser = new SSFParser();
	}

	public Map<Class<?>, Object> read() throws IOException {
		Map<Class<?>, Object> result = new HashMap<Class<?>, Object>();
		
		// Create buffered reader from given InputStream 
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		
		final CyNetwork network = cyNetworkFactory.getInstance();
		final CyRootNetwork rootNet = rnFactory.convert(network);
		
		String line = null;
		try {

			while ((line = in.readLine()) != null)
				parser.parse(rootNet, line);

		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw e;
			}
		}

		result.put(CyNetwork.class, rootNet.getBaseNetwork());
		parser.flush();
		return result;
	}

}
