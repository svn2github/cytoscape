/*
 File: XGMMLReader.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute of Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.io.internal.read.xgmml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cytoscape.io.internal.read.xgmml.handler.ReadDataManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.io.read.CyNetworkReaderFactory;
import org.cytoscape.io.CyFileFilter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.ParserAdapter;

/**
 * XGMML file reader.<br>
 * This version is Metanode-compatible.
 * 
 * @version 1.0
 * @since Cytoscape 2.3
 * @see cytoscape.data.writers.XGMMLWriter
 * @author kono
 * 
 */
public class XGMMLReaderFactory implements CyNetworkReaderFactory {

	protected static final String CY_NAMESPACE = "http://www.cytoscape.org";

	private XGMMLParser parser;

	private Properties prop;

	private CyFileFilter fileFilter;

	/**
	 * Constructor.
	 */
	public XGMMLReaderFactory(XGMMLParser parser, Properties prop, CyFileFilter fileFilter) {
		this.parser = parser;
		this.prop = prop;
		this.fileFilter = fileFilter;
	}

	public CyFileFilter getCyFileFilter()
	{
		return fileFilter;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public Task getReader(InputStream input, CyNetwork network, CyDataTable dataTable) {
		
		return new XGMMLReader(input, network, dataTable);
	}

	class XGMMLReader implements Task
	{
		final InputStream input;
		final CyNetwork network;
		final CyDataTable dataTable;
		final ReadDataManager readDataManager;
		boolean cancel = false;

		public XGMMLReader(InputStream input, CyNetwork network, CyDataTable dataTable) {
			this.input = input;
			this.network = network;
			this.dataTable = dataTable;
			this.readDataManager = new ReadDataManager();
			readDataManager.setNetwork(network);
		}

		/**
		 * Actual method to read XGMML documents.
		 * 
		 * @throws IOException
		 * @throws IOException
		 * @throws SAXException
		 * @throws ParserConfigurationException
		 */
		public void run(TaskMonitor monitor) throws Exception {
			monitor.setTitle("Reading XGMML document");

			final SAXParserFactory spf = SAXParserFactory.newInstance();

			try {
				// Get our parser
				SAXParser sp = spf.newSAXParser();
				ParserAdapter pa = new ParserAdapter(sp.getParser());
				pa.setContentHandler(parser);
				pa.setErrorHandler(parser);
				pa.parse(new InputSource(input));

			} catch (OutOfMemoryError oe) {
				// It's not generally a good idea to catch OutOfMemoryErrors, but in
				// this case, where we know the culprit (a file that is too large),
				// we can at least try to degrade gracefully.
				System.gc();
				throw new RuntimeException(
						"Out of memory error caught! The network being loaded is too large for the current memory allocation.  Use the -Xmx flag for the java virtual machine to increase the amount of memory available, e.g. java -Xmx1G cytoscape.jar -p plugins ....");
			} catch (ParserConfigurationException e) {
			} catch (SAXParseException e) {
				System.err.println("XGMMLParser: fatal parsing error on line "
						+ e.getLineNumber() + " -- '" + e.getMessage() + "'");
				throw e;
			} catch(IOException e) {
				// Ignore IOException if we've cancelled
				if (!cancel)
					throw e;
			}
		}

		public void cancel()
		{
			if (cancel)
				return;

			cancel = true;
			try
			{
				input.close();
			}
			catch (IOException e)
			{
			}
		}

	}
}
