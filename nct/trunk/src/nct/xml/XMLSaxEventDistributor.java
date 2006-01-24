
//============================================================================
// 
//  file: XMLSaxEventDistributor.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.xml; 

import java.io.IOException;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class acts as a Handler but instead of handling SAX events, it simply 
 * distributes the events (calls the same methods) to a list of handlers. 
 */
public class XMLSaxEventDistributor<HandlerType extends ContentHandler & ErrorHandler & DTDHandler> implements ContentHandler, ErrorHandler, DTDHandler
{
	protected List<HandlerType> handlers;  

	public XMLSaxEventDistributor() {
		handlers = new ArrayList<HandlerType>();
	}

	public XMLSaxEventDistributor(List<HandlerType> handlers ) {
		this.handlers = handlers; 
	}

	public void addHandler(HandlerType h ) {
		handlers.add( h );
	}

	public void parse( String xmlFile ) {
		try { 
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			xr.parse(new InputSource(new FileReader(xmlFile)));
		} catch (Exception e) { e.printStackTrace(); }
	}

	//---------------------------------------------------------------------
	// ContentHandler Interface
	//---------------------------------------------------------------------

	public void characters (char[] ch, int start, int length) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.characters(ch,start,length);
	}

	public void endDocument () throws SAXException { 
		for ( HandlerType d : handlers ) 
			d.endDocument();
	}

	public void endElement (String uri, String name, String qName) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.endElement(uri,name,qName);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.endPrefixMapping(prefix);
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.ignorableWhitespace(ch,start,length);
	}

	public void processingInstruction(String target, String data) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.processingInstruction(target,data);
	}

	public void setDocumentLocator(Locator locator) {
		for ( HandlerType d : handlers ) 
			d.setDocumentLocator(locator);
	}

	public void skippedEntity(String name) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.skippedEntity(name);
	}

	public void startDocument () throws SAXException { 
		for ( HandlerType d : handlers ) 
			d.startDocument();
    	}

	public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.startElement(uri,name,qName,atts);
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.startPrefixMapping(prefix,uri);
	}

	//---------------------------------------------------------------------
	// ErrorHandler Interface
	//---------------------------------------------------------------------

	public void error(SAXParseException e) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.error(e);
	}

	public void fatalError(SAXParseException e) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.fatalError(e);
	}

	public void warning(SAXParseException e)  throws SAXException {
		for ( HandlerType d : handlers ) 
			d.warning(e);
	}

	//---------------------------------------------------------------------
	// DTDHandler Interface
	//---------------------------------------------------------------------

	public void notationDecl(String name, String publicId, String systemId) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.notationDecl(name,publicId,systemId);
	}

	public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
		for ( HandlerType d : handlers ) 
			d.unparsedEntityDecl(name,publicId,systemId,notationName);
	}

}
