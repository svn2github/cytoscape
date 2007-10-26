package org.mskcc.pathway_commons.web_service;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.*;
import java.net.*;

import cytoscape.task.TaskMonitor;

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
    public static final String ARG_FORMAT = "output";

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
    public static final String CURRENT_VERSION = "2.0";

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
    private volatile GetMethod method;
    private boolean cancelledByUser = false;

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
     * Abort the Request.
     */
    public void abort() {
        if (method != null) {
            cancelledByUser = true;
            method.abort();
        }
    }

    /**
     * Connects to cPath Web Service API.
     *
     * @return XML Document.
     * @throws CPathException    Indicates Error connecting.
     * @throws EmptySetException All went all, but no results found.
     */
    public String connect (TaskMonitor taskMonitor) throws CPathException, EmptySetException {
        try {
            NameValuePair[] nvps = createNameValuePairs();
            String liveUrl = createURI(baseUrl, nvps);

            // Create an instance of HttpClient.
            HttpClient client = new HttpClient();

            // Create a method instance.
            method = new GetMethod(liveUrl);

            int statusCode = client.executeMethod(method);
            long contentLength = method.getResponseContentLength();

            //  Check status code
            checkHttpStatusCode(statusCode);

            //  Read in Content
            BufferedReader in = new BufferedReader (new InputStreamReader
                    (method.getResponseBodyAsStream()));
            StringBuffer buf = new StringBuffer();
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str + "\n");
            }
            in.close();
            
            String content = buf.toString();
            if (content.toLowerCase().indexOf(XML_TAG) >= 0) {
                //  Check for protocol errors.
                if (content.indexOf("<error>") >=0) {
                    StringReader reader = new StringReader(content);
                    SAXBuilder builder = new SAXBuilder();
                    Document document = builder.build(reader);
                    checkForErrors(document);
                }
                return content.trim();
            } else {
                return content.trim();
            }
        } catch (UnknownHostException e) {
            throw new CPathException(CPathException.ERROR_UNKNOWN_HOST, e);
        } catch (SocketException e) {
            if (cancelledByUser) {
                throw new CPathException(CPathException.ERROR_CANCELED_BY_USER, e);
            } else {
                throw new CPathException(CPathException.ERROR_NETWORK_IO, e);
            }
        } catch (IOException e) {
            throw new CPathException(CPathException.ERROR_NETWORK_IO, e);
        } catch (JDOMException e) {
            throw new CPathException(CPathException.ERROR_XML_PARSING, e);
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
            throw new CPathException(CPathException.ERROR_HTTP, "HTTP Status Code:  " + statusCode
                + ":  " + HttpStatus.getStatusText(statusCode));
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
                throw new CPathException(CPathException.ERROR_WEB_SERVICE_API, 
                    "Error Code:  " + errorCode + ", " + errorMsg);
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
