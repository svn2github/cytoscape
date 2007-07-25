/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.io;

import java.io.*;

import javax.xml.parsers.*;

import org.apache.log4j.Logger;
import org.xml.sax.*;


/**
 * Abstract Reader for XML format.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.22 $
 */
public abstract class AbstractXMLReader extends AbstractReader
    implements ContentHandler, EntityResolver {
    private static Logger logger = Logger.getLogger(AbstractXMLReader.class); 
    /** <code>true</code> when reading the first tag. */
    protected boolean firstTag;
    protected XMLReader reader;
    protected SAXParser parser;
    /**
     * Creates a new AbstractXMLReader object.
     *
     * @param in the InputStream
     * @param name a given name
     */
    public AbstractXMLReader(InputStream in, String name) {
        super(in, name);
    }

    /**
     * @see infovis.io.AbstractReader#load()
     */
    public boolean load() {
        firstTag = true;
        try {
            SAXParserFactory p = SAXParserFactory.newInstance();
            p.setFeature("http://xml.org/sax/features/validation", false);
            p.setValidating(false);
            parser = p.newSAXParser();
            reader = parser.getXMLReader();
            reader.setContentHandler(this);
            reader.setEntityResolver(this);
            reader.setDTDHandler(null);
            reader.setFeature("http://xml.org/sax/features/validation", false);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            InputSource source = new InputSource(getIn());
            if (getEncoding() != null) {
                source.setEncoding(getEncoding());
            }
            source.setSystemId(getName());
            reader.parse(source);
        } catch (FactoryConfigurationError e) {
            return false;
        }
        catch (SAXParseException e) {
            logger.error("Error parsing XML:"
                    +e.getPublicId()+":"
                    +e.getSystemId()+":"
                    +e.getLineNumber()+":"
                    +e.getColumnNumber()+":"
                    +e.getMessage(), e);
            return false;
        } catch(RuntimeException e) {
            logger.info("Wrong toplevel element");
            return false;
        } catch(Exception e) {
            logger.error("error reading XML ",e);
            return false;
        } finally {
            try {
                getIn().close();
            } catch (IOException e) {
                logger.error("While closing input", e);
            }
        }
        return true;
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
	throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String namespaceURI, String localName, String qName)
	throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] ch, int start, int length)
	throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
     */
    public void processingInstruction(String target, String data)
	throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
     */
    public void setDocumentLocator(Locator locator) {
    }

    /**
     * @see org.xml.sax.ContentHandler#skippedEntity(String)
     */
    public void skippedEntity(String name) throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts)
	throws SAXException {
    }

    /**
     * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
     */
    public void startPrefixMapping(String prefix, String uri)
	throws SAXException {
    }

    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(String, String)
     */
    public InputSource resolveEntity(String publicId, String systemId)
	throws SAXException, IOException {
        File file = new File(systemId);

        if (file.exists()) {
            return new InputSource(file.getAbsolutePath());
        }
        
        File dir = new File(getName());
        file = new File(dir.getParentFile(), systemId);
        if (file.exists()) {
            return new InputSource(file.getAbsolutePath());
        }
        
        return null;
    }
}
