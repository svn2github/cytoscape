package org.cytoscape.io.internal.read.cysession;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.property.session.Cysession;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.io.internal.read.AbstractPropertyReader;

public class CysessionReader extends AbstractPropertyReader {

	private static final String CYSESSION_PACKAGE = Cysession.class.getPackage().getName();

	public CysessionReader(InputStream is) {
		super(is);
	}

	public void run(TaskMonitor tm) throws Exception {

		final JAXBContext jaxbContext = JAXBContext.newInstance(CYSESSION_PACKAGE);

		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		propertyObject = (Cysession) unmarshaller.unmarshal(inputStream);
	}
}
