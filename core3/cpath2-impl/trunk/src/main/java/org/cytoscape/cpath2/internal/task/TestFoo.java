package org.cytoscape.cpath2.internal.task;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

import org.biopax.paxtools.io.simpleIO.SimpleReader;
import org.biopax.paxtools.model.Model;

public class TestFoo extends XMLInputFactory {
	public static void main(String[] args) throws Exception {
//		System.setProperty("javax.xml.stream.XMLInputFactory", "com.bea.xml.stream.MXParserFactory");
	    Model model = (new SimpleReader()).convertFromOWL(new FileInputStream("/Users/jay/Downloads/old/owl.xml"));
	}

	public TestFoo() {
		throw new RuntimeException();
	}
	
	@Override
	public XMLStreamReader createFilteredReader(XMLStreamReader arg0,
			StreamFilter arg1) throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventReader createFilteredReader(XMLEventReader arg0,
			EventFilter arg1) throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventReader createXMLEventReader(Reader arg0)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventReader createXMLEventReader(XMLStreamReader arg0)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventReader createXMLEventReader(Source arg0)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventReader createXMLEventReader(InputStream arg0)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventReader createXMLEventReader(String arg0, Reader arg1)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventReader createXMLEventReader(InputStream arg0, String arg1)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventReader createXMLEventReader(String arg0, InputStream arg1)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLStreamReader createXMLStreamReader(Reader arg0)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLStreamReader createXMLStreamReader(Source arg0)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLStreamReader createXMLStreamReader(InputStream arg0)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLStreamReader createXMLStreamReader(InputStream arg0, String arg1)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLStreamReader createXMLStreamReader(String arg0, InputStream arg1)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLStreamReader createXMLStreamReader(String arg0, Reader arg1)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLEventAllocator getEventAllocator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProperty(String arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLReporter getXMLReporter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLResolver getXMLResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPropertySupported(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEventAllocator(XMLEventAllocator arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(String arg0, Object arg1)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setXMLReporter(XMLReporter arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setXMLResolver(XMLResolver arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
