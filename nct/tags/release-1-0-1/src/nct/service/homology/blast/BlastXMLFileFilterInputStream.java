
//============================================================================
// 
//  file: BlastXMLFileFilterInputStream.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.service.homology.blast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This class extends a {@link java.io.FilterInputStream} and is used to 
 * filter out the extra xml version and DOCTYPE tags that come in Blast 
 * "XML" files for multiple queries and wraps the entire document in
 * blast_aggregate tags.  The result is an {@link java.io.InputStream}
 * that is well formed XML.  This class be used in place of a 
 * {@link java.io.FileInputStream} as follows.
 *
 * <pre>
 * InputStream is = new BlastXMLFileFilterInputStream(blastFileName, true);
 * 
 * // blast parser setup - see Biojava in Anger
 *
 * parser.parse(new InputSource(is));
 * </pre>
 *
 * @author Michael Smoot
 */
public class BlastXMLFileFilterInputStream extends FilterInputStream {

    /**
     * The tag used to wrap the multiple BlastOutput sections.
     */
    public static String wrappingTag = "blast_aggregate";

    /**
     * The string defining the regular expression used to identify
     * the xml version tags in the Blast ouptput.
     */
    public static String xmlRegEx = "^\\<\\?xml version\\=.+\\?\\>";

    /**
     * The string defining the regular expression used to identify
     * the DOCTYPE tags in the Blast ouptput.
     */
    public static String doctypeRegEx = "^\\<\\!DOCTYPE BlastOutput.+";

    /**
     * Whether or not to keep the first instances of xmlRegEx and
     * doctypeRegEx.  Different parsers are more or less tolerant.
     */
    protected boolean keepFirst;

    /**
     * Constructor.  
     * @param fileName The XML file name that needs to be processed.
     * @param keepFirst Whether or not to keep the first instance of
     * the doctype and xml version declarations.
     */
    public BlastXMLFileFilterInputStream(String fileName, boolean keepFirst) 
        throws IOException {
        this( new FileInputStream( fileName ), keepFirst );
    }

    /**
     * Constructor.  
     * @param ins The input stream of the XML file that needs to be
     * processed.
     * @param keepFirst Whether or not to keep the first instance of
     * the doctype and xml version declarations.
     */
    public BlastXMLFileFilterInputStream(InputStream ins, boolean keepFirst) 
        throws IOException {
        super(ins);

	this.keepFirst = keepFirst;

        BufferedReader read = new BufferedReader(new InputStreamReader(in));
        String line = "";
        boolean foundFirstXml = false;
        boolean foundFirstDoc = false;
        Matcher xmlMatcher = Pattern.compile(xmlRegEx).matcher("");
        Matcher docMatcher = Pattern.compile(doctypeRegEx).matcher("");
        StringBuffer stringBuf = new StringBuffer();
        String lineSep = System.getProperty("line.separator");

        // filter the inputstream
        while ( (line = read.readLine()) != null ) {

            xmlMatcher.reset(line);
            docMatcher.reset(line);
            
            if ( xmlMatcher.matches() ) {
                if ( !foundFirstXml ) {
                    foundFirstXml = true;

		    if ( keepFirst ) {
                    	stringBuf.append( line );
                    	stringBuf.append( lineSep ); 
		    }
                }

            } else if ( docMatcher.matches() ) {

                if ( !foundFirstDoc ) {
                    foundFirstDoc = true;

		    if ( keepFirst ) {
                    	stringBuf.append( line );
                    	stringBuf.append( lineSep ); 
		    }

                    // always add wrapper tag for the first one
                    stringBuf.append('<');
                    stringBuf.append(wrappingTag);
                    stringBuf.append('>');
                    stringBuf.append( lineSep ); 
                }
	

            } else {
                stringBuf.append( line );
                stringBuf.append( lineSep ); 
            }
        }

        // add wrapper tag at end
        stringBuf.append("</");
        stringBuf.append(wrappingTag);
        stringBuf.append('>');
        stringBuf.append( System.getProperty("line.separator") );

        // now stomp on the existing InputStream with our newly filtered stream
        in = new ByteArrayInputStream(stringBuf.toString().getBytes());
    }
}
