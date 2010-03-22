/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
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
package org.cytoscape.coreplugin.cpath.protocol;

import org.cytoscape.coreplugin.cpath.model.CPathException;
import org.cytoscape.coreplugin.cpath.model.EmptySetException;
import org.cytoscape.coreplugin.cpath.util.CPathProperties;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.URLEncoder;

/**
 * Utility Class for Connecting to the cPath Web Service API.
 *
 * @author Ethan Cerami
 */
public class CPathProtocol {

    /**
     * The CPath Web Service Path.
     */
    public static final String WEB_SERVICE_PATH = "webservice.do";

    /**
     * Command Argument.
     */
    public static final String ARG_COMMAND = "cmd";

    /**
     * Query Argument.
     */
    public static final String ARG_QUERY = "q";

    /**
     * Format Argument.
     */
    public static final String ARG_FORMAT = "format";

    /**
     * Organism Argument.
     */
    public static final String ARG_ORGANISM = "organism";

    /**
     * Max Hits Argument.
     */
    public static final String ARG_MAX_HITS = "maxHits";

    /**
     * Start Index Argument.
     */
    public static final String ARG_START_INDEX = "startIndex";

    /**
     * Version Argument.
     */
    public static final String ARG_VERSION = "version";

    /**
     * PSI-MI XML Format.
     */
    public static final String FORMAT_XML = "xml";

    /**
     * BIOXPAX XML Format.
     */
    public static final String FORMAT_BIOPAX = "biopax";

    /**
     * Count Only Format.
     */
    public static final String FORMAT_COUNT_ONLY = "count_only";

    /**
     * Currently Supported Version.
     */
    public static final String CURRENT_VERSION = "1.0";

    /**
     * Get Interactions By Keyword.
     */
    public static final String COMMAND_GET_BY_KEYWORD =
            "get_by_keyword";

    /**
     * Get Interactions By Interactor Name / Xref.
     */
    public static final String COMMAND_GET_BY_INTERACTOR_XREF =
            "get_by_interactor_name_xref";

    /**
     * Get Interactions By Organism.
     */
    public static final String COMMAND_GET_BY_ORGANISM =
            "get_by_organism";

    /**
     * Get Interactions By Experiment Type.
     */
    public static final String COMMAND_GET_BY_EXPERIMENT_TYPE =
            "get_by_experiment_type";

    /**
     * Get Interactions by PMID.
     */
    public static final String COMMAND_GET_BY_PMID =
            "get_by_pmid";

    /**
     * Get Interactions By Database.
     */
    public static final String COMMAND_GET_BY_DATABASE =
            "get_by_database";

    /**
     * Get Top Level Pathway List.
     */
    public static final String COMMAND_GET_TOP_LEVEL_PATHWAY_LIST =
            "get_top_level_pathway_list";

    /**
     * Get Patheway record by CPath ID.
     */
    public static final String COMMAND_GET_RECORD_BY_CPATH_ID =
            "get_record_by_cpath_id";

    /**
     * Default for Max Hits.
     */
    public static final int DEFAULT_MAX_HITS = 10;

    /**
     * Not Specified
     */
    public static final int NOT_SPECIFIED = -1;

    /**
     * XML Tag.
     */
    private static final String XML_TAG = "xml";

    private String command;
    private String query;
    private int taxonomyId;
    private int maxHits;
    private int startIndex;
    private String format;
    private String baseUrl;

    /**
     * Constructor.
     */
    public CPathProtocol () {
        this.baseUrl = CPathProperties.getCPathUrl();
        this.maxHits = DEFAULT_MAX_HITS;
        this.taxonomyId = NOT_SPECIFIED;
    }

    /**
     * Sets the Command Argument.
     *
     * @param command Command Argument.
     */
    public void setCommand (String command) {
        this.command = command;
    }

    /**
     * Sets the Query Argument.
     *
     * @param query Query Argument.
     */
    public void setQuery (String query) {
        this.query = query;
    }

    /**
     * Sets the Format Argument.
     *
     * @param format Format Argument.
     */
    public void setFormat (String format) {
        this.format = format;
    }

    /**
     * Sets the Organism Argument.
     *
     * @param taxonomyId NCBI TaxonomyID
     */
    public void setOrganism (int taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    /**
     * Sets the MaxHits Argument.
     *
     * @param maxHits Max Number of Hits.
     */
    public void setMaxHits (int maxHits) {
        this.maxHits = maxHits;
    }

    /**
     * Sets the StartIndex Argument.
     *
     * @param startIndex StartIndex Argument.
     */
    public void setStartIndex (int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Connects to cPath Web Service API.
     *
     * @return XML Document.
     * @throws CPathException    Indicates Error connecting.
     * @throws EmptySetException All went all, but no results found.
     */
    public String connect () throws CPathException, EmptySetException {
        try {
            NameValuePair[] nvps = createNameValuePairs();
            String liveUrl = createURI(baseUrl, nvps);
            URL cPathUrl = new URL(liveUrl);
            HttpURLConnection cPathConnection = (HttpURLConnection) cPathUrl.openConnection();

            //  Check status code
            int statusCode = cPathConnection.getResponseCode();
            checkHttpStatusCode(statusCode);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(cPathUrl.openStream()));
            StringBuffer buf = new StringBuffer();
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str + "\n");
            }
            in.close();

            //  Check for errors
            String content = buf.toString();
            if (content.toLowerCase().indexOf(XML_TAG) >= 0) {
                //  Check for protocol errors.
                if (content.indexOf("<error>") >=0) {
                    StringReader reader = new StringReader(content);
                    SAXBuilder builder = new SAXBuilder();
                    Document document = builder.build(reader);
                    checkForErrors(document);
                }
                return content;
            } else {
                return content.trim();
            }
        } catch (UnknownHostException e) {
            String msg = "Network error occurred while tring to connect to "
                    + "the cPath Web Service.  Could not find server:  "
                    + e.getMessage()
                    + ". Please check your server and network settings, "
                    + "and try again.";
            throw new CPathException(msg, e);
        } catch (IOException e) {
            String msg = "Network error occurred while trying to "
                    + "connect to the cPath Web Service.  "
                    + "Please check your server and network settings, "
                    + "and try again.";
            throw new CPathException(msg, e);
        } catch (JDOMException e) {
            String msg = "Error occurred while trying to parse XML results "
                    + "retrieved from the cPath Web Service.  "
                    + "Please check your server and network settings, "
                    + "and try again.";
            throw new CPathException(msg, e);
        }
    }

    /**
     * Gets URI
     *
     * @return URI.
     */
    public String getURI () {
        NameValuePair[] nvps = createNameValuePairs();
        return createURI(baseUrl, nvps);
    }

    /**
     * Gets URI of cPath Call.
     *
     * @return URI for cPath Call.
     */
    private String createURI (String url, NameValuePair[] nvps) {
        StringBuffer buf = new StringBuffer(url);
        buf.append("?");
        for (int i = 0; i < nvps.length; i++) {
            buf.append(nvps[i].getName() + "=" + nvps[i].getValue() + "&");
        }
        return buf.toString();
    }

    private NameValuePair[] createNameValuePairs () {
        NameValuePair nvps[] = null;
        if (taxonomyId == NOT_SPECIFIED) {
            nvps = new NameValuePair[6];
        } else {
            nvps = new NameValuePair[7];
            nvps[6] = new NameValuePair(ARG_ORGANISM,
                    Integer.toString(taxonomyId));
        }
        nvps[0] = new NameValuePair(ARG_COMMAND, command);
        nvps[1] = new NameValuePair(ARG_QUERY, query);
        nvps[2] = new NameValuePair(ARG_FORMAT, format);
        nvps[3] = new NameValuePair(ARG_VERSION, CPathProtocol.CURRENT_VERSION);
        nvps[4] = new NameValuePair(ARG_MAX_HITS, Integer.toString(maxHits));
        nvps[5] = new NameValuePair(ARG_START_INDEX,
                Integer.toString(startIndex));
        return nvps;
    }

    private void checkHttpStatusCode (int statusCode)
            throws CPathException {
        if (statusCode != 200) {
            String msg = new String("Error Connecting to cPath "
                    + "Web Service (Details:  HTTP Status Code:  "
                    + statusCode + ")");
            throw new CPathException(msg);
        }
    }

    private void checkForErrors (Document document)
            throws CPathException, EmptySetException {
        Element element = document.getRootElement();
        String name = element.getName();
        if (name.equals("error")) {
            String errorCode = element.getChild("error_code").getText();
            String errorMsg = element.getChild("error_msg").getText();
            if (errorCode.equals("460")) {
                throw new EmptySetException();
            } else {
                String msg = new String("Error Connecting to cPath "
                        + "Web Service (Error Code:  " + errorCode
                        + ", Error Message:  " + errorMsg
                        + ")");
                throw new CPathException(msg);
            }
        }
    }
}

/**
 * Name Value Pair.
 *
 * @author Ethan Cerami.
 */
class NameValuePair {
    private String name;
    private String value;

    /**
     * Constructor.
     * @param name  name.
     * @param value value.
     */
    public NameValuePair (String name, String value) {
        this.name = name;
		try {
			this.value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			this.value = value;
		}
	}

    /**
     * Gets name.
     * @return name.
     */
    public String getName () {
        return name;
    }

    /**
     * Gets value.
     * @return value.
     */
    public String getValue () {
        return value;
    }
}
